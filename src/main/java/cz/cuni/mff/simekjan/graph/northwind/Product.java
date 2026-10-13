package cz.cuni.mff.simekjan.graph.northwind;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.neo4j.ogm.annotation.*;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@NodeEntity
public class Product {

    public Product(Long productId, String name, Double unitPrice, String category) {
        this.productId = productId;
        this.productName = name;
        this.unitPrice = unitPrice;
        this.category = category;
    }

    @Id
    @GeneratedValue
    private Long id;

    @Index(unique = true)
    private Long productId;

    private String productName;

    private Double unitPrice;

    private String category;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Relationship(type = "IS_PRODUCED_BY")
    private Supplier supplier;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Relationship(type = "INCLUDES", direction = Relationship.Direction.INCOMING)
    private Set<Order> orders = new HashSet<>();

    public void addOrder(Order o) {
        orders.add(o);
    }
}
