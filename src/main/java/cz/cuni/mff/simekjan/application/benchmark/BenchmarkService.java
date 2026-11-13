package cz.cuni.mff.simekjan.application.benchmark;

import cz.cuni.mff.simekjan.model.Benchmark;
import cz.cuni.mff.simekjan.model.BenchmarkBase;
import cz.cuni.mff.simekjan.model.CreateBenchmarkCommand;
import cz.cuni.mff.simekjan.model.GenerateBenchmarkCommand;

import java.util.List;
import java.util.UUID;

/**
 * Defines business logic methods to operate with Benchmarks.
 */
public interface BenchmarkService {

    List<BenchmarkBase> getAllBenchmarks();

    Benchmark getBenchmark(UUID benchmarkId);

    UUID createBenchmark(CreateBenchmarkCommand command);

    void generateData(UUID benchmarkId, GenerateBenchmarkCommand command);

    class BenchmarkNotFoundException extends RuntimeException {
        public BenchmarkNotFoundException(UUID benchmarkId) {
            super("Benchmark not found: " + benchmarkId);
        }
    }
}
