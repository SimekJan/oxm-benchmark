package cz.cuni.mff.simekjan.document_embedded.northwind;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

/**
 * Same as in 'document' version
 */
@Data
@NoArgsConstructor
public class Product {

    public Product(Long productId, String productName, Float unitPrice, String category) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.category = category;
    }

    private ObjectId id;

    private Long productId;
    private String productName;
    private Float unitPrice;
    private String category;
    private ObjectId supplier;
}
