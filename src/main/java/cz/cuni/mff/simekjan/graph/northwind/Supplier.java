package cz.cuni.mff.simekjan.graph.northwind;

import lombok.*;
import org.neo4j.ogm.annotation.*;

import java.util.Set;
import java.util.HashSet;

@Data
@NoArgsConstructor
@NodeEntity
public class Supplier {

    public Supplier(Long supplierId, String name, String city) {
        this.supplierId = supplierId;
        this.companyName = name;
        this.city = city;
    }

    @Id
    @GeneratedValue
    private Long id;

    @Index(unique = true)
    private Long supplierId;

    private String companyName;

    private String city;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Relationship(type = "IS_PRODUCED_BY", direction = Relationship.Direction.INCOMING)
    private Set<Product> products = new HashSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Relationship(type = "SUPPLIES_TO")
    private Set<Supplier> suppliesTo = new HashSet<>();

    public void addProduct(Product p) {
        if (products.add(p)) {
            p.setSupplier(this);
        }
    }

    public void addSuppliesTo(Supplier s) {
        suppliesTo.add(s);
    }
}
