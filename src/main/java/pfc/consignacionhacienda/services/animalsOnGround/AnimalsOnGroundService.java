package pfc.consignacionhacienda.services.animalsOnGround;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pfc.consignacionhacienda.dto.AnimalsOnGroundDTO;
import pfc.consignacionhacienda.exceptions.BadHttpRequest;
import pfc.consignacionhacienda.exceptions.HttpForbidenException;
import pfc.consignacionhacienda.exceptions.animalsOnGround.AnimalsOnGroundNotFound;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.model.AnimalsOnGround;

public interface AnimalsOnGroundService {
    Page<AnimalsOnGround> getAnimalsOnGroundByAuction(Integer auctionId, Pageable of);
    Page<AnimalsOnGround> getAnimalsOnGroundByAuctionSold(Integer auctionId, Pageable of);
    Page<AnimalsOnGround> getAnimalsOnGroundByAuctionNotSold(Integer auctionId, Pageable of);
    Page<AnimalsOnGround> getAnimalsOnGroundByAuctionForSell(Integer auctionId, Pageable of);

    AnimalsOnGround deleteById(Integer id) throws AnimalsOnGroundNotFound;

    AnimalsOnGround getAnimalsOnGroundNotDeletedById(Integer animalsId);

    AnimalsOnGround updateAnimalsOnGround(Integer animalsId, AnimalsOnGroundDTO animalsOnGroundDTO) throws BadHttpRequest, AnimalsOnGroundNotFound, AuctionNotFoundException, HttpForbidenException;

    AnimalsOnGround findById(Integer animalsOnGroundId) throws AnimalsOnGroundNotFound;

    AnimalsOnGround findByIdNotDeleted(Integer animalsOnGroundId) throws AnimalsOnGroundNotFound;

    AnimalsOnGround save(AnimalsOnGround animalsOnGround);
}
