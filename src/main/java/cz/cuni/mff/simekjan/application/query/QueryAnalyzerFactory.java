package cz.cuni.mff.simekjan.application.query;

import cz.cuni.mff.simekjan.model.QueryFileFormat;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class QueryAnalyzerFactory {

    private final Map<QueryFileFormat, QueryAnalyzer> analyzers;

    public QueryAnalyzerFactory(List<QueryAnalyzer> analyzers) {
        Map<QueryFileFormat, QueryAnalyzer> analyzersByFormat = new EnumMap<>(QueryFileFormat.class);
        for (QueryAnalyzer analyzer : analyzers) {
            QueryAnalyzer previous = analyzersByFormat.putIfAbsent(analyzer.getFormat(), analyzer);
            if (previous != null) {
                throw new IllegalStateException("Multiple query analyzers registered for " + analyzer.getFormat());
            }
        }
        this.analyzers = Map.copyOf(analyzersByFormat);
    }

    public QueryAnalyzer getAnalyzer(QueryFileFormat format) {
        QueryAnalyzer analyzer = analyzers.get(format);
        if (analyzer == null) {
            throw new IllegalArgumentException("No query analyzer registered for " + format);
        }
        return analyzer;
    }
}
