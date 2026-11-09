package cz.cuni.mff.simekjan.application.operation;

import cz.cuni.mff.simekjan.application.query.QueryEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OperationServiceImpl implements OperationService {

    @Override
    public OperationEntity createOperation(QueryEntity queryEntity) {
        if (queryEntity == null) {
            throw new IllegalArgumentException("Query entity is required");
        }

        OperationEntity operation = new OperationEntity();
        operation.setId(UUID.randomUUID());
        operation.setQuery(queryEntity);
        queryEntity.getOperations().add(operation);
        return operation;
    }

    @Override
    public OperationEntity createOperation(QueryEntity queryEntity, String operationType) {
        if (operationType == null || operationType.isBlank()) {
            throw new IllegalArgumentException("Operation type is required");
        }

        OperationEntity operation = createOperation(queryEntity);
        operation.setName(operationType);
        operation.setOperationType(operationType);
        return operation;
    }

    @Override
    public void addChild(OperationEntity parent, OperationEntity child, OperationChildSlot childSlot) {
        if (parent == null || child == null || childSlot == null) {
            throw new IllegalArgumentException("Parent, child, and child slot are required");
        }
        if (parent == child || parent.getQuery() == null || parent.getQuery() != child.getQuery()) {
            throw new IllegalArgumentException("Parent and child must belong to the same query");
        }
        if (child.getParentOperation() != null) {
            throw new IllegalArgumentException("Operation already has a parent");
        }
        for (OperationEntity ancestor = parent; ancestor != null; ancestor = ancestor.getParentOperation()) {
            if (ancestor == child) {
                throw new IllegalArgumentException("Operation tree cannot contain a cycle");
            }
        }
        if (parent.getChildOperations().stream().anyMatch(existing -> existing.getParentSlot() == childSlot)) {
            throw new IllegalArgumentException("Parent already has a child in slot " + childSlot);
        }

        child.getQuery().getOperations().remove(child);
        child.setParentOperation(parent);
        child.setParentSlot(childSlot);
        parent.getChildOperations().add(child);
    }
}
