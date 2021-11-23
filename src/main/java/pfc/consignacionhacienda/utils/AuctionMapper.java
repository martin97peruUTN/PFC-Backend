package pfc.consignacionhacienda.utils;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import pfc.consignacionhacienda.dto.AuctionDTO;
import pfc.consignacionhacienda.model.Auction;

@Mapper(componentModel = "spring")
public interface AuctionMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAuctionFromDto(AuctionDTO dto, @MappingTarget Auction entity);
}
