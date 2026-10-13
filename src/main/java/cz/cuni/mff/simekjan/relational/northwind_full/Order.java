package cz.cuni.mff.simekjan.relational.northwind_full;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
/*
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name="Orders")
@Table(name = "orders", schema = "public")
public class Order implements java.io.Serializable {

    @Id
    @Column(name = "order_id", unique = true, nullable = false)
    private short orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ship_via")
    private Shipper shippers;

    @Temporal(TemporalType.DATE)
    @Column(name = "order_date", length = 13)
    private Date orderDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "required_date", length = 13)
    private Date requiredDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "shipped_date", length = 13)
    private Date shippedDate;

    @Column(name = "freight", precision = 8, scale = 8)
    private Float freight;

    @Column(name = "ship_name", length = 40)
    private String shipName;

    @Column(name = "ship_address", length = 60)
    private String shipAddress;

    @Column(name = "ship_city", length = 15)
    private String shipCity;

    @Column(name = "ship_region", length = 15)
    private String shipRegion;

    @Column(name = "ship_postal_code", length = 10)
    private String shipPostalCode;

    @Column(name = "ship_country", length = 15)
    private String shipCountry;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    private Set<OrderDetail> orderDetails = new HashSet<>();

    public Order(short orderId) {
        this.orderId = orderId;
    }
}
*/