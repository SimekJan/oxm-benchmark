package cz.cuni.mff.simekjan.application.dataset;

import cz.cuni.mff.simekjan.model.DatasetInfo;
import cz.cuni.mff.simekjan.model.DatasetRuntimeStatus;
import cz.cuni.mff.simekjan.model.QueryInfo;
import cz.cuni.mff.simekjan.model.CreateDatasetFromSchemaCommand;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class DatasetService {

    private final DatasetRepository datasetRepository;
    private final QueryRepository queryRepository;
    private final ObjectMapper objectMapper;
    private final ExecutorClient executorClient;

    public DatasetService(
        DatasetRepository datasetRepository,
        QueryRepository queryRepository,
        ObjectMapper objectMapper,
        ExecutorClient executorClient
    ) {
        this.datasetRepository = datasetRepository;
        this.queryRepository = queryRepository;
        this.objectMapper = objectMapper;
        this.executorClient = executorClient;
    }

    public List<DatasetInfo> getDatasets() {
        return datasetRepository.findAllByOrderByNameAsc()
            .stream()
            .map(dataset -> {
                DatasetInfo info = new DatasetInfo(dataset.getId(), dataset.getName());
                info.setDescription(dataset.getDescription());
                if (dataset.getRuntimeStatus() != null) {
                    info.setRuntimeStatus(DatasetInfo.RuntimeStatusEnum.fromValue(dataset.getRuntimeStatus()));
                }
                return info;
            })
            .toList();
    }

    @Transactional(noRollbackFor = ExecutorUnavailableException.class)
    public UUID createDataset(CreateDatasetFromSchemaCommand command) {
        validateSchema(command);
        UUID datasetId = UUID.randomUUID();

        DatasetEntity dataset = new DatasetEntity();
        dataset.setId(datasetId);
        dataset.setName(command.getName());
        dataset.setDescription(command.getDescription());
        dataset.setRuntimeStatus("BUILDING");
        try {
            dataset.setSchemaJson(objectMapper.writeValueAsString(command.getSchema()));
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Dataset schema could not be serialized", exception);
        }

        datasetRepository.saveAndFlush(dataset);
        try {
            DatasetRuntimeStatus status = executorClient.createDataset(datasetId, command);
            updateRuntimeStatus(dataset, status);
            datasetRepository.saveAndFlush(dataset);
        } catch (ExecutorClient.ExecutorRequestException exception) {
            dataset.setRuntimeStatus("FAILED");
            dataset.setRuntimeMessage(exception.getMessage());
            datasetRepository.saveAndFlush(dataset);
            throw new ExecutorUnavailableException(exception);
        }

        return datasetId;
    }

    @Transactional
    public DatasetRuntimeStatus getRuntimeStatus(UUID datasetId) {
        DatasetEntity dataset = datasetRepository.findById(datasetId)
            .orElseThrow(() -> new DatasetNotFoundException(datasetId));
        DatasetRuntimeStatus status;
        try {
            status = executorClient.getStatus(datasetId);
        } catch (ExecutorClient.ExecutorRequestException exception) {
            throw new ExecutorUnavailableException(exception);
        }
        updateRuntimeStatus(dataset, status);
        datasetRepository.saveAndFlush(dataset);
        return status;
    }

    public List<QueryInfo> getQueries(UUID datasetId) {
        return queryRepository.findAllByDataset_IdOrderByNameAsc(datasetId)
            .stream()
            .map(query -> {
                QueryInfo info = new QueryInfo(query.getId(), query.getName());
                info.setDescription(query.getDescription());
                return info;
            })
            .toList();
    }

    private static void validateSchema(CreateDatasetFromSchemaCommand command) {
        if (command.getName() == null || command.getName().isBlank()) {
            throw new IllegalArgumentException("Dataset name must not be blank");
        }
        if (command.getSchema().getTables() == null || command.getSchema().getTables().isEmpty()) {
            throw new IllegalArgumentException("Dataset schema must contain at least one table");
        }
    }

    private static void updateRuntimeStatus(DatasetEntity dataset, DatasetRuntimeStatus status) {
        dataset.setRuntimeStatus(status.getStatus().getValue());
        dataset.setRuntimeMessage(status.getMessage());
    }

    public static class DatasetNotFoundException extends RuntimeException {
        public DatasetNotFoundException(UUID datasetId) {
            super("Dataset not found: " + datasetId);
        }
    }

    public static class ExecutorUnavailableException extends RuntimeException {
        public ExecutorUnavailableException(Throwable cause) {
            super("Dataset executor is unavailable", cause);
        }
    }
}
