package cz.cuni.mff.simekjan.application.query;

import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.model.CreateQuerySetCommand;
import cz.cuni.mff.simekjan.model.CreateQuerySetCommandFilesInner;
import cz.cuni.mff.simekjan.model.QueryInfo;
import cz.cuni.mff.simekjan.model.QuerySetInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Implements business logic for operating with queries and query sets.
 */
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class QueryServiceImpl implements QueryService {

    private final QuerySetRepository querySetRepository;
    private final QueryAnalyzerFactory queryAnalyzerFactory;

    public List<QuerySetInfo> getQuerySets(UUID datasetId) {
        return querySetRepository.findAllByDataset_IdOrderByNameAsc(datasetId)
            .stream()
            .map(querySet -> {
                List<QueryInfo> queries = querySet.getQueries().stream()
                    .sorted(Comparator.comparing(QueryEntity::getName))
                    .map(query -> {
                        QueryInfo queryInfo = new QueryInfo(query.getId(), query.getName());
                        queryInfo.setDescription(query.getDescription());
                        return queryInfo;
                    })
                    .toList();
                QuerySetInfo info = new QuerySetInfo();
                info.setId(querySet.getId());
                info.setName(querySet.getName());
                info.setQueries(queries);
                return info;
            })
            .toList();
    }

    public QuerySetEntity getQuerySetEntity(UUID querySetId) {
        return querySetRepository.findById(querySetId)
            .orElseThrow(() -> new QuerySetNotFoundException(querySetId));
    }

    @Transactional
    public UUID createQuerySet(DatasetEntity dataset, CreateQuerySetCommand command) {
        if (command == null || command.getName() == null || command.getName().isBlank()) {
            throw new IllegalArgumentException("Query set name must not be blank");
        }
        if (command.getFiles() == null || command.getFiles().isEmpty()) {
            throw new IllegalArgumentException("At least one query file is required");
        }

        for (CreateQuerySetCommandFilesInner file : command.getFiles()) {
            if (file == null || file.getName() == null || file.getName().isBlank()
                || file.getFormat() == null || file.getContent() == null) {
                throw new IllegalArgumentException("Query files must have a name, format, and content");
            }
        }

        QuerySetEntity querySet = new QuerySetEntity();
        querySet.setId(UUID.randomUUID());
        querySet.setName(command.getName());
        querySet.setDataset(dataset);

        for (CreateQuerySetCommandFilesInner file : command.getFiles()) {
            QueryEntity queryEntity = new QueryEntity();
            queryEntity.setId(UUID.randomUUID());
            queryEntity.setName(file.getName());
            queryEntity.setDataset(dataset);
            queryEntity.setQuerySet(querySet);
            querySet.getQueries().add(queryEntity);

            QueryAnalyzer analyzer = queryAnalyzerFactory.getAnalyzer(file.getFormat());
            try {
                if (!dataset.isSchemaReady()) {
                    analyzer.inferSchema(dataset, queryEntity, file.getContent());
                }
                analyzer.extractOperationMetadata(dataset, queryEntity, file.getContent());
            } catch (UnsupportedOperationException exception) {
                throw new QuerySetCreationNotImplementedException(exception);
            }
        }

        QuerySetEntity savedQuerySet = querySetRepository.saveAndFlush(querySet);
        dataset.getQuerySets().add(savedQuerySet);
        return savedQuerySet.getId();
    }

}
