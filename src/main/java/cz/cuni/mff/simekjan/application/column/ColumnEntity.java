package cz.cuni.mff.simekjan.application.column;

import cz.cuni.mff.simekjan.application.table.TableEntity;
import jakarta.persistence.Column;
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

    @Column(name = "column_type", nullable = false)
    private String datatype;

    private Boolean indexed;

    @Column(name = "is_part_of_key")
    private Boolean partOfKey;

    @Column(name = "is_unused", nullable = true)
    private Boolean unused = null;

    @Column(name = "is_hidden", nullable = true)
    private Boolean hidden = null;

    private Boolean optional;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "table_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TableEntity table;
}
