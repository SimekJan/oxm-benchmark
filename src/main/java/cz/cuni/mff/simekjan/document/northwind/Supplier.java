package cz.cuni.mff.simekjan.document.northwind;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Supplier {

    public Supplier(Long supplierId, String companyName, String city) {
        this.supplierId = supplierId;
        this.id = new ObjectId();
        this.companyName = companyName;
        this.city = city;
        this.suppliedBy = new ArrayList<>();
    }

    @BsonId
    private ObjectId id;

    private Long supplierId;
    private String companyName;
    private String city;
    private List<ObjectId> suppliedBy;
}
