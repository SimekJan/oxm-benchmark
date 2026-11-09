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
}
