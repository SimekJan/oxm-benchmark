package cz.cuni.mff.simekjan.application.dataset;

import cz.cuni.mff.simekjan.api.DatasetApi;
import cz.cuni.mff.simekjan.model.DatasetInfo;
import cz.cuni.mff.simekjan.model.QueryInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public ResponseEntity<List<QueryInfo>> getQueries(String datasetId) {
        return ResponseEntity.ok(datasetService.getQueries(datasetId));
    }
}
