package queries.northwind.hibernate.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity(name = "Customers")
@Table(name = "customers",
    indexes = {
        @Index(name = "idx_customer_id", columnList = "customer_id")
    })
public class Customer implements java.io.Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id", unique = true, nullable = false, length = 5)
    private Long id;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "company_name", nullable = false, length = 40)
    private String companyName;

    @Column(name = "city", length = 25)
    private String city;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    private Set<Order> orders = new HashSet<>();

    public Customer(Long customerId, String companyName, String city) {
        this.customerId = customerId;
        this.companyName = companyName;
        this.city = city;
    }

    public void addOrder(Order order) {
        orders.add(order);
        order.setCustomer(this);
    }
}


