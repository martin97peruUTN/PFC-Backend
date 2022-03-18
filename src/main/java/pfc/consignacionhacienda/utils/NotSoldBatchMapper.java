package pfc.consignacionhacienda.utils;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import pfc.consignacionhacienda.dto.NotSoldBatchDTO;
import pfc.consignacionhacienda.model.NotSoldBatch;

@Mapper(componentModel = "spring")
public interface NotSoldBatchMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateNotSoldBatchFromDto(NotSoldBatchDTO dto, @MappingTarget NotSoldBatch entity);
}
