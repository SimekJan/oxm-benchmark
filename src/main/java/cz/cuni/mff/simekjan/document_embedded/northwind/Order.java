package cz.cuni.mff.simekjan.document_embedded.northwind;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Same as in 'document' version
 */
@Data
@NoArgsConstructor
public class Order {

    public Order(Long orderId, LocalDate orderDate) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.products = new ArrayList<>();
    }

    private ObjectId id;

    private Long orderId;
    private ObjectId employee;
    private ObjectId customer;
    private LocalDate orderDate;
    private List<ProductSnapshot> products;
}
