package cz.cuni.mff.simekjan.generator;

import cz.cuni.mff.simekjan.application.column.ColumnEntity;
import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.application.relationship.RelationshipEntity;
import cz.cuni.mff.simekjan.application.table.TableEntity;
import cz.cuni.mff.simekjan.generator.exporter.DataExporter;
import cz.cuni.mff.simekjan.generator.orderer.DataGeneratorOrderer;
import cz.cuni.mff.simekjan.model.ColumnDatatype;
import cz.cuni.mff.simekjan.model.GenerateBenchmarkCommand;
import org.neo4j.driver.types.Relationship;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Generates records from dataset metadata and coordinates their serialization.
 */
@Service
public class DatasetGenerator {

    private static final int BATCH_SIZE = 5;

    private final Map<GenerateBenchmarkCommand.FormatsEnum, DataExporter> exporters;
    private final DataGeneratorOrderer orderer = new DataGeneratorOrderer();
    private final DataProvider dataProvider = new DataProvider(42L);

    public DatasetGenerator(List<DataExporter> exporters) {
        Map<GenerateBenchmarkCommand.FormatsEnum, DataExporter> exportersByFormat =
            new EnumMap<>(GenerateBenchmarkCommand.FormatsEnum.class);
        for (DataExporter exporter : exporters) {
            DataExporter previous = exportersByFormat.putIfAbsent(exporter.getFormat(), exporter);
            if (previous != null) {
                throw new IllegalStateException("Multiple data exporters registered for " + exporter.getFormat());
            }
        }
        this.exporters = Map.copyOf(exportersByFormat);
    }

    /**
     * Main entry point for generating dataset records and serializing them to the specified format.
     *
     * @param dataset The dataset metadata to generate records for.
     * @param formats The serialization formats to use.
     */
    public void generate(DatasetEntity dataset, List<GenerateBenchmarkCommand.FormatsEnum> formats) {
        List<DataExporter> exporters = formats.stream()
            .map(format -> {
                DataExporter exporter = this.exporters.get(format);
                if (exporter == null) {
                    throw new IllegalArgumentException("No data exporter registered for " + format);
                }
                return exporter;
            })
            .toList();

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

        // TODO: These items are possible to generate in parallel due to being independent of each other
        for (UUID itemId : orderer.nextItemBatch()) {
            generateNextItem(itemId, exporters, dataset);
        }
    }

    /**
     * Generates next table to be exported (either value table or relationship table)
     */
    private void generateNextItem(UUID itemId, List<DataExporter> exporters, DatasetEntity dataset) {

        TableEntity table = dataset.getTables().stream()
            .filter(t -> t.getId().equals(itemId))
            .findFirst()
            .orElse(null);

        if (table != null) {
            generateTable(table, exporters);
            return;
        }

        RelationshipEntity relationship = dataset.getRelationships().stream()
            .filter(r -> r.getId().equals(itemId))
            .findFirst()
            .orElse(null);

        if (relationship != null) {
            generateRelationship(relationship, exporters);
            return;
        }

        throw new IllegalStateException("Item ID " + itemId + " is neither a table nor a relationship in the dataset");
    }

    /**
     * Generates all rows for a given table in batches and writes them to the specified exporters.
     *
     * @param table     The table entity to generate data for.
     * @param exporters The list of data exporters to write the generated data to.
     */
    private void generateTable(TableEntity table, List<DataExporter> exporters) {
        int rowsRemaining = table.getNumberOfRows();
        while (rowsRemaining > 0) {
            int batchSize = Math.min(BATCH_SIZE, rowsRemaining);
            generateTableBatch(table, batchSize, exporters);
            rowsRemaining -= batchSize;
        }
    }

    /**
     * Generates all rows for a given relationship in batches and writes them to the specified exporters.
     *
     * @param relationship The relationship entity to generate data for.
     * @param exporters    The list of data exporters to write the generated data to.
     */
    private void generateRelationship(RelationshipEntity relationship, List<DataExporter> exporters) {
        // TODO: Add number of rows to relationship entity and generate accordingly
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
     * @param table     The table entity to generate data for.
     * @param batchSize The number of rows to generate in this batch.
     * @param exporters The list of data exporters to write the generated data to.
     */
    private void generateTableBatch(TableEntity table, int batchSize, List<DataExporter> exporters) {
        List<String> columnNames = table.getColumns().stream()
            .map(ColumnEntity::getName)
            .toList();

        List<List<Object>> rows = new java.util.ArrayList<>();

        for (int i = 0; i < batchSize; i++) {
            List<Object> row = new java.util.ArrayList<>();
            for (ColumnEntity column : table.getColumns()) {
                // TODO: add support for generating values based on value distribution and constraints
                ColumnDatatype datatype = column.getDatatype();
                Object value = generateValue(datatype);
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
