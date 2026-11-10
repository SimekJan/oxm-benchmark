package cz.cuni.mff.simekjan.application.benchmark;

import cz.cuni.mff.simekjan.api.BenchmarkApi;
import cz.cuni.mff.simekjan.model.Benchmark;
import cz.cuni.mff.simekjan.model.BenchmarkBase;
import cz.cuni.mff.simekjan.model.CreateBenchmarkCommand;
import cz.cuni.mff.simekjan.model.PostDataset202Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BenchmarkController implements BenchmarkApi {

    private final BenchmarkService benchmarkService;

    @Override
    public ResponseEntity<List<BenchmarkBase>> getAllBenchmarks() {
        return ResponseEntity.ok(benchmarkService.getAllBenchmarks());
    }

    @Override
    public ResponseEntity<Benchmark> getBenchmark(UUID benchmarkId) {
        try {
            return ResponseEntity.ok(benchmarkService.getBenchmark(benchmarkId));
        } catch (BenchmarkService.BenchmarkNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<PostDataset202Response> postBenchmark(CreateBenchmarkCommand createBenchmarkCommand) {
        return null;
    }
}
