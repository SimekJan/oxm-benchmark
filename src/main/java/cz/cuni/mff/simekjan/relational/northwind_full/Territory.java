package cz.cuni.mff.simekjan.relational.northwind_full;

import cz.cuni.mff.simekjan.relational.northwind.Employee;
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
@Entity(name = "Territories")
@Table(name = "territories", schema = "public")
public class Territory implements java.io.Serializable {

    @Id
    @Column(name = "territory_id", unique = true, nullable = false, length = 20)
    private String territoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @Column(name = "territory_description", nullable = false, length = 60)
    private String territoryDescription;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "territories")
    private Set<Employee> employees = new HashSet<>();

    public Territory(String territoryId, Region region, String territoryDescription) {
        this.territoryId = territoryId;
        this.region = region;
        this.territoryDescription = territoryDescription;
    }
}
