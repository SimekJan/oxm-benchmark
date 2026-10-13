package cz.cuni.mff.simekjan.relational.northwind_full;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import cz.cuni.mff.simekjan.relational.northwind.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Categories")
@Table(name = "categories", schema = "public")
public class Category implements java.io.Serializable {

    @Id
    @Column(name = "category_id", unique = true, nullable = false)
    private short categoryId;

    @Column(name = "category_name", nullable = false, length = 15)
    private String categoryName;

    @Column(name = "description")
    private String description;

    @Column(name = "picture")
    private byte[] picture;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
    private Set<Product> products = new HashSet<>();

    public Category(short categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
}