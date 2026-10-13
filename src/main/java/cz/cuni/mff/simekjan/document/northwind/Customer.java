package cz.cuni.mff.simekjan.document.northwind;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.bson.codecs.pojo.annotations.BsonId;

@Data
@NoArgsConstructor
public class Customer {

    public Customer(Long customerId, String companyName, String city) {
        this.customerId = customerId;
        this.companyName = companyName;
        this.city = city;
    }

    @BsonId
    private ObjectId id;

    private Long customerId;
    private String companyName;
    private String city;
}
