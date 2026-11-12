package cz.cuni.mff.simekjan.application.benchmark;

import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.application.dataset.DatasetService;
import cz.cuni.mff.simekjan.application.query.QueryEntity;
import cz.cuni.mff.simekjan.application.query.QueryService;
import cz.cuni.mff.simekjan.application.query.QuerySetEntity;
import cz.cuni.mff.simekjan.application.table.TableEntity;
import cz.cuni.mff.simekjan.generator.CsvGenerator;
import cz.cuni.mff.simekjan.generator.Generator;
import cz.cuni.mff.simekjan.generator.JsonGenerator;
import cz.cuni.mff.simekjan.model.Benchmark;
import cz.cuni.mff.simekjan.model.BenchmarkBase;
import cz.cuni.mff.simekjan.model.CreateBenchmarkCommand;
import cz.cuni.mff.simekjan.model.DatasetDetail;
import cz.cuni.mff.simekjan.model.GenerateBenchmarkCommand;
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
public class BenchmarkServiceImpl implements BenchmarkService {

    private final BenchmarkRepository benchmarkRepository;
    private final DatasetService datasetService;
    private final QueryService queryService;

    public List<BenchmarkBase> getAllBenchmarks() {
        return benchmarkRepository.findAll().stream()
            .map(BenchmarkServiceImpl::toBenchmarkBase)
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
            .map(BenchmarkServiceImpl::toQueryInfo)
            .toList());
        return benchmark;
    }

    @Transactional
    public UUID createBenchmark(CreateBenchmarkCommand command) {
        if (command == null || command.getName() == null || command.getName().isBlank()) {
            throw new IllegalArgumentException("Benchmark name must not be blank");
        }

        UUID datasetId = parseId(command.getDatasetId(), "datasetId");
        UUID querySetId = parseId(command.getQuerySetId(), "querySetId");
        DatasetEntity dataset = datasetService.getDatasetEntity(datasetId);
        QuerySetEntity querySet = queryService.getQuerySetEntity(querySetId);
        if (!querySet.getDataset().getId().equals(datasetId)) {
            throw new IllegalArgumentException("Query set does not belong to dataset: " + datasetId);
        }

        BenchmarkEntity entity = new BenchmarkEntity();
        entity.setId(UUID.randomUUID());
        entity.setName(command.getName());
        entity.setStatus(Benchmark.StatusEnum.READY.getValue());
        entity.setDataset(dataset);
        entity.setQuerySet(querySet);
        return benchmarkRepository.save(entity).getId();
    }

    @Transactional
    public void generateData(UUID benchmarkId, GenerateBenchmarkCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Generation command is required");
        }

        BenchmarkEntity benchmark = benchmarkRepository.findById(benchmarkId)
            .orElseThrow(() -> new BenchmarkNotFoundException(benchmarkId));
        var dataset = datasetService.getDatasetEntity(benchmark.getDataset().getId());
        if (command.getFormat() == null) {
            throw new IllegalArgumentException("File format is required");
        }
        QuerySetEntity querySet = benchmark.getQuerySet();
        if (dataset.getQuerySets().stream().noneMatch(candidate -> candidate.getId().equals(querySet.getId()))) {
            throw new IllegalArgumentException("Query set does not belong to dataset: " + querySet.getId());
        }

        Generator generator = switch (command.getFormat()) {
            case CSV -> new CsvGenerator();
            case JSON -> new JsonGenerator();
        };
        generator.generate(dataset, querySet);
    }

    private static UUID parseId(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(fieldName + " must be a UUID", exception);
        }
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

}
