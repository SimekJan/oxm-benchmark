package cz.cuni.mff.simekjan.application.benchmark;

import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.application.query.QueryEntity;
import cz.cuni.mff.simekjan.application.table.TableEntity;
import cz.cuni.mff.simekjan.model.Benchmark;
import cz.cuni.mff.simekjan.model.BenchmarkBase;
import cz.cuni.mff.simekjan.model.DatasetDetail;
import cz.cuni.mff.simekjan.model.QueryInfo;
import cz.cuni.mff.simekjan.model.TableBase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BenchmarkService {

    private final BenchmarkRepository benchmarkRepository;

    public List<BenchmarkBase> getAllBenchmarks() {
        return benchmarkRepository.findAll().stream()
            .map(BenchmarkService::toBenchmarkBase)
            .toList();
    }

    public Benchmark getBenchmark(UUID benchmarkId) {
        BenchmarkEntity entity = benchmarkRepository.findById(benchmarkId)
            .orElseThrow(() -> new BenchmarkNotFoundException(benchmarkId));

        Benchmark benchmark = new Benchmark(
            entity.getId().toString(),
            entity.getName(),
            Benchmark.StatusEnum.fromValue(entity.getStatus())
        );
        benchmark.setDataset(toDatasetDetail(entity.getDataset()));
        benchmark.setQueries(entity.getQuerySet().getQueries().stream()
            .sorted(Comparator.comparing(QueryEntity::getName))
            .map(BenchmarkService::toQueryInfo)
            .toList());
        return benchmark;
    }

    private static BenchmarkBase toBenchmarkBase(BenchmarkEntity entity) {
        return new BenchmarkBase(
            entity.getId().toString(),
            entity.getName(),
            BenchmarkBase.StatusEnum.fromValue(entity.getStatus())
        );
    }

    private static DatasetDetail toDatasetDetail(DatasetEntity entity) {
        DatasetDetail detail = new DatasetDetail(entity.getId(), entity.getName());
        detail.setDescription(entity.getDescription());
        detail.setRuntimeStatus(DatasetDetail.RuntimeStatusEnum.fromValue(entity.getRuntimeStatus()));
        detail.setTables(entity.getTables().stream()
            .sorted(Comparator.comparing(TableEntity::getName))
            .map(table -> new TableBase(table.getId(), entity.getId(), table.getName()))
            .toList());
        return detail;
    }

    private static QueryInfo toQueryInfo(QueryEntity entity) {
        QueryInfo info = new QueryInfo(entity.getId(), entity.getName());
        info.setDescription(entity.getDescription());
        return info;
    }

    public static class BenchmarkNotFoundException extends RuntimeException {
        public BenchmarkNotFoundException(UUID benchmarkId) {
            super("Benchmark not found: " + benchmarkId);
        }
    }
}
