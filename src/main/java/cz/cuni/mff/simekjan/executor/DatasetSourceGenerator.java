package cz.cuni.mff.simekjan.executor;

import cz.cuni.mff.simekjan.model.DatasetSchema;
import cz.cuni.mff.simekjan.model.DatasetSchemaRelationshipsInner;
import cz.cuni.mff.simekjan.model.DatasetSchemaTablesInner;
import cz.cuni.mff.simekjan.model.DatasetSchemaTablesInnerColumnsInner;

import javax.lang.model.SourceVersion;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

final class DatasetSourceGenerator {

    List<String> generate(UUID datasetId, DatasetSchema schema, Path sourceRoot) throws IOException {
        validateSchema(schema);
        String packageName = "generated.dataset.d" + datasetId.toString().replace("-", "");
        Path packageDirectory = sourceRoot.resolve(packageName.replace('.', '/'));
        Files.createDirectories(packageDirectory);

        Map<String, String> classNamesByTable = new LinkedHashMap<>();
        for (DatasetSchemaTablesInner table : schema.getTables()) {
            classNamesByTable.put(table.getName(), className(table.getName()));
        }

        List<String> generatedClasses = new ArrayList<>();
        for (DatasetSchemaTablesInner table : schema.getTables()) {
            String className = classNamesByTable.get(table.getName());
            String source = createSource(packageName, className, table, schema, classNamesByTable);
            Files.writeString(packageDirectory.resolve(className + ".java"), source);
            generatedClasses.add(packageName + "." + className);
        }
        return generatedClasses;
    }

    private static String createSource(
        String packageName,
        String className,
        DatasetSchemaTablesInner table,
        DatasetSchema schema,
        Map<String, String> classNamesByTable
    ) {
        Map<String, String> properties = new LinkedHashMap<>();
        for (DatasetSchemaTablesInnerColumnsInner column : table.getColumns()) {
            properties.put(propertyName(column.getName()), javaType(column.getDatatype()));
        }

        List<DatasetSchemaRelationshipsInner> tableRelationships = schema.getRelationships().stream()
            .filter(relationship -> table.getName().equals(relationship.getFromTable()))
            .toList();
        for (DatasetSchemaRelationshipsInner relationship : tableRelationships) {
            String propertyName = propertyName(relationship.getName());
            String targetType = classNamesByTable.get(relationship.getToTable());
            boolean collection = relationship.getRelationshipType()
                == DatasetSchemaRelationshipsInner.RelationshipTypeEnum.ONE_TO_MANY
                || relationship.getRelationshipType()
                == DatasetSchemaRelationshipsInner.RelationshipTypeEnum.MANY_TO_MANY;
            properties.put(propertyName, collection ? "java.util.List<" + targetType + ">" : targetType);
        }

        StringBuilder source = new StringBuilder();
        source.append("package ").append(packageName).append(";\n\n")
            .append("public class ").append(className).append(" {\n");
        properties.forEach((property, type) -> {
            source.append("    private ").append(type).append(' ').append(property);
            if (type.startsWith("java.util.List<")) {
                source.append(" = new java.util.ArrayList<>()");
            }
            source.append(";\n");
        });
        source.append('\n');

        properties.forEach((property, type) -> {
            String suffix = Character.toUpperCase(property.charAt(0)) + property.substring(1);
            source.append("    public ").append(type).append(" get").append(suffix)
                .append("() { return ").append(property).append("; }\n")
                .append("    public void set").append(suffix).append('(').append(type).append(' ')
                .append(property).append(") { this.").append(property).append(" = ").append(property)
                .append("; }\n");
        });
        source.append("}\n");
        return source.toString();
    }

    static void validateSchema(DatasetSchema schema) {
        if (schema == null || schema.getTables() == null || schema.getTables().isEmpty()) {
            throw new IllegalArgumentException("Dataset schema must contain at least one table");
        }

        Map<String, DatasetSchemaTablesInner> tables = new HashMap<>();
        Set<String> classNames = new HashSet<>();
        for (DatasetSchemaTablesInner table : schema.getTables()) {
            if (table.getName() == null || table.getName().isBlank()) {
                throw new IllegalArgumentException("Table names must not be blank");
            }
            if (tables.putIfAbsent(table.getName(), table) != null) {
                throw new IllegalArgumentException("Duplicate table name: " + table.getName());
            }
            if (!classNames.add(className(table.getName()))) {
                throw new IllegalArgumentException("Table names collide after Java identifier conversion");
            }
            if (table.getColumns() == null || table.getColumns().isEmpty()) {
                throw new IllegalArgumentException("Each table must contain at least one column");
            }

            Set<String> propertyNames = new HashSet<>();
            for (DatasetSchemaTablesInnerColumnsInner column : table.getColumns()) {
                if (column.getName() == null || column.getName().isBlank() || column.getDatatype() == null) {
                    throw new IllegalArgumentException("Column names and data types are required");
                }
                if (!propertyNames.add(propertyName(column.getName()))) {
                    throw new IllegalArgumentException(
                        "Column names collide after Java identifier conversion in table " + table.getName()
                    );
                }
            }
        }

        if (schema.getRelationships() == null) {
            return;
        }
        for (DatasetSchemaRelationshipsInner relationship : schema.getRelationships()) {
            if (relationship.getRelationshipType() == null) {
                throw new IllegalArgumentException("Relationship type is required");
            }
            DatasetSchemaTablesInner fromTable = tables.get(relationship.getFromTable());
            DatasetSchemaTablesInner toTable = tables.get(relationship.getToTable());
            if (fromTable == null || toTable == null) {
                throw new IllegalArgumentException("Relationship references an unknown table");
            }
            if (!containsColumn(fromTable, relationship.getFromColumn())
                || !containsColumn(toTable, relationship.getToColumn())) {
                throw new IllegalArgumentException("Relationship references an unknown column");
            }
            if (relationship.getName() == null || relationship.getName().isBlank()) {
                throw new IllegalArgumentException("Relationship names must not be blank");
            }
            Set<String> usedProperties = new HashSet<>();
            fromTable.getColumns().forEach(column -> usedProperties.add(propertyName(column.getName())));
            boolean relationshipNameIsUsed = schema.getRelationships().stream()
                .filter(candidate -> fromTable.getName().equals(candidate.getFromTable()))
                .filter(candidate -> candidate != relationship)
                .anyMatch(candidate -> propertyName(candidate.getName()).equals(propertyName(relationship.getName())));
            if (usedProperties.contains(propertyName(relationship.getName())) || relationshipNameIsUsed) {
                throw new IllegalArgumentException(
                    "Relationship property conflicts with another property in table " + fromTable.getName()
                );
            }
        }
    }

    private static boolean containsColumn(DatasetSchemaTablesInner table, String columnName) {
        return table.getColumns().stream().anyMatch(column -> column.getName().equals(columnName));
    }

    private static String javaType(DatasetSchemaTablesInnerColumnsInner.DatatypeEnum datatype) {
        return switch (datatype) {
            case STRING -> "String";
            case BOOLEAN -> "Boolean";
            case INTEGER -> "Long";
            case FLOAT -> "Double";
            case DATE -> "java.time.LocalDate";
        };
    }

    private static String className(String name) {
        return identifier(name, true);
    }

    private static String propertyName(String name) {
        String identifier = identifier(name, false);
        return Character.toLowerCase(identifier.charAt(0)) + identifier.substring(1);
    }

    private static String identifier(String name, boolean capitalize) {
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = capitalize;
        for (int index = 0; index < name.length(); index++) {
            char character = name.charAt(index);
            if (!Character.isLetterOrDigit(character) && character != '_') {
                capitalizeNext = true;
                continue;
            }
            if (result.isEmpty() && !Character.isJavaIdentifierStart(character)) {
                result.append('_');
            }
            result.append(capitalizeNext ? Character.toUpperCase(character) : character);
            capitalizeNext = false;
        }
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Name does not produce a Java identifier: " + name);
        }
        String identifier = result.toString();
        if (!SourceVersion.isIdentifier(identifier) || SourceVersion.isKeyword(identifier)) {
            identifier = "_" + identifier;
        }
        return identifier;
    }
}
