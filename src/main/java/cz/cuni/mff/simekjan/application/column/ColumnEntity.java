package cz.cuni.mff.simekjan.application.column;

import cz.cuni.mff.simekjan.application.table.TableEntity;
import cz.cuni.mff.simekjan.model.ColumnDatatype;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/**
 * Represents a column in a dataset schema.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "dataset_columns")
public class ColumnEntity {

    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 36)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "column_type", nullable = false)
    private ColumnDatatype datatype;

    @Column(name = "is_indexed", nullable = false)
    private boolean indexed;

    @Column(name = "is_part_of_key", nullable = false)
    private boolean partOfKey;

    @Column(name = "is_unused", nullable = false)
    private boolean unused;

    @Column(name = "is_hidden", nullable = false)
    private boolean hidden;

    @Column(name = "is_optional", nullable = false)
    private boolean optional;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "table_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TableEntity table;
}
