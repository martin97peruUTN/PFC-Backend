package pfc.consignacionhacienda.utils;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import pfc.consignacionhacienda.dto.AnimalsOnGroundDTO;
import pfc.consignacionhacienda.model.AnimalsOnGround;

@Mapper(componentModel = "spring")
public interface AnimalsOnGoundMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAnimalsOnGroundFromDto(AnimalsOnGroundDTO dto, @MappingTarget AnimalsOnGround entity);
}
