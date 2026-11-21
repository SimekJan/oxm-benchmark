package cz.cuni.mff.simekjan.generator;

import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.application.relationship.RelationshipEntity;
import cz.cuni.mff.simekjan.application.table.TableEntity;
import cz.cuni.mff.simekjan.generator.exporter.DataExporter;
import cz.cuni.mff.simekjan.generator.orderer.DataGeneratorOrderer;
import cz.cuni.mff.simekjan.model.ColumnDatatype;
import cz.cuni.mff.simekjan.model.GenerateBenchmarkCommand;
import org.neo4j.driver.types.Relationship;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Generates records from dataset metadata and coordinates their serialization.
 */
@Service
public class DatasetGenerator {

    private static final int BATCH_SIZE = 1000;

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

        // TODO: decide if table or relationship table and generate accordingly

        TableEntity table = dataset.getTables().stream()
            .filter(t -> t.getId().equals(itemId))
            .findFirst()
            .orElse(null);

        if (table != null) {
            int rowsRemaining = table.getNumberOfRows();
            while (rowsRemaining > 0) {
                int batchSize = Math.min(BATCH_SIZE, rowsRemaining);
                generateBatch(); // TODO: Implement this method to generate and export a batch of data
                rowsRemaining -= batchSize;
            }
            return;
        }

        RelationshipEntity relationship = dataset.getRelationships().stream()
            .filter(r -> r.getId().equals(itemId))
            .findFirst()
            .orElse(null);

        if (relationship == null) {
            throw new IllegalStateException("Item ID " + itemId + " is neither a table nor a relationship in the dataset");
        }

        // TODO: Add number of rows to relationship entity and generate accordingly
        /*
        int rowsRemaining = relationship.getNumberOfRows();
        while (rowsRemaining > 0) {
            int batchSize = Math.min(BATCH_SIZE, rowsRemaining);
            generateBatch(); // TODO: Implement this method to generate and export a batch of data
            rowsRemaining -= batchSize;
        }
        */
    }

    /**
     * Generates part of a table's data (one batch) and writes it to the specified exporters.
     */
    private static void generateBatch() {
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
