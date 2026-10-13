package cz.cuni.mff.simekjan.document_embedded.northwind;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

/**
 * Same as in 'document' version, nested inside Supplier
 */
@Data
@NoArgsConstructor
public class ProductSnapshot {

    public ProductSnapshot(Product product) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.unitPrice = product.getUnitPrice();
        this.category = product.getCategory();
    }

    private ObjectId id;

    private Long productId;
    private String productName;
    private Float unitPrice;
    private String category;
}
