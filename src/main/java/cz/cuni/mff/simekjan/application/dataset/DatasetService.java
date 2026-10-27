package cz.cuni.mff.simekjan.application.dataset;

import cz.cuni.mff.simekjan.model.DatasetInfo;
import cz.cuni.mff.simekjan.model.QueryInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DatasetService {

    private final DatasetRepository datasetRepository;
    private final QueryRepository queryRepository;

    public DatasetService(
        DatasetRepository datasetRepository,
        QueryRepository queryRepository
    ) {
        this.datasetRepository = datasetRepository;
        this.queryRepository = queryRepository;
    }

    public List<DatasetInfo> getDatasets() {
        return datasetRepository.findAllByOrderByNameAsc()
            .stream()
            .map(dataset -> {
                DatasetInfo info = new DatasetInfo(dataset.getId(), dataset.getName());
                info.setDescription(dataset.getDescription());
                return info;
            })
            .toList();
    }

    public List<QueryInfo> getQueries(String datasetId) {
        return queryRepository.findAllByDataset_IdOrderByNameAsc(datasetId)
            .stream()
            .map(query -> {
                QueryInfo info = new QueryInfo(query.getId(), query.getName());
                info.setDescription(query.getDescription());
                return info;
            })
            .toList();
    }
}
