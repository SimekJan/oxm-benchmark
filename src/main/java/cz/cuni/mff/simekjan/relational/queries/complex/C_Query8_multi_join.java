package cz.cuni.mff.simekjan.relational.queries.complex;

import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import cz.cuni.mff.simekjan.relational.queries.RelationalQuery;

import java.time.LocalDate;
import java.util.List;

/**
 * Join Customers, Employees, Orders, Products and Suppliers. (Indexed)
 */
public class C_Query8_multi_join implements RelationalQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {
        System.out.println("----------PostgreSQL-CQ8---------");

        String hql = "SELECT c.companyName, e.firstName, o.orderDate, p.productName, s.companyName " +
            "FROM Suppliers s " +
            "JOIN s.products p " +
            "JOIN p.orders o " +
            "JOIN o.employee e " +
            "JOIN o.customer c " +
            "WHERE s.companyName = 'BluePeak Industries' " +
            "AND p.productName = 'Steel Plate' " +
            "AND e.firstName = 'Jeffrey' " +
            "AND c.companyName = 'StoneBridge' ";

        List<Object[]> result = session.createQuery(hql, Object[].class).getResultList();

        for (Object[] row : result) {
            String customerCompanyName = (String) row[0];
            String employeeName = (String) row[1];
            LocalDate orderDate = (LocalDate) row[2];
            String productName = (String) row[3];
            String supplierCompanyName = (String) row[4];

            System.out.println(customerCompanyName + " - " + employeeName + " - " + orderDate + " - " + productName + " - " + supplierCompanyName);
        }

    }
}
