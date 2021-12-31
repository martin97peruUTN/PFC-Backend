package pfc.consignacionhacienda.services.auction;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import pfc.consignacionhacienda.dto.AuctionDTO;
import pfc.consignacionhacienda.exceptions.HttpForbidenException;
import pfc.consignacionhacienda.exceptions.HttpUnauthorizedException;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.exceptions.user.InvalidCredentialsException;
import pfc.consignacionhacienda.exceptions.user.UserNotFoundException;
import pfc.consignacionhacienda.model.Auction;
import pfc.consignacionhacienda.model.NotSoldBatch;
import pfc.consignacionhacienda.model.User;

import java.util.List;

public interface AuctionService {
    Auction saveAuction(Auction auction) throws InvalidCredentialsException, HttpUnauthorizedException;
    List<Auction> getAllAuctions();
    Page<Auction> getAllAuctionsByPage(Integer page, Integer limit) throws InvalidCredentialsException;
    List<Auction> getAllNotDeletedAuctions();
    Page<Auction> getAllNotDeletedAuctionsByPage(Integer page, Integer limit) throws InvalidCredentialsException;
    Page<Auction> getOwnNotDeletedAuctionsByPageAndId(Integer id, Integer page, Integer limit) throws InvalidCredentialsException, HttpUnauthorizedException;
    Page<Auction> getOthersNotDeletedAuctionsByPageAndId(Integer id, Integer page, Integer limit) throws InvalidCredentialsException, HttpUnauthorizedException;
    Page<Auction> getAllNotDeletedAndNotFinishedAuctionsByPage(Integer page, Integer limit) throws InvalidCredentialsException;
    Auction getAuctionById(Integer id) throws AuctionNotFoundException;
    Auction deleteAuctionById(Integer id) throws AuctionNotFoundException, HttpUnauthorizedException, HttpForbidenException;
//    Auction updateAuctionById(Integer id, Map<Object, Object> changes) throws InvalidCredentialsException, AuctionNotFoundException;
    Auction updateAuctionById(Integer id, AuctionDTO changes) throws InvalidCredentialsException, AuctionNotFoundException, HttpUnauthorizedException;
    List<User> getUsersByAuctionId(Integer auctionID) throws AuctionNotFoundException;
    Auction removeUserFromAuction(Integer auctionId, Integer UserId) throws AuctionNotFoundException, UserNotFoundException, HttpForbidenException, HttpUnauthorizedException;
    Auction addUserToAuction(Integer auctionId, Integer userId) throws AuctionNotFoundException, UserNotFoundException, HttpUnauthorizedException;

    Auction finishAuctionById(Integer id) throws AuctionNotFoundException, HttpUnauthorizedException;

    Auction resumeAuctionById(Integer id) throws AuctionNotFoundException, HttpUnauthorizedException;
}
