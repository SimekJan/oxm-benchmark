package cz.cuni.mff.simekjan.application.operation;

import cz.cuni.mff.simekjan.api.OperationApi;
import cz.cuni.mff.simekjan.model.BenchmarkOperation;
import cz.cuni.mff.simekjan.model.UpdateOperationExactValueCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Implements API endpoints for working with operations.
 */
@RequiredArgsConstructor
@RestController
public class OperationController implements OperationApi {

    private final OperationService operationService;

    @Override
    public ResponseEntity<BenchmarkOperation> getOperation(UUID operationId) {
        try {
            return ResponseEntity.ok(operationService.getOperation(operationId));
        } catch (OperationService.OperationNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Void> updateOperation(UUID operationId, UpdateOperationExactValueCommand updateOperationExactValueCommand) {
        return null;
    }
}
