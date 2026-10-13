package cz.cuni.mff.simekjan.relational.northwind_full;

import cz.cuni.mff.simekjan.relational.northwind.Order;
import cz.cuni.mff.simekjan.relational.northwind.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "OrderDetails")
@Table(name = "order_details", schema = "public")
public class OrderDetail implements java.io.Serializable {

    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name = "orderId", column = @Column(name = "order_id", nullable = false)),
        @AttributeOverride(name = "productId", column = @Column(name = "product_id", nullable = false))
    })
    private OrderDetailsId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, insertable = false, updatable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, insertable = false, updatable = false)
    private Product products;

    @Column(name = "unit_price", nullable = false, precision = 8, scale = 8)
    private float unitPrice;

    @Column(name = "quantity", nullable = false)
    private short quantity;

    @Column(name = "discount", nullable = false, precision = 8, scale = 8)
    private float discount;
}
