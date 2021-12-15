package pfc.consignacionhacienda.services.animalsOnGround;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pfc.consignacionhacienda.exceptions.animalsOnGround.AnimalsOnGroundNotFound;
import pfc.consignacionhacienda.model.AnimalsOnGround;

public interface AnimalsOnGroundService {
    Page<AnimalsOnGround> getAnimalsOnGroundByAuction(Integer auctionId, Pageable of);
    Page<AnimalsOnGround> getAnimalsOnGroundByAuctionSold(Integer auctionId, Pageable of);
    Page<AnimalsOnGround> getAnimalsOnGroundByAuctionNotSold(Integer auctionId, Pageable of);
    Page<AnimalsOnGround> getAnimalsOnGroundByAuctionForSell(Integer auctionId, Pageable of);

    AnimalsOnGround deleteById(Integer id) throws AnimalsOnGroundNotFound;
}
