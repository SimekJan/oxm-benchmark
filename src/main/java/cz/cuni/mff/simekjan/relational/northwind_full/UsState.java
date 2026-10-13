package cz.cuni.mff.simekjan.relational.northwind_full;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "UsStates")
@Table(name = "us_states", schema = "public")
public class UsState implements java.io.Serializable {

    @Id
    @Column(name = "state_id", unique = true, nullable = false)
    private short stateId;

    @Column(name = "state_name", length = 100)
    private String stateName;

    @Column(name = "state_abbr", length = 2)
    private String stateAbbr;

    @Column(name = "state_region", length = 50)
    private String stateRegion;

    public UsState(short stateId) {
        this.stateId = stateId;
    }
}
