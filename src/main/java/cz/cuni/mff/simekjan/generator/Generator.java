package cz.cuni.mff.simekjan.generator;

import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.application.query.QuerySetEntity;

/**
 * Generates benchmark data in a specific output format.
 */
public interface Generator {

    /**
     * Generates data for the given dataset and query set.
     */
    void generate(DatasetEntity dataset, QuerySetEntity querySet);
}
