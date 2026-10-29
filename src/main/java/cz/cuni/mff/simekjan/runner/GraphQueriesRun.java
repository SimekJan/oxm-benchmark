package cz.cuni.mff.simekjan.runner;

// import cz.cuni.mff.simekjan.graph.queries.basic.*;
// import cz.cuni.mff.simekjan.graph.queries.complex.*;

import java.util.List;

public class GraphQueriesRun implements QueriesRun {
    /*
    private static final Query01_select_indexed q1 = new Query01_select_indexed();
    private static final Query02_select_non_indexed q2 = new Query02_select_non_indexed();
    private static final Query03_range_query_indexed q3 = new Query03_range_query_indexed();
    private static final Query04_range_query_non_indexed q4 = new Query04_range_query_non_indexed();
    private static final Query05_count q5 = new Query05_count();
    private static final Query06_max q6 = new Query06_max();
    private static final Query07_join_indexed q7 = new Query07_join_indexed();
    private static final Query08_join_non_indexed q8 = new Query08_join_non_indexed();
    private static final Query09_neighbors q9 = new Query09_neighbors();
    private static final Query10_shortest_path q10 = new Query10_shortest_path();
    private static final Query11_optional_traversal q11 = new Query11_optional_traversal();
    private static final Query12_union q12 = new Query12_union();
    private static final Query13_intersect q13 = new Query13_intersect();
    private static final Query14_diff q14 = new Query14_diff();
    private static final Query15_sorting q15 = new Query15_sorting();
    private static final Query16_sorting_indexed q16 = new Query16_sorting_indexed();
    private static final Query17_distinct q17 = new Query17_distinct();
    private static final Query18_map_reduce q18 = new Query18_map_reduce();

    private static final C_Query1_filter_max cq1 = new C_Query1_filter_max();
    private static final C_Query2_join_sort cq2 = new C_Query2_join_sort();
    private static final C_Query3_group_by_sort_join cq3 = new C_Query3_group_by_sort_join();
    private static final C_Query4_group_by_having_sort cq4 = new C_Query4_group_by_having_sort();
    private static final C_Query5_filter_join cq5 = new C_Query5_filter_join();
    private static final C_Query6_range_count cq6 = new C_Query6_range_count();
    private static final C_Query7_multi_filter cq7 = new C_Query7_multi_filter();
    private static final C_Query8_multi_join cq8 = new C_Query8_multi_join();
    private static final C_Query9_subquery cq9 = new C_Query9_subquery();
    private static final C_Query10_employee_report cq10 = new C_Query10_employee_report();
    */

    private static final List<RunnableQuery> graphQueries = List.of(
        /*
        new BoundedQuery<>(q1_params, q1::run),
        new BoundedQuery<>(q2_params, q2::run),
        new BoundedQuery<>(q3_params, q3::run),
        new BoundedQuery<>(q4_params, q4::run),
        new BoundedQuery<>(qx_no_params, q5::run),
        new BoundedQuery<>(qx_no_params, q6::run),
        new BoundedQuery<>(qx_no_params, q7::run),
        new BoundedQuery<>(qx_no_params, q8::run),
        new BoundedQuery<>(qx_no_params, q9::run),
        new BoundedQuery<>(q10_params, q10::run),
        new BoundedQuery<>(qx_no_params, q11::run),
        new BoundedQuery<>(qx_no_params, q12::run),
        new BoundedQuery<>(qx_no_params, q13::run),
        new BoundedQuery<>(qx_no_params, q14::run),
        new BoundedQuery<>(qx_no_params, q15::run),
        new BoundedQuery<>(qx_no_params, q16::run),
        new BoundedQuery<>(qx_no_params, q17::run),
        new BoundedQuery<>(qx_no_params, q18::run),

        new BoundedQuery<>(cq1_params, cq1::run),
        new BoundedQuery<>(qx_no_params, cq2::run),
        new BoundedQuery<>(qx_no_params, cq3::run),
        new BoundedQuery<>(qx_no_params, cq4::run),
        new BoundedQuery<>(cq5_params, cq5::run),
        new BoundedQuery<>(cq6_params, cq6::run),
        new BoundedQuery<>(cq7_params, cq7::run),
        new BoundedQuery<>(qx_no_params, cq8::run),
        new BoundedQuery<>(qx_no_params, cq9::run),
        new BoundedQuery<>(cq10_params, cq10::run)
        */
    );

    @Override
    public List<RunnableQuery> getQueries() {
        return graphQueries;
    }
}
