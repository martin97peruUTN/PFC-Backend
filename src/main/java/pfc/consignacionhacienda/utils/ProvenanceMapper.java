package pfc.consignacionhacienda.utils;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import pfc.consignacionhacienda.dto.ProvenanceDTO;
import pfc.consignacionhacienda.model.Provenance;

@Mapper(componentModel = "spring")
public interface ProvenanceMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProvenanceFromDto(ProvenanceDTO dto, @MappingTarget Provenance entity);
}
