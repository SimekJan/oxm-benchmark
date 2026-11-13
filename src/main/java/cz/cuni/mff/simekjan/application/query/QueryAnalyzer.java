package cz.cuni.mff.simekjan.application.query;

import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.model.QueryFileFormat;

/**
 * Defines methods for analyzing queries in a supported format.
 */
public interface QueryAnalyzer {

    QueryFileFormat getFormat();

    void inferSchema(DatasetEntity dataset, QueryEntity queryEntity, String query);

    void extractOperationMetadata(DatasetEntity dataset, QueryEntity queryEntity, String query);
}
