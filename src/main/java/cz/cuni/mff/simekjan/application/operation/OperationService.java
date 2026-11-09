package cz.cuni.mff.simekjan.application.operation;

import cz.cuni.mff.simekjan.application.query.QueryEntity;

public interface OperationService {

    OperationEntity createOperation(QueryEntity queryEntity);
}
