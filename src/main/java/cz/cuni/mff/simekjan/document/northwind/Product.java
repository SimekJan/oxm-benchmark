package cz.cuni.mff.simekjan.document.northwind;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.bson.codecs.pojo.annotations.BsonId;

@Data
@NoArgsConstructor
public class Product {

    public Product(Long productId, String productName, Float unitPrice, String category) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.category = category;
    }

    @BsonId
    private ObjectId id;

    private Long productId;
    private String productName;
    private Float unitPrice;
    private String category;
    private ObjectId supplier;
}
