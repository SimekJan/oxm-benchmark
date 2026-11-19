package cz.cuni.mff.simekjan.generator;

import cz.cuni.mff.simekjan.application.column.ColumnEntity;
import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.application.table.TableEntity;
import cz.cuni.mff.simekjan.generator.exporter.DataExporter;
import cz.cuni.mff.simekjan.model.ColumnDatatype;
import cz.cuni.mff.simekjan.model.GenerateBenchmarkCommand;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Generates records from dataset metadata and coordinates their serialization.
 */
@Service
public class DatasetGenerator {

    private final Map<GenerateBenchmarkCommand.FormatsEnum, DataExporter> exporters;

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
        List<GeneratedTable> tables = dataset.getTables().stream()
            .map(table -> generateTable(table, dataProvider))
            .toList();
/*
        try {
            // TODO: remake -- exporter.export(dataset, tables);
        } catch (IOException exception) {
            throw new UncheckedIOException("Failed to export generated dataset " + dataset.getId(), exception);
        }
 */
    }


    private static GeneratedTable generateTable(TableEntity table, DataProvider dataProvider) {
        Integer rowCount = table.getNumberOfRows();
        if (rowCount == null || rowCount < 0) {
            throw new IllegalStateException("Table size must be set before generating data: " + table.getName());
        }

        List<ColumnEntity> columns = table.getColumns();
        List<String> columnNames = columns.stream().map(ColumnEntity::getName).toList();
        List<List<Object>> rows = new ArrayList<>(rowCount);

        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            List<Object> row = new ArrayList<>(columns.size());
            for (ColumnEntity column : columns) {
                row.add(generateValue(column.getDatatype(), dataProvider));
            }
            rows.add(row);
        }
        return new GeneratedTable(table.getId(), table.getName(), columnNames, rows);
    }

    /**
     *
     */
    private static void generateBatch() {

    }

    private static Object generateValue(ColumnDatatype datatype, DataProvider dataProvider) {
        return switch (datatype) {
            case STRING -> dataProvider.generateStringType();
            case BOOLEAN -> dataProvider.generateBooleanType();
            case INTEGER -> dataProvider.generateIntegerType();
            case FLOAT -> dataProvider.generateFloatType();
            case DATE -> dataProvider.generateDateType();
        };
    }
}
