package cz.cuni.mff.simekjan.runner;

import cz.cuni.mff.simekjan.document_embedded.queries.basic.Query07_join_indexed;
import cz.cuni.mff.simekjan.document_embedded.queries.basic.Query14_diff;
import cz.cuni.mff.simekjan.document_embedded.queries.complex.*;

import java.util.List;

public class EmbeddedDocumentQueriesRun implements QueriesRun {

    private static final Query07_join_indexed q7 = new Query07_join_indexed();
    private static final Query14_diff q14 = new Query14_diff();

    private static final C_Query2_join_sort cq2 = new C_Query2_join_sort();
    private static final C_Query3_group_by_sort_join cq3 = new C_Query3_group_by_sort_join();
    private static final C_Query4_group_by_having_sort cq4 = new C_Query4_group_by_having_sort();
    private static final C_Query5_filter_join cq5 = new C_Query5_filter_join();
    private static final C_Query10_employee_report cq10 = new C_Query10_employee_report();

    private static final List<RunnableQuery> embeddedDocumentQueries = List.of(
        new BoundedQuery<>(qx_no_params, q7::run),
        new BoundedQuery<>(qx_no_params, q14::run),

        new BoundedQuery<>(qx_no_params, cq2::run),
        new BoundedQuery<>(qx_no_params, cq3::run),
        new BoundedQuery<>(qx_no_params, cq4::run),
        new BoundedQuery<>(cq5_params, cq5::run),
        new BoundedQuery<>(cq10_params, cq10::run)
    );

    /**
     * Provides all queries for Embedded MongoDB
     *
     * @return List of Embedded Document queries
     */
    @Override
    public List<RunnableQuery> getQueries() {
        return embeddedDocumentQueries;
    }
}
