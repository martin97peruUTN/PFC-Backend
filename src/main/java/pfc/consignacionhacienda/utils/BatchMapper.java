package pfc.consignacionhacienda.utils;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import pfc.consignacionhacienda.dto.BatchDTO;
import pfc.consignacionhacienda.model.Batch;

@Mapper(componentModel = "spring")
public interface BatchMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateBatchFromDto(BatchDTO dto, @MappingTarget Batch entity);
}
