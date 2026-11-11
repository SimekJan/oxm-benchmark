package cz.cuni.mff.simekjan.application.dataset;

import cz.cuni.mff.simekjan.model.CreateDatasetFromDatabaseCommand;
import cz.cuni.mff.simekjan.model.CreateDatasetFromFileCommand;
import cz.cuni.mff.simekjan.model.CreateDatasetFromSchemaCommand;
import cz.cuni.mff.simekjan.model.CreateDatasetWithoutSchemaCommand;
import cz.cuni.mff.simekjan.model.CreateQuerySetCommand;
import cz.cuni.mff.simekjan.model.DatasetInfo;
import cz.cuni.mff.simekjan.model.DatasetRuntimeStatus;
import cz.cuni.mff.simekjan.model.QuerySetInfo;

import java.util.List;
import java.util.UUID;

public interface DatasetService {

    List<DatasetInfo> getDatasets();

    DatasetEntity getDatasetEntity(UUID datasetId);

    UUID createDatasetFromSchema(CreateDatasetFromSchemaCommand command);

    UUID createDatasetWithoutSchema(CreateDatasetWithoutSchemaCommand command);

    UUID createDatasetFromDatabase(CreateDatasetFromDatabaseCommand command);

    UUID createDatasetFromFile(CreateDatasetFromFileCommand command);

    DatasetRuntimeStatus getRuntimeStatus(UUID datasetId);

    List<QuerySetInfo> getQuerySets(UUID datasetId);

    UUID createQuerySet(UUID datasetId, CreateQuerySetCommand command);

    class DatasetNotFoundException extends RuntimeException {
        public DatasetNotFoundException(UUID datasetId) {
            super("Dataset not found: " + datasetId);
        }
    }

    class ExecutorUnavailableException extends RuntimeException {
        public ExecutorUnavailableException(Throwable cause) {
            super("Dataset executor is unavailable", cause);
        }
    }

    class DatasetCreationNotSupportedException extends RuntimeException {
        public DatasetCreationNotSupportedException(String creationType) {
            super("Dataset creation type is not implemented: " + creationType);
        }
    }
}
