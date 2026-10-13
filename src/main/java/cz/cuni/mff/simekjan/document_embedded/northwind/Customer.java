package cz.cuni.mff.simekjan.document_embedded.northwind;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Customer {

    public Customer(Long customerId, String companyName, String city) {
        this.customerId = customerId;
        this.companyName = companyName;
        this.city = city;
        this.orders = new ArrayList<>();
    }

    private ObjectId id;

    private Long customerId;
    private String companyName;
    private String city;

    private List<OrderSnapshot> orders;
}
