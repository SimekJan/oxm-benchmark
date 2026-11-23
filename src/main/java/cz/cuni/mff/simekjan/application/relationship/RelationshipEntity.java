package cz.cuni.mff.simekjan.application.relationship;

import cz.cuni.mff.simekjan.application.column.ColumnEntity;
import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.model.FunctionDistribution;
import cz.cuni.mff.simekjan.model.RelationshipType;
import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a relationship between columns in a dataset schema.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "dataset_relationships")
public class RelationshipEntity {

    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 36)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "relationship_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RelationshipType relationshipType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dataset_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private DatasetEntity dataset;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_column_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ColumnEntity sourceColumn;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_column_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ColumnEntity targetColumn;

    @Enumerated(EnumType.STRING)
    @Column
    private FunctionDistribution distribution;

    @ElementCollection
    @CollectionTable(name = "relationship_value_distribution", joinColumns = @JoinColumn(name = "relationship_id"))
    @MapKeyColumn(name = "value")
    @Column(name = "count")
    private Map<String, Integer> valueDistribution = new HashMap<>();
}
