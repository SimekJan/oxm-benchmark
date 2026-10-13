package cz.cuni.mff.simekjan.relational.northwind_full;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
/*
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name="Employees")
@Table(name = "employees", schema = "public")
public class Employee implements java.io.Serializable {

    @Id
    @Column(name = "employee_id", unique = true, nullable = false)
    private short employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reports_to")
    private Employee manager;

    @Column(name = "last_name", nullable = false, length = 20)
    private String lastName;

    @Column(name = "first_name", nullable = false, length = 10)
    private String firstName;

    @Column(name = "title", length = 30)
    private String title;

    @Column(name = "title_of_courtesy", length = 25)
    private String titleOfCourtesy;

    @Column(name = "birth_date", length = 13)
    private LocalDate birthDate;

    @Column(name = "hire_date", length = 13)
    private LocalDate hireDate;

    @Column(name = "address", length = 60)
    private String address;

    @Column(name = "city", length = 15)
    private String city;

    @Column(name = "region", length = 15)
    private String region;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Column(name = "country", length = 15)
    private String country;

    @Column(name = "home_phone", length = 24)
    private String homePhone;

    @Column(name = "extension", length = 4)
    private String extension;

    @Column(name = "photo")
    private byte[] photo;

    @Column(name = "notes")
    private String notes;

    @Column(name = "photo_path")
    private String photoPath;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "employee")
    private Set<Order> orders = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "employee_territories",
            schema = "public",
            joinColumns = @JoinColumn(name = "employee_id", nullable = false, updatable = false),
            inverseJoinColumns = @JoinColumn(name = "territory_id", nullable = false, updatable = false)
    )
    private Set<Territory> territories = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "manager")
    private Set<Employee> subordinates = new HashSet<>();

    public Employee(short employeeId, String lastName, String firstName) {
        this.employeeId = employeeId;
        this.lastName = lastName;
        this.firstName = firstName;
    }
}
*/