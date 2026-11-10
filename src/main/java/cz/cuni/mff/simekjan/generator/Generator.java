package cz.cuni.mff.simekjan.generator;

import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.application.query.QuerySetEntity;

import java.util.Objects;
import java.util.UUID;

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

    public void generate() {
    }
}
