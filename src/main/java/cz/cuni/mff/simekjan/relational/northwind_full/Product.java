package cz.cuni.mff.simekjan.relational.northwind_full;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
/*
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name="Products")
@Table(name = "products", schema = "public")
public class Product implements java.io.Serializable {

    @Id
    @Column(name = "product_id", unique = true, nullable = false)
    private short productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(name = "product_name", nullable = false, length = 40)
    private String productName;

    @Column(name = "quantity_per_unit", length = 20)
    private String quantityPerUnit;

    @Column(name = "unit_price", precision = 8, scale = 8)
    private Float unitPrice;

    @Column(name = "units_in_stock")
    private Short unitsInStock;

    @Column(name = "units_on_order")
    private Short unitsOnOrder;

    @Column(name = "reorder_level")
    private Short reorderLevel;

    @Column(name = "discontinued", nullable = false)
    private int discontinued;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "products")
    private Set<OrderDetail> orderDetails = new HashSet<>();

    public Product(short productId, String productName, int discontinued) {
        this.productId = productId;
        this.productName = productName;
        this.discontinued = discontinued;
    }
}
*/