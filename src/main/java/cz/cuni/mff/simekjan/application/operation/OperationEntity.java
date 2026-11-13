package cz.cuni.mff.simekjan.application.operation;

import cz.cuni.mff.simekjan.application.column.ColumnEntity;
import cz.cuni.mff.simekjan.application.query.QueryEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a specific operation associated with a query.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "query_operations")
public class OperationEntity {

    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 36)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "results_count", nullable = false)
    private Integer resultsCount;

    @Column(name = "operation_type", nullable = false)
    private String operationType;

    private String value;

    private String value1;

    private String value2;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "query_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private QueryEntity query;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "column_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ColumnEntity column;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "column1_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ColumnEntity column1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "column2_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ColumnEntity column2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_operation_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private OperationEntity parentOperation;

    @Enumerated(EnumType.STRING)
    @Column(name = "parent_slot")
    private OperationChildSlot parentSlot;

    @OneToMany(mappedBy = "parentOperation", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<OperationEntity> childOperations = new ArrayList<>();
}
