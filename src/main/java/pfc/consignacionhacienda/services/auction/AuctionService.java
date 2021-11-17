package pfc.consignacionhacienda.services.auction;

import org.springframework.data.domain.Page;
import pfc.consignacionhacienda.dto.AuctionDTO;
import pfc.consignacionhacienda.exceptions.HttpUnauthorizedException;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.exceptions.user.InvalidCredentialsException;
import pfc.consignacionhacienda.model.Auction;

import java.util.List;

public interface AuctionService {
    Auction saveAuction(Auction auction) throws InvalidCredentialsException, HttpUnauthorizedException;
    List<Auction> getAllAuctions();
    Page<Auction> getAllAuctionsByPage(Integer page, Integer limit) throws InvalidCredentialsException;
    List<Auction> getAllNotDeletedAuctions();
    Page<Auction> getAllNotDeletedAuctionsByPage(Integer page, Integer limit) throws InvalidCredentialsException;
    Auction getAuctionById(Integer id) throws AuctionNotFoundException;
    Auction deleteAuctionById(Integer id) throws AuctionNotFoundException, HttpUnauthorizedException;
//    Auction updateAuctionById(Integer id, Map<Object, Object> changes) throws InvalidCredentialsException, AuctionNotFoundException;
    Auction updateAuctionById(Integer id, AuctionDTO changes) throws InvalidCredentialsException, AuctionNotFoundException, HttpUnauthorizedException;
}
