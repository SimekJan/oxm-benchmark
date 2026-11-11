package cz.cuni.mff.simekjan.application.query;

import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.model.CreateQuerySetCommand;
import cz.cuni.mff.simekjan.model.QuerySetInfo;

import java.util.List;
import java.util.UUID;

public interface QueryService {

    List<QuerySetInfo> getQuerySets(UUID datasetId);

    QuerySetEntity getQuerySetEntity(UUID querySetId);

    UUID createQuerySet(DatasetEntity dataset, CreateQuerySetCommand command);

    class QuerySetCreationNotImplementedException extends RuntimeException {
        public QuerySetCreationNotImplementedException(Throwable cause) {
            super("Query set creation is not implemented for this file format", cause);
        }
    }

    class QuerySetNotFoundException extends RuntimeException {
        public QuerySetNotFoundException(UUID querySetId) {
            super("Query set not found: " + querySetId);
        }
    }
}
