package cz.cuni.mff.simekjan.generator;

import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.application.query.QuerySetEntity;
import cz.cuni.mff.simekjan.model.GenerateBenchmarkFilesCommand;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Generates data for a benchmark using its dataset and selected query set.
 */
public class Generator {

    private final DatasetEntity dataset;
    private final QuerySetEntity querySet;

    public Generator(DatasetEntity dataset, UUID querySetId) {
        this.dataset = Objects.requireNonNull(dataset, "dataset");
        Objects.requireNonNull(querySetId, "querySetId");
        this.querySet = dataset.getQuerySets().stream()
            .filter(candidate -> candidate.getId().equals(querySetId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "Query set does not belong to dataset: " + querySetId
            ));
    }

    /**
     * Generates benchmark data as files in the requested format.
     */
    public void generateToFiles(GenerateBenchmarkFilesCommand.FormatEnum format) {
    }

    /**
     * Generates benchmark data directly in the requested databases.
     */
    public void generateToDatabases(List<Object> databases) {
    }
}
