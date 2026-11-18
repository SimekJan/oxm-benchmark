package cz.cuni.mff.simekjan.application.operation.mapper;

import cz.cuni.mff.simekjan.application.column.mapper.ColumnMapper;
import cz.cuni.mff.simekjan.application.operation.OperationEntity;
import cz.cuni.mff.simekjan.model.BenchmarkOperation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ColumnMapper.class)
public interface OperationMapper {

    BenchmarkOperation toBenchmarkOperation(OperationEntity operationEntity);
}
