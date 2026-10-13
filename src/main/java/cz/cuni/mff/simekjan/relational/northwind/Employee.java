package cz.cuni.mff.simekjan.relational.northwind;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Employees")
@Table(name = "employees",
    indexes = {
        @Index(name = "idx_employee_id", columnList = "employee_id")
    })
public class Employee implements java.io.Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "last_name", nullable = false, length = 20)
    private String lastName;

    @Column(name = "first_name", nullable = false, length = 20)
    private String firstName;

    @Column(name = "birth_date", length = 13)
    private LocalDate birthDate;

    @Column(name = "hire_date", length = 13)
    private LocalDate hireDate;

    @Column(name = "city", length = 25)
    private String city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reports_to")
    private Employee reportsTo;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "reportsTo")
    private Set<Employee> subordinates = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "employee")
    private Set<Order> orders = new HashSet<>();

    public Employee(Long employeeId, String firstName, String lastName, LocalDate birthDate, LocalDate hireDate, String city) {
        this.employeeId = employeeId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthDate = birthDate;
        this.hireDate = hireDate;
        this.city = city;
    }

    public void addSubordinate(Employee employee) {
        subordinates.add(employee);
        employee.setReportsTo(this);
    }

    public void addOrder(Order order) {
        orders.add(order);
        order.setEmployee(this);
    }
}