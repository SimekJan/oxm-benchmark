package cz.cuni.mff.simekjan.application.benchmark;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Defines persistence operations for Benchmarks.
 */
public interface BenchmarkRepository extends JpaRepository<BenchmarkEntity, UUID> {
}
