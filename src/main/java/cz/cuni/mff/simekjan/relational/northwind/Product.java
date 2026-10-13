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
@Entity(name = "Products")
@Table(name = "products",
    indexes = {
        @Index(name = "idx_product_id", columnList = "product_id")
    })
public class Product implements java.io.Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", nullable = false, length = 40)
    private String productName;

    @Column(name = "unit_price", precision = 10, scale = 2)
    private Double unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToMany(mappedBy = "products")
    private Set<Order> orders = new HashSet<>();

    @Column(name = "category")
    private String category;

    public Product(Long productId, String productName, Double unitPrice, String category) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.category = category;
    }
}
