package cz.cuni.mff.simekjan.application.operation;

import cz.cuni.mff.simekjan.application.query.QueryEntity;

public interface OperationService {

    OperationEntity createOperation(QueryEntity queryEntity);

    OperationEntity createOperation(QueryEntity queryEntity, String operationType);

    void addChild(OperationEntity parent, OperationEntity child, OperationChildSlot childSlot);
}
