package cz.cuni.mff.simekjan.query_params;

import cz.cuni.mff.simekjan.query_params.params.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryParams {
    private Q1_Params q1_params;
    private Q2_Params q2_params;
    private Q3_Params q3_params;
    private Q4_Params q4_params;
    private Q10_Params q10_params;

    private CQ1_Params cq1_params;
    private CQ5_Params cq5_params;
    private CQ6_Params cq6_params;
    private CQ7_Params cq7_params;
    private CQ10_Params cq10_params;
}
