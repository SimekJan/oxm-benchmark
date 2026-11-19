package cz.cuni.mff.simekjan.application.dataset;

import cz.cuni.mff.simekjan.model.*;

import java.util.List;
import java.util.UUID;

/**
 * Defines business logic methods to operate with Datasets.
 */
public interface DatasetService {

    List<DatasetInfo> getDatasets();

    DatasetDetail getDataset(UUID datasetId);

    void requireDataset(UUID datasetId);

    void generateData(UUID datasetId, List<GenerateBenchmarkCommand.FormatsEnum> format);

    UUID createDatasetFromSchema(CreateDatasetFromSchemaCommand command);

    UUID createDatasetWithoutSchema(CreateDatasetWithoutSchemaCommand command);

    UUID createDatasetFromDatabase(CreateDatasetFromDatabaseCommand command);

    UUID createDatasetFromFile(CreateDatasetFromFileCommand command);

    DatasetRuntimeStatus getRuntimeStatus(UUID datasetId);

    List<QuerySetInfo> getQuerySets(UUID datasetId);

    QuerySetDetail getQuerySet(UUID datasetId, UUID querySetId);

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
