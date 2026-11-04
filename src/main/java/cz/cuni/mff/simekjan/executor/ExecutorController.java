package cz.cuni.mff.simekjan.executor;

import cz.cuni.mff.simekjan.model.DatasetRuntimeStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/internal/datasets")
public class ExecutorController {

    private final DatasetBuildService datasetBuildService;

    @PostMapping("/{datasetId}/entities")
    public ResponseEntity<Void> uploadEntitySources(
        @PathVariable UUID datasetId,
        @RequestBody JavaSourceBundle bundle
    ) {
        try {
            datasetBuildService.uploadSources(datasetId, "entities", bundle);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{datasetId}/queries")
    public ResponseEntity<Void> uploadQuerySources(
        @PathVariable UUID datasetId,
        @RequestBody JavaSourceBundle bundle
    ) {
        try {
            datasetBuildService.uploadSources(datasetId, "queries", bundle);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{datasetId}/start")
    public ResponseEntity<DatasetRuntimeStatus> startDataset(@PathVariable UUID datasetId) {
        try {
            return ResponseEntity.accepted().body(datasetBuildService.start(datasetId));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{datasetId}")
    public ResponseEntity<DatasetRuntimeStatus> getDatasetStatus(@PathVariable UUID datasetId) {
        DatasetRuntimeStatus status = datasetBuildService.getStatus(datasetId);
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(status);
    }
}
