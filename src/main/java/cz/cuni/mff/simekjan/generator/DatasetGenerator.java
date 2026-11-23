package cz.cuni.mff.simekjan.generator;

import cz.cuni.mff.simekjan.application.column.ColumnEntity;
import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.application.relationship.RelationshipEntity;
import cz.cuni.mff.simekjan.application.table.TableEntity;
import cz.cuni.mff.simekjan.generator.exporter.DataExporter;
import cz.cuni.mff.simekjan.generator.orderer.DataGeneratorOrderer;
import cz.cuni.mff.simekjan.generator.reader.DataReader;
import cz.cuni.mff.simekjan.model.ColumnDatatype;
import cz.cuni.mff.simekjan.model.GenerateBenchmarkCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static cz.cuni.mff.simekjan.model.RelationshipType.*;

/**
 * Generates records from dataset metadata and coordinates their serialization.
 */
@Service
public class DatasetGenerator {

    private static final int BATCH_SIZE = 1000;

    private final Map<GenerateBenchmarkCommand.FormatsEnum, DataExporter> exporters;
    private final Map<GenerateBenchmarkCommand.FormatsEnum, DataReader> readers;
    private final DataGeneratorOrderer orderer = new DataGeneratorOrderer();
    private final DataProvider dataProvider = new DataProvider(42L);

    public DatasetGenerator(List<DataExporter> exporters) {
        this(exporters, List.of());
    }

    @Autowired
    public DatasetGenerator(List<DataExporter> exporters, List<DataReader> readers) {
        Map<GenerateBenchmarkCommand.FormatsEnum, DataExporter> exportersByFormat =
            new EnumMap<>(GenerateBenchmarkCommand.FormatsEnum.class);
        for (DataExporter exporter : exporters) {
            DataExporter previous = exportersByFormat.putIfAbsent(exporter.getFormat(), exporter);
            if (previous != null) {
                throw new IllegalStateException("Multiple data exporters registered for " + exporter.getFormat());
            }
        }
        this.exporters = Map.copyOf(exportersByFormat);
        Map<GenerateBenchmarkCommand.FormatsEnum, DataReader> readersByFormat =
            new EnumMap<>(GenerateBenchmarkCommand.FormatsEnum.class);
        for (DataReader reader : readers) {
            if (readersByFormat.putIfAbsent(reader.getFormat(), reader) != null) {
                throw new IllegalStateException("Multiple data readers registered for " + reader.getFormat());
            }
        }
        this.readers = Map.copyOf(readersByFormat);
    }

    /**
     * Main entry point for generating dataset records and serializing them to the specified format.
     *
     * @param dataset The dataset metadata to generate records for.
     * @param formats The serialization formats to use.
     */
    public void generate(DatasetEntity dataset, List<GenerateBenchmarkCommand.FormatsEnum> formats) {

        if (formats.isEmpty()) {
            throw new IllegalArgumentException("No formats specified for data export");
        }

        List<DataExporter> exporters = formats.stream()
            .map(format -> {
                DataExporter exporter = this.exporters.get(format);
                if (exporter == null) {
                    throw new IllegalArgumentException("No data exporter registered for " + format);
                }
                return exporter;
            })
            .toList();

        // TODO: remove when more readers are implemented, currently only CSV is supported for reading dependencies
        if (!readers.containsKey(GenerateBenchmarkCommand.FormatsEnum.CSV)) {
            throw new IllegalStateException("No CSV data reader registered for table dependencies");
        }

        // Analyze the dataset and Relationships and construct a directed graph for generation order
        orderer.decideOrder(dataset);

        // Generate by epochs (item batches), based on fulfilled dependencies
        while (orderer.hasNextItemBatch()) {
            generateItemBatch(exporters, dataset);
        }
    }

    /**
     * Generates all items (tables and relationships) in a batch (tables possible to generate at once)
     */
    private void generateItemBatch(List<DataExporter> exporters, DatasetEntity dataset) {

        System.out.println("Generating next item batch:");

        // TODO: These items are possible to generate in parallel due to being independent of each other
        for (UUID itemId : orderer.nextItemBatch()) {
            generateNextItem(itemId, exporters, dataset);
        }
    }

    /**
     * Generates next table to be exported (either value table or relationship table)
     *
     * @param itemId    The ID of the item (table or relationship) to generate.
     * @param exporters The list of data exporters to write the generated data to.
     * @param dataset   The dataset entity containing the tables and relationships.
     */
    private void generateNextItem(UUID itemId, List<DataExporter> exporters, DatasetEntity dataset) {

        TableEntity table = dataset.getTables().stream()
            .filter(t -> t.getId().equals(itemId))
            .findFirst()
            .orElse(null);

        if (table != null) {

            System.out.println("Generating next table: " + table.getName());

            // Get dependencies from relationships and corresponding tables
            List<RelationshipEntity> dependencies = dataset.getRelationships().stream()
                .filter(r ->
                    !Objects.equals(r.getSourceColumn().getTable().getId(), r.getTargetColumn().getTable().getId())
                        && ((r.getRelationshipType() == ONE_TO_MANY && r.getTargetColumn().getTable().equals(table)) ||
                        (r.getRelationshipType() == MANY_TO_ONE && r.getSourceColumn().getTable().equals(table))))
                .toList();

            generateTable(table, exporters, dependencies);
            return;
        }

        RelationshipEntity relationship = dataset.getRelationships().stream()
            .filter(r -> r.getRelationshipType() != MANY_TO_ONE && r.getRelationshipType() != ONE_TO_MANY)
            .filter(r -> r.getId().equals(itemId))
            .findFirst()
            .orElse(null);

        if (relationship != null) {

            System.out.println("Generating next relationship: " + relationship.getName());

            generateRelationship(relationship, exporters);
        }
    }

    /**
     * Generates all rows for a given table in batches and writes them to the specified exporters.
     *
     * @param table        The table entity to generate data for.
     * @param exporters    The list of data exporters to write the generated data to.
     * @param dependencies The list of relationship dependencies for the table.
     */
    private void generateTable(TableEntity table, List<DataExporter> exporters, List<RelationshipEntity> dependencies) {

        List<TableDependency> tableDependencies = new ArrayList<>();
        for (RelationshipEntity relationship : dependencies) {
            ColumnEntity dependent = relationship.getRelationshipType() == ONE_TO_MANY
                ? relationship.getTargetColumn() : relationship.getSourceColumn();
            ColumnEntity referenced = relationship.getRelationshipType() == ONE_TO_MANY
                ? relationship.getSourceColumn() : relationship.getTargetColumn();
            if (!table.getColumns().contains(dependent)) {
                throw new IllegalStateException("Relationship " + relationship.getName() + " references a missing column");
            }
            if (tableDependencies.stream().anyMatch(dependency -> dependency.dependent().equals(dependent))) {
                throw new IllegalStateException("Multiple dependencies for column " + dependent.getName());
            }
            if (dependent.getDatatype() != referenced.getDatatype()) {
                throw new IllegalStateException("Mismatched column types in relationship " + relationship.getName());
            }
            Integer referencedRows = referenced.getTable().getNumberOfRows();
            if (table.getNumberOfRows() > 0 && (referencedRows == null || referencedRows <= 0)) {
                throw new IllegalStateException("No referenced rows for relationship " + relationship.getName());
            }
            if (referencedRows != null && referencedRows > table.getNumberOfRows()) {
                throw new IllegalStateException("More referenced rows than dependent rows for relationship "
                    + relationship.getName());
            }
            tableDependencies.add(new TableDependency(dependent, referenced, referencedRows));
        }

        int rowsRemaining = table.getNumberOfRows();
        int rowsGenerated = 0;
        while (rowsRemaining > 0) {
            int batchSize = Math.min(BATCH_SIZE, rowsRemaining);
            Map<ColumnEntity, List<Object>> dependencyValues = new HashMap<>();
            for (TableDependency dependency : tableDependencies) {
                dependencyValues.put(dependency.dependent(),
                    readDependencyBatch(dependency, rowsGenerated, batchSize, table.getNumberOfRows()));
            }
            generateTableBatch(table, batchSize, exporters, dependencyValues);
            rowsRemaining -= batchSize;
            rowsGenerated += batchSize;
        }
    }

    private record TableDependency(ColumnEntity dependent, ColumnEntity referenced, Integer referencedRows) {
    }

    private List<Object> readDependencyBatch(TableDependency dependency, int startRow, int batchSize, int totalRows) {
        // TODO: resolve only using CSV reader
        DataReader reader = readers.get(GenerateBenchmarkCommand.FormatsEnum.CSV);
        ColumnEntity referenced = dependency.referenced();
        long firstIndex = (long) startRow * dependency.referencedRows() / totalRows;
        long lastIndex = ((long) startRow + batchSize - 1) * dependency.referencedRows() / totalRows;
        int count = Math.toIntExact(lastIndex - firstIndex + 1);
        List<String> referencedValues;
        try {
            referencedValues = reader.readColumn(referenced.getTable().getName(), referenced.getName(), firstIndex, count);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read column '" + referenced.getName()
                + "' from table '" + referenced.getTable().getName() + "'", e);
        }
        if (referencedValues.size() != count) {
            throw new IllegalStateException("Not enough rows in referenced table '" + referenced.getTable().getName() + "'");
        }

        List<Object> values = new ArrayList<>(batchSize);
        for (int row = 0; row < batchSize; row++) {
            long index = ((long) startRow + row) * dependency.referencedRows() / totalRows;
            values.add(parseDependencyValue(referencedValues.get(Math.toIntExact(index - firstIndex)),
                dependency.dependent()));
        }
        return values;
    }

    private Object parseDependencyValue(String value, ColumnEntity column) {
        try {
            return switch (column.getDatatype()) {
                case STRING -> value;
                case BOOLEAN -> {
                    if (!value.equals("true") && !value.equals("false")) {
                        throw new IllegalArgumentException("Invalid boolean: " + value);
                    }
                    yield Boolean.valueOf(value);
                }
                case INTEGER -> Integer.valueOf(value);
                case FLOAT -> Float.valueOf(value);
                case DATE -> LocalDate.parse(value);
            };
        } catch (IllegalArgumentException | java.time.DateTimeException e) {
            throw new IllegalStateException("Invalid referenced value for column '" + column.getName() + "'", e);
        }
    }

    /**
     * Generates all rows for a given relationship in batches and writes them to the specified exporters.
     *
     * @param relationship The relationship entity to generate data for.
     * @param exporters    The list of data exporters to write the generated data to.
     */
    private void generateRelationship(RelationshipEntity relationship, List<DataExporter> exporters) {

        if (relationship.getRelationshipType() == null) {
            throw new IllegalStateException("Relationship " + relationship.getName() + " has no defined relationship type");
        }
        if (relationship.getRelationshipType() == MANY_TO_ONE) {
            throw new IllegalStateException("Many-to-one relationships are generated with tables");
        }

        if (relationship.getRelationshipType() == MANY_TO_MANY) {
            // TODO: many-to-many relationships have only general distribution defined
        }

        // if column is not optional, on "one" side of any relationship => "number of rows" == number of rows in the "one" side table

        // if one-to-one and both columns are required then both tables need to have the same number of rows

        //

        /*
            int rowsRemaining = relationship.getNumberOfRows();
            while (rowsRemaining > 0) {
                int batchSize = Math.min(BATCH_SIZE, rowsRemaining);
                generateRelationshipBatch(); // TODO: Implement this method to generate and export a batch of data
                rowsRemaining -= batchSize;
            }
        */
    }

    /**
     * Generates part of a table's data (one batch) and writes it to the specified exporters.
     *
     * @param table            The table entity to generate data for.
     * @param batchSize        The number of rows to generate in this batch.
     * @param exporters        The list of data exporters to write the generated data to.
     * @param dependencyValues referenced values for dependent columns, in row order.
     */
    private void generateTableBatch(TableEntity table, int batchSize, List<DataExporter> exporters,
                                    Map<ColumnEntity, List<Object>> dependencyValues) {
        List<String> columnNames = table.getColumns().stream()
            .map(ColumnEntity::getName)
            .toList();

        List<List<Object>> rows = new java.util.ArrayList<>();

        for (int i = 0; i < batchSize; i++) {
            List<Object> row = new java.util.ArrayList<>();
            for (ColumnEntity column : table.getColumns()) {
                // TODO: add support for generating values based on value distribution and constraints
                ColumnDatatype datatype = column.getDatatype();
                List<Object> referencedValues = dependencyValues.get(column);
                Object value = referencedValues == null ? generateValue(datatype) : referencedValues.get(i);
                row.add(value);
            }
            rows.add(row);
        }

        GeneratedTable generatedTable = new GeneratedTable(
            java.util.UUID.randomUUID(),
            table.getName(),
            columnNames,
            rows
        );

        for (DataExporter exporter : exporters) {
            try {
                exporter.write(generatedTable);
            } catch (IOException e) {
                throw new UncheckedIOException(
                    "Failed to export table '" + table.getName() + "' as " + exporter.getFormat(),
                    e
                );
            }
        }
    }

    /**
     * Generates part of a relationship's data (one batch) and writes it to the specified exporters.
     *
     * @param relationship The relationship entity to generate data for.
     * @param batchSize    The number of rows to generate in this batch.
     * @param exporters    The list of data exporters to write the generated data to.
     */
    private void generateRelationshipBatch(RelationshipEntity relationship, int batchSize, List<DataExporter> exporters) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Generates a single value for a given column datatype using the provided data provider.
     *
     * @param datatype Datatype to generate
     * @return Generated value for the specified datatype
     */
    private Object generateValue(ColumnDatatype datatype) {
        return switch (datatype) {
            case STRING -> dataProvider.generateStringType();
            case BOOLEAN -> dataProvider.generateBooleanType();
            case INTEGER -> dataProvider.generateIntegerType();
            case FLOAT -> dataProvider.generateFloatType();
            case DATE -> dataProvider.generateDateType();
        };
    }
}
