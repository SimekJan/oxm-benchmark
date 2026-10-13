package cz.cuni.mff.simekjan.document.northwind;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.bson.codecs.pojo.annotations.BsonId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Order {

    public Order(Long orderId, LocalDate orderDate) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.products = new ArrayList<>();
    }

    @BsonId
    private ObjectId id;

    private Long orderId;
    private ObjectId employee;
    private ObjectId customer;
    private LocalDate orderDate;
    private List<ObjectId> products;
}
