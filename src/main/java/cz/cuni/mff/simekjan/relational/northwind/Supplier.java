package cz.cuni.mff.simekjan.relational.northwind;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Suppliers")
@Table(name = "suppliers",
    indexes = {
        @Index(name = "idx_supplier_id", columnList = "supplier_id")
    }
)
public class Supplier implements java.io.Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "supplier_id")
    private Long supplierId;

    @Column(name = "company_name", nullable = false, length = 40)
    private String companyName;

    @Column(name = "city", length = 25)
    private String city;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "supplier")
    private Set<Product> products = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "supplier_relationship",
        joinColumns = @JoinColumn(name = "supplier_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "supplied_to_id", referencedColumnName = "id")
    )
    private Set<Supplier> suppliesTo = new HashSet<>();

    @ManyToMany(mappedBy = "suppliesTo")
    private Set<Supplier> suppliedBy = new HashSet<>();

    public Supplier(Long supplierId, String companyName, String city) {
        this.supplierId = supplierId;
        this.companyName = companyName;
        this.city = city;
    }

    public void addSuppliesTo(Supplier target) {
        this.suppliesTo.add(target);
        target.getSuppliedBy().add(this);
    }

    public void addProduct(Product product) {
        products.add(product);
        product.setSupplier(this);
    }
}
