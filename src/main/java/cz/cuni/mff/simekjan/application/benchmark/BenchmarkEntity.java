package cz.cuni.mff.simekjan.application.benchmark;

import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.application.query.QuerySetEntity;
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
@Table(name = "benchmarks")
public class BenchmarkEntity {

    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 36)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 16)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dataset_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private DatasetEntity dataset;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "query_set_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private QuerySetEntity querySet;
}
