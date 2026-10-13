package cz.cuni.mff.simekjan.document.northwind;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.bson.codecs.pojo.annotations.BsonId;

import java.time.LocalDate;

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
    }

    @BsonId
    private ObjectId id;

    private Long employeeId;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private LocalDate hireDate;
    private String city;
    private ObjectId reportsTo;
}
