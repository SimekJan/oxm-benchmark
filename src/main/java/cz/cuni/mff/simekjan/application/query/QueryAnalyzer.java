package cz.cuni.mff.simekjan.application.query;

import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.model.QueryFileFormat;

public interface QueryAnalyzer {

    QueryFileFormat getFormat();

    void inferSchema(DatasetEntity dataset, String query);

    void extractOperationMetadata(DatasetEntity dataset, String query);
}
