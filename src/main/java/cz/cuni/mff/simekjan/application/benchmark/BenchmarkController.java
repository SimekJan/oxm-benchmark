package cz.cuni.mff.simekjan.application.benchmark;

import cz.cuni.mff.simekjan.api.BenchmarkApi;
import cz.cuni.mff.simekjan.application.dataset.DatasetService;
import cz.cuni.mff.simekjan.application.query.QueryService;
import cz.cuni.mff.simekjan.model.*;
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
        try {
            PostDataset202Response response = new PostDataset202Response();
            response.setId(benchmarkService.createBenchmark(createBenchmarkCommand));
            return ResponseEntity.accepted().body(response);
        } catch (IllegalArgumentException | DatasetService.DatasetNotFoundException
                 | QueryService.QuerySetNotFoundException exception) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Override
    public ResponseEntity<Void> generateData(UUID benchmarkId, GenerateBenchmarkCommand generateBenchmarkCommand) {
        return null;
    }
}
