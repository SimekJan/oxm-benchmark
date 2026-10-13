package cz.cuni.mff.simekjan.graph.northwind;

import lombok.*;
import org.neo4j.ogm.annotation.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@NodeEntity
public class Order {

    public Order(Long orderId, LocalDate orderDate) {
        this.orderId = orderId;
        this.orderDate = orderDate;
    }

    @Id
    @GeneratedValue
    private Long id;

    @Index(unique = true)
    private Long orderId;

    private LocalDate orderDate;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Relationship(type = "IS_RESPONSIBLE_FOR", direction = Relationship.Direction.INCOMING)
    private Employee employee;

    @Relationship(type = "IS_CUSTOMERS_ORDER")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Customer customer;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Relationship(type = "INCLUDES")
    private Set<Product> products = new HashSet<>();

    public void addProduct(Product p) {
        if (products.add(p)) {
            p.addOrder(this);
        }
    }
}
