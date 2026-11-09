package cz.cuni.mff.simekjan.application.dataset;

import cz.cuni.mff.simekjan.api.DatasetApi;
import cz.cuni.mff.simekjan.application.query.QueryService.QuerySetCreationNotImplementedException;
import cz.cuni.mff.simekjan.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class DatasetController implements DatasetApi {

    private final DatasetService datasetService;

    @Override
    public ResponseEntity<List<DatasetInfo>> getDatasets() {
        return ResponseEntity.ok(datasetService.getDatasets());
    }

    @Override
    public ResponseEntity<List<QuerySetInfo>> getQueries(UUID datasetId) {
        return ResponseEntity.ok(datasetService.getQuerySets(datasetId));
    }

    @Override
    public ResponseEntity<QueryDetail> getQuery(UUID datasetId, UUID queryId) {
        return null;
    }

    @Override
    public ResponseEntity<DatasetRuntimeStatus> getDatasetRuntimeStatus(UUID datasetId) {
        try {
            return ResponseEntity.ok(datasetService.getRuntimeStatus(datasetId));
        } catch (DatasetService.DatasetNotFoundException exception) {
            return ResponseEntity.notFound().build();
        } catch (DatasetService.ExecutorUnavailableException exception) {
            return ResponseEntity.status(503).build();
        }
    }

    @Override
    public ResponseEntity<PostDataset202Response> postDataset(CreateDatasetCommand command) {
        if (command == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            PostDataset202Response response = new PostDataset202Response();
            response.setId(switch (command.getCreationType()) {
                case "none" -> datasetService.createDatasetWithoutSchema(
                    requireVariant(command, CreateDatasetWithoutSchemaCommand.class));
                case "schema" -> datasetService.createDatasetFromSchema(
                    requireVariant(command, CreateDatasetFromSchemaCommand.class));
                case "database" -> datasetService.createDatasetFromDatabase(
                    requireVariant(command, CreateDatasetFromDatabaseCommand.class));
                case "file" -> datasetService.createDatasetFromFile(
                    requireVariant(command, CreateDatasetFromFileCommand.class));
                default -> throw new IllegalArgumentException("Unknown dataset creation type");
            });
            return ResponseEntity.accepted().body(response);
        } catch (DatasetService.DatasetCreationNotSupportedException exception) {
            return ResponseEntity.status(501).build();
        } catch (DatasetService.ExecutorUnavailableException exception) {
            return ResponseEntity.status(503).build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        }
    }

    private static <T> T requireVariant(CreateDatasetCommand command, Class<T> variantType) {
        if (!variantType.isInstance(command)) {
            throw new IllegalArgumentException("Dataset creation type does not match command");
        }
        return variantType.cast(command);
    }

    @Override
    public ResponseEntity<PostDataset202Response> postQuerySet(UUID datasetId, CreateQuerySetCommand createQuerySetCommand) {
        try {
            PostDataset202Response response = new PostDataset202Response();
            response.setId(datasetService.createQuerySet(datasetId, createQuerySetCommand));
            return ResponseEntity.accepted().body(response);
        } catch (DatasetService.DatasetNotFoundException exception) {
            return ResponseEntity.notFound().build();
        } catch (QuerySetCreationNotImplementedException exception) {
            return ResponseEntity.status(501).build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        }
    }
}
