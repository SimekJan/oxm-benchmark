package cz.cuni.mff.simekjan.application.query.analyzer;

import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.application.operation.OperationService;
import cz.cuni.mff.simekjan.application.query.QueryAnalyzer;
import cz.cuni.mff.simekjan.application.query.QueryEntity;
import cz.cuni.mff.simekjan.model.QueryFileFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Implements query analysis for queries expressed in the Java Hibernate format.
 */
@RequiredArgsConstructor
@Component
public class HibernateOrmJavaQueryAnalyzer implements QueryAnalyzer {

    private final OperationService operationService;

    @Override
    public QueryFileFormat getFormat() {
        return QueryFileFormat._JAVA;
    }

    @Override
    public void inferSchema(DatasetEntity dataset, QueryEntity queryEntity, String query) {
        throw new UnsupportedOperationException("Hibernate ORM Java query analysis is not implemented");
    }

    @Override
    public void extractOperationMetadata(DatasetEntity dataset, QueryEntity queryEntity, String query) {
        throw new UnsupportedOperationException("Hibernate ORM Java query analysis is not implemented");
    }
}
