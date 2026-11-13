package cz.cuni.mff.simekjan.application.dataset.mapper;

import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.application.table.mapper.TableMapper;
import cz.cuni.mff.simekjan.model.DatasetDetail;
import cz.cuni.mff.simekjan.model.TableBase;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.Comparator;

/**
 * Defines mappings between dataset entities and API models.
 */
@Mapper(componentModel = "spring", uses = TableMapper.class)
public interface DatasetMapper {

    DatasetDetail toDatasetDetail(DatasetEntity dataset);

    @AfterMapping
    default void sortTables(@MappingTarget DatasetDetail detail) {
        if (detail.getTables() != null) {
            detail.getTables().sort(Comparator.comparing(TableBase::getName));
        }
    }
}
