package pfc.consignacionhacienda.utils;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import pfc.consignacionhacienda.dto.SoldBatchDTO;
import pfc.consignacionhacienda.model.SoldBatch;

@Mapper(componentModel = "spring")
public interface SoldBatchMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSoldBatchFromDto(SoldBatchDTO dto, @MappingTarget SoldBatch entity);
}
