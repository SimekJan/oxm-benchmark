package cz.cuni.mff.simekjan.application.operation;

import cz.cuni.mff.simekjan.application.query.QueryEntity;
import cz.cuni.mff.simekjan.model.OperationInfo;

import java.util.List;
import java.util.UUID;

public interface OperationService {

    OperationEntity createOperation(QueryEntity queryEntity);

    OperationEntity createOperation(QueryEntity queryEntity, String operationType);

    void addChild(OperationEntity parent, OperationEntity child, OperationChildSlot childSlot);

    List<OperationInfo> getOperationsForColumn(UUID columnId);
}
