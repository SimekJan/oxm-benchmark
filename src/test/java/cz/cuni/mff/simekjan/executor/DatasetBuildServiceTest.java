package cz.cuni.mff.simekjan.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cuni.mff.simekjan.model.CreateDatasetFromSchemaCommand;
import cz.cuni.mff.simekjan.model.DatasetRuntimeStatus;
import cz.cuni.mff.simekjan.model.DatasetSchema;
import cz.cuni.mff.simekjan.model.DatasetSchemaTablesInner;
import cz.cuni.mff.simekjan.model.DatasetSchemaTablesInnerColumnsInner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatasetBuildServiceTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void buildsAndStartsAnIsolatedDatasetRuntime() {
        DatasetBuildService service = new DatasetBuildService(
            new ObjectMapper(),
            temporaryDirectory.toString(),
            Runnable::run
        );
        UUID datasetId = UUID.randomUUID();
        CreateDatasetFromSchemaCommand command = new CreateDatasetFromSchemaCommand()
            .name("Test dataset")
            .creationType(CreateDatasetFromSchemaCommand.CreationTypeEnum.SCHEMA)
            .schema(new DatasetSchema()
                .tables(List.of(new DatasetSchemaTablesInner("people", List.of(
                    new DatasetSchemaTablesInnerColumnsInner(
                        "person-id",
                        DatasetSchemaTablesInnerColumnsInner.DatatypeEnum.INTEGER
                    )
                ))))
                .relationships(List.of()));

        try {
            DatasetRuntimeStatus submitted = service.submit(datasetId, command);

            assertEquals(DatasetRuntimeStatus.StatusEnum.BUILDING, submitted.getStatus());
            assertEquals(
                DatasetRuntimeStatus.StatusEnum.READY,
                service.getStatus(datasetId).getStatus()
            );
        } finally {
            service.stopDatasetProcesses();
        }
    }
}
