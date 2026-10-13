package cz.cuni.mff.simekjan.relational.northwind_full;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
/*
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name="Suppliers")
@Table(name = "suppliers", schema = "public")
public class Supplier implements java.io.Serializable {

    @Id
    @Column(name = "supplier_id", unique = true, nullable = false)
    private short supplierId;

    @Column(name = "company_name", nullable = false, length = 40)
    private String companyName;

    @Column(name = "contact_name", length = 30)
    private String contactName;

    @Column(name = "contact_title", length = 30)
    private String contactTitle;

    @Column(name = "address", length = 60)
    private String address;

    @Column(name = "city", length = 15)
    private String city;

    @Column(name = "region", length = 15)
    private String region;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Column(name = "country", length = 15)
    private String country;

    @Column(name = "phone", length = 24)
    private String phone;

    @Column(name = "fax", length = 24)
    private String fax;

    @Column(name = "homepage")
    private String homepage;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "supplier")
    private Set<Product> products = new HashSet<>();

    public Supplier(short supplierId, String companyName) {
        this.supplierId = supplierId;
        this.companyName = companyName;
    }
}
*/