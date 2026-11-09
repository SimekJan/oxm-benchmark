package cz.cuni.mff.simekjan.application.query.analyzer;

import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.application.operation.OperationService;
import cz.cuni.mff.simekjan.application.query.QueryAnalyzer;
import cz.cuni.mff.simekjan.application.query.QueryEntity;
import cz.cuni.mff.simekjan.model.QueryFileFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SqlQueryAnalyzer implements QueryAnalyzer {

    private final OperationService operationService;

    @Override
    public QueryFileFormat getFormat() {
        return QueryFileFormat._SQL;
    }

    @Override
    public void inferSchema(DatasetEntity dataset, QueryEntity queryEntity, String query) {
        throw new UnsupportedOperationException("SQL query analysis is not implemented");
    }

    @Override
    public void extractOperationMetadata(DatasetEntity dataset, QueryEntity queryEntity, String query) {
        new SqlOperationExtractor(operationService).extract(dataset, queryEntity, query);
    }
}
