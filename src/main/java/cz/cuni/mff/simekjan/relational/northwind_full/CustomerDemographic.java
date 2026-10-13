package cz.cuni.mff.simekjan.relational.northwind_full;

import cz.cuni.mff.simekjan.relational.northwind.Customer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Setter
@Getter
@Entity(name = "CustomerDemographics")
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customer_demographics"
    , schema = "public"
)
public class CustomerDemographic implements java.io.Serializable {

    @Id
    @Column(name = "customer_type_id", unique = true, nullable = false, length = 5)
    private String customerTypeId;

    @Column(name = "customer_desc")
    private String customerDesc;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "customerDemographics")
    private Set<Customer> customers = new HashSet<>();

    public CustomerDemographic(String customerTypeId) {
        this.customerTypeId = customerTypeId;
    }
}


