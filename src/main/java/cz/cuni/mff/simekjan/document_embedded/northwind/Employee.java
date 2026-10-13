package cz.cuni.mff.simekjan.document_embedded.northwind;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Employee {

    public Employee(Long employeeId, String firstName, String lastName, LocalDate birthDate, LocalDate hireDate, String city) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.hireDate = hireDate;
        this.city = city;
        this.orders = new ArrayList<>();
    }

    private ObjectId id;

    private Long employeeId;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private LocalDate hireDate;
    private String city;
    private ObjectId reportsTo;

    private List<OrderSnapshot> orders;
}
