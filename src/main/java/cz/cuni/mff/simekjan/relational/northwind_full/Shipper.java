package cz.cuni.mff.simekjan.relational.northwind_full;

import cz.cuni.mff.simekjan.relational.northwind.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Shippers")
@Table(name = "shippers", schema = "public")
public class Shipper implements java.io.Serializable {

    @Id
    @Column(name = "shipper_id", unique = true, nullable = false)
    private short shipperId;

    @Column(name = "company_name", nullable = false, length = 40)
    private String companyName;

    @Column(name = "phone", length = 24)
    private String phone;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "shippers")
    private Set<Order> orders = new HashSet<>();

    public Shipper(short shipperId, String companyName) {
        this.shipperId = shipperId;
        this.companyName = companyName;
    }
}
