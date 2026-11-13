package cz.cuni.mff.simekjan.application.operation.mapper;

import cz.cuni.mff.simekjan.application.operation.OperationEntity;
import org.mapstruct.Mapper;
import cz.cuni.mff.simekjan.model.OperationInfo;

/**
 * Defines mappings between operation entities and API models.
 */
@Mapper(componentModel = "spring")
public interface OperationMapper {

    OperationInfo toOperationInfo(OperationEntity operation);
}
