package cz.cuni.mff.simekjan.application.query.analyzer;

import cz.cuni.mff.simekjan.application.query.QueryAnalyzer;
import cz.cuni.mff.simekjan.model.QueryFileFormat;
import org.springframework.stereotype.Component;

@Component
public class SqlQueryAnalyzer implements QueryAnalyzer {

    @Override
    public QueryFileFormat getFormat() {
        return QueryFileFormat._SQL;
    }

    @Override
    public void inferSchema(String query) {
        throw new UnsupportedOperationException("SQL query analysis is not implemented");
    }

    @Override
    public void extractOperationMetadata(String query) {
        throw new UnsupportedOperationException("SQL query analysis is not implemented");
    }
}
