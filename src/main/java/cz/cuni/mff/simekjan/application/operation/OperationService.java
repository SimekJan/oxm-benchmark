package cz.cuni.mff.simekjan.application.operation;

import cz.cuni.mff.simekjan.application.query.QueryEntity;
import cz.cuni.mff.simekjan.model.BenchmarkOperation;
import cz.cuni.mff.simekjan.model.OperationInfo;

import java.util.List;
import java.util.UUID;

/**
 * Defines business logic methods to operate with query operations.
 */
public interface OperationService {

    BenchmarkOperation getOperation(UUID operationId);

    OperationEntity createOperation(QueryEntity queryEntity);

    OperationEntity createOperation(QueryEntity queryEntity, String operationType);

    void addChild(OperationEntity parent, OperationEntity child, OperationChildSlot childSlot);

    List<OperationInfo> getOperationsForColumn(UUID columnId);

    class OperationNotFoundException extends RuntimeException {
        public OperationNotFoundException(UUID operationId) {
            super("Operation not found: " + operationId);
        }
    }
}
