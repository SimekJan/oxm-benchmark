package cz.cuni.mff.simekjan.graph.queries.complex;

import cz.cuni.mff.simekjan.graph.queries.GraphQuery;
import cz.cuni.mff.simekjan.query_params.params.CQ10_Params;

import java.util.Map;

/**
 * Create a complex real-world-like report including employee info
 */
public class C_Query10_employee_report implements GraphQuery<CQ10_Params> {

    @Override
    public void run(CQ10_Params params) {

        String query = """
            MATCH (e:Employee)
            WITH e
            
            OPTIONAL MATCH (e)-[:IS_RESPONSIBLE_FOR]->(o:Order)
            WITH e, COUNT(o) AS orderCount
            
            OPTIONAL MATCH (e)-[:IS_RESPONSIBLE_FOR]->(o:Order)-[:INCLUDES]->(p:Product)
            WITH e, orderCount, SUM(p.unitPrice) AS totalPrice
            
            OPTIONAL MATCH (s:Employee)-[:REPORTS_TO]->(e)
            WITH e, orderCount, totalPrice, COUNT(s) AS subordinateCount
            
            WITH e, orderCount, totalPrice, subordinateCount,
                 duration.between(date(e.hireDate), date($currentDate)).years AS yearsWorked,
                 duration.between(date(e.birthDate), date($currentDate)).years AS age
            
            RETURN
                e.employeeId AS employeeId,
                e.firstName AS firstName,
                e.lastName AS lastName,
                orderCount,
                totalPrice,
                subordinateCount,
                yearsWorked,
                age
            """;

        Iterable<Map<String, Object>> results =
            session.query(query, Map.of("currentDate", params.today().toString()));

        System.out.println("------------Neo4j-CQ10-----------");
        for (Map<String, Object> row : results) {
            Long employeeId = (Long) row.get("employeeId");
            String firstName = (String) row.get("firstName");
            String lastName = (String) row.get("lastName");
            Long numberOfOrders = (Long) row.get("orderCount");
            // Number needed because if whole number is returned then the value needs to be parsed into integer
            Number totalPrice = (Number) row.get("totalPrice");
            Long numberOfSubordinates = (Long) row.get("subordinateCount");
            Long yearsSinceHire = (Long) row.get("yearsWorked");
            Long age = (Long) row.get("age");

            System.out.println("Employee: " + employeeId + " " + firstName + " " + lastName);
            System.out.println("Manages " + numberOfOrders + " orders, with total price of " + totalPrice);
            System.out.println("Supervises " + numberOfSubordinates + " employee(s)");
            System.out.println("Works for " + yearsSinceHire + " years. Aged: " + age);
            System.out.println("-----------------------------------------------------------------------------------");
        }
    }
}
