package cz.cuni.mff.simekjan.application.dataset;

import cz.cuni.mff.simekjan.api.DatasetApi;
import cz.cuni.mff.simekjan.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class DatasetController implements DatasetApi {
    private final DatasetService datasetService;

    public DatasetController(DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    @Override
    public ResponseEntity<List<DatasetInfo>> getDatasets() {
        return ResponseEntity.ok(datasetService.getDatasets());
    }

    @Override
    public ResponseEntity<List<QueryInfo>> getQueries(UUID datasetId) {
        return ResponseEntity.ok(datasetService.getQueries(datasetId));
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
    public ResponseEntity<PostDataset202Response> postDataset(CreateDatasetFromSchemaCommand body) {
        if (body == null || body.getSchema() == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            PostDataset202Response response = new PostDataset202Response();
            response.setId(datasetService.createDataset(body));
            return ResponseEntity.accepted().body(response);
        } catch (DatasetService.ExecutorUnavailableException exception) {
            return ResponseEntity.status(503).build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        }
    }
}
