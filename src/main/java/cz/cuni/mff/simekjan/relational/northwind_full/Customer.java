package cz.cuni.mff.simekjan.relational.northwind_full;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/*
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="Customers")
@Table(name="customers"
    ,schema="public"
)
public class Customer implements java.io.Serializable {

    @Id
    @Column(name="customer_id", unique=true, nullable=false, length=5)
    private String customerId;

    @Column(name="company_name", nullable=false, length=40)
    private String companyName;

    @Column(name="contact_name", length=30)
    private String contactName;

    @Column(name="contact_title", length=30)
    private String contactTitle;

    @Column(name="address", length=60)
    private String address;

    @Column(name="city", length=15)
    private String city;

    @Column(name="region", length=15)
    private String region;

    @Column(name="postal_code", length=10)
    private String postalCode;

    @Column(name="country", length=15)
    private String country;

    @Column(name="phone", length=24)
    private String phone;

    @Column(name="fax", length=24)
    private String fax;

    @OneToMany(fetch=FetchType.LAZY, mappedBy="customer")
    private Set<Order> orders = new HashSet<>();

    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="customer_customer_demo", schema="public", joinColumns = {
            @JoinColumn(name="customer_id", nullable=false, updatable=false) }, inverseJoinColumns = {
            @JoinColumn(name="customer_type_id", nullable=false, updatable=false) })
     private Set<CustomerDemographic> customerDemographics = new HashSet<>();

    public Customer(String customerId, String companyName) {
        this.customerId = customerId;
        this.companyName = companyName;
    }
}
*/


