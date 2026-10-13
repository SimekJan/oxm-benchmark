package cz.cuni.mff.simekjan.runner;

import cz.cuni.mff.simekjan.query_params.params.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Common interface for definition of series of Queries to run for given OXM
 */
public interface QueriesRun {

    QX_No_Params qx_no_params = new QX_No_Params();
    Q1_Params q1_params = new Q1_Params(1L);
    Q2_Params q2_params = new Q2_Params("Steven");
    Q3_Params q3_params = new Q3_Params(1L, 5L);
    Q4_Params q4_params = new Q4_Params(200.0F, 300.0F);
    Q10_Params q10_params = new Q10_Params(5L, 1L);

    CQ1_Params cq1_params = new CQ1_Params('S');
    CQ5_Params cq5_params = new CQ5_Params("Packaging Materials", 'P');
    CQ6_Params cq6_params = new CQ6_Params(10F, 15F);
    CQ7_Params cq7_params = new CQ7_Params('R', 'S', "Reykjavik",
        LocalDate.of(2000, 1, 1),
        LocalDate.of(2014, 1, 1));
    CQ10_Params cq10_params = new CQ10_Params(LocalDate.of(2026, 10, 1));

    /**
     * Returns predefined list of Queries to be run in benchmark for given OXM
     *
     * @return Series of Queries defined for given OXM
     */
    List<RunnableQuery> getQueries();
}
