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
    public ResponseEntity<PostDataset202Response> postDataset(CreateDatasetFromSchemaCommand body) {
        if (body.getSchema() == null) {
            return ResponseEntity.badRequest().build();
        }

        // TODO: finish this method
        // Process the dataset creation logic here

        return ResponseEntity.ok(new PostDataset202Response());
    }
}
