package cz.cuni.mff.simekjan.application.query;

import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.model.CreateQuerySetCommand;
import cz.cuni.mff.simekjan.model.CreateQuerySetCommandFilesInner;
import cz.cuni.mff.simekjan.model.QueryInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class QueryService {

    private final QueryRepository queryRepository;
    private final QueryAnalyzerFactory queryAnalyzerFactory;

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

    public UUID createQuerySet(DatasetEntity dataset, CreateQuerySetCommand command) {
        if (command == null || command.getFiles() == null || command.getFiles().isEmpty()) {
            throw new IllegalArgumentException("At least one query file is required");
        }

        for (CreateQuerySetCommandFilesInner file : command.getFiles()) {
            if (file == null || file.getName() == null || file.getName().isBlank()
                || file.getFormat() == null || file.getContent() == null) {
                throw new IllegalArgumentException("Query files must have a name, format, and content");
            }
        }

        for (CreateQuerySetCommandFilesInner file : command.getFiles()) {
            QueryEntity queryEntity = new QueryEntity();
            queryEntity.setId(UUID.randomUUID());
            queryEntity.setName(file.getName());
            queryEntity.setDataset(dataset);

            QueryAnalyzer analyzer = queryAnalyzerFactory.getAnalyzer(file.getFormat());
            if (!dataset.isSchemaReady()) {
                analyzer.inferSchema(dataset, queryEntity, file.getContent());
            }
            analyzer.extractOperationMetadata(dataset, queryEntity, file.getContent());
        }

        throw new QuerySetCreationNotImplementedException();
    }

    public static class QuerySetCreationNotImplementedException extends RuntimeException {
        public QuerySetCreationNotImplementedException() {
            super("Query set creation is not implemented");
        }
    }
}
