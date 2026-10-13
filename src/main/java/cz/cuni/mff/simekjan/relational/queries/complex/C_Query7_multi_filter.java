package cz.cuni.mff.simekjan.relational.queries.complex;

import cz.cuni.mff.simekjan.query_params.params.CQ7_Params;
import cz.cuni.mff.simekjan.relational.northwind.Employee;
import cz.cuni.mff.simekjan.relational.queries.RelationalQuery;

import java.util.List;

/**
 * Select only employees with firstname starting with B, lastname starting with M,
 * city 'Prague' birthdate before 1991 and date of hire after 2018.
 */
public class C_Query7_multi_filter implements RelationalQuery<CQ7_Params> {

    @Override
    public void run(CQ7_Params params) {
        System.out.println("----------PostgreSQL-CQ7---------");

        String hql = "FROM Employees e " +
            "WHERE e.firstName LIKE CONCAT(:startingLetterFirstName, '%') " +
            "AND e.lastName LIKE CONCAT(:startingLetterLastName, '%') " +
            "AND e.city = :city " +
            "AND e.birthDate <= :birth_date " +
            "AND e.hireDate >= :start_date ";

        List<Employee> result = session.createQuery(hql, Employee.class)
            .setParameter("startingLetterFirstName", params.firstNameStartingLetter())
            .setParameter("startingLetterLastName", params.lastNameStartingLetter())
            .setParameter("city", params.cityName())
            .setParameter("birth_date", params.birthDate())
            .setParameter("start_date", params.hireDate())
            .getResultList();

        for (Employee e : result) {
            System.out.println(e.getFirstName() + " " + e.getLastName());
        }
    }
}
