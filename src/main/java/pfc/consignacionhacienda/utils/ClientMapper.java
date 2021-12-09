package pfc.consignacionhacienda.utils;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import pfc.consignacionhacienda.dto.ClientDTO;
import pfc.consignacionhacienda.model.Client;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateClientFromDto(ClientDTO dto, @MappingTarget Client entity);
}
