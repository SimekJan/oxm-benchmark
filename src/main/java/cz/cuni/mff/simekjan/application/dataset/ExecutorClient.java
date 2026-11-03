package cz.cuni.mff.simekjan.application.dataset;

import cz.cuni.mff.simekjan.model.CreateDatasetFromSchemaCommand;
import cz.cuni.mff.simekjan.model.DatasetRuntimeStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Component
public class ExecutorClient {

    private final RestClient restClient;

    public ExecutorClient(
        RestClient.Builder restClientBuilder,
        @Value("${executor.base-url:http://localhost:8081}") String baseUrl
    ) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    public DatasetRuntimeStatus createDataset(UUID datasetId, CreateDatasetFromSchemaCommand command) {
        return execute(() -> restClient.post()
            .uri("/internal/datasets/{datasetId}", datasetId)
            .body(command)
            .retrieve()
            .body(DatasetRuntimeStatus.class));
    }

    public DatasetRuntimeStatus getStatus(UUID datasetId) {
        return execute(() -> restClient.get()
            .uri("/internal/datasets/{datasetId}", datasetId)
            .retrieve()
            .body(DatasetRuntimeStatus.class));
    }

    private static DatasetRuntimeStatus execute(StatusRequest request) {
        try {
            DatasetRuntimeStatus status = request.execute();
            if (status == null) {
                throw new ExecutorRequestException("Executor returned an empty response");
            }
            return status;
        } catch (RestClientException exception) {
            throw new ExecutorRequestException("Executor request failed: " + exception.getMessage(), exception);
        }
    }

    @FunctionalInterface
    private interface StatusRequest {
        DatasetRuntimeStatus execute();
    }

    public static class ExecutorRequestException extends RuntimeException {
        public ExecutorRequestException(String message) {
            super(message);
        }

        public ExecutorRequestException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
