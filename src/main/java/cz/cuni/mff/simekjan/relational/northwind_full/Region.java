package cz.cuni.mff.simekjan.relational.northwind_full;

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
@Entity(name = "Regions")
@Table(name = "region", schema = "public")
public class Region implements java.io.Serializable {

    @Id
    @Column(name = "region_id", unique = true, nullable = false)
    private short regionId;

    @Column(name = "region_description", nullable = false, length = 60)
    private String regionDescription;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "region")
    private Set<Territory> territories = new HashSet<>();

    public Region(short regionId, String regionDescription) {
        this.regionId = regionId;
        this.regionDescription = regionDescription;
    }
}
