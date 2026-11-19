package cz.cuni.mff.simekjan.generator;

import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.generator.exporter.DataExporter;
import cz.cuni.mff.simekjan.generator.orderer.DataGeneratorOrderer;
import cz.cuni.mff.simekjan.model.ColumnDatatype;
import cz.cuni.mff.simekjan.model.GenerateBenchmarkCommand;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Generates records from dataset metadata and coordinates their serialization.
 */
@Service
public class DatasetGenerator {

    private final Map<GenerateBenchmarkCommand.FormatsEnum, DataExporter> exporters;
    private final DataGeneratorOrderer orderer = new DataGeneratorOrderer();

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


        // Generate tables in order determined, possibly in parallel by whole order equivalence batch

        DataProvider dataProvider = new DataProvider(dataset.getId().getMostSignificantBits());

        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Generates all items (tables and relationships) in a batch (tables possible to generate at once)
     */
    private void generateItemBatch() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Generates next table to be exported (either value table or relationship table)
     */
    private void generateNextItem() {
        throw new UnsupportedOperationException("Not implemented yet");
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
     * @param datatype     Datatype to generate
     * @param dataProvider Data provider to use for generating values
     * @return Generated value for the specified datatype
     */
    private Object generateValue(ColumnDatatype datatype, DataProvider dataProvider) {
        return switch (datatype) {
            case STRING -> dataProvider.generateStringType();
            case BOOLEAN -> dataProvider.generateBooleanType();
            case INTEGER -> dataProvider.generateIntegerType();
            case FLOAT -> dataProvider.generateFloatType();
            case DATE -> dataProvider.generateDateType();
        };
    }
}
