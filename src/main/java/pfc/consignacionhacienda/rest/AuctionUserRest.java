package pfc.consignacionhacienda.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pfc.consignacionhacienda.exceptions.HttpForbidenException;
import pfc.consignacionhacienda.exceptions.HttpUnauthorizedException;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.exceptions.user.InvalidCredentialsException;
import pfc.consignacionhacienda.exceptions.user.UserNotFoundException;
import pfc.consignacionhacienda.model.Auction;
import pfc.consignacionhacienda.model.User;
import pfc.consignacionhacienda.services.auction.AuctionService;

import java.util.List;

@RestController
@RequestMapping("/api/auction-user")
public class AuctionUserRest {

    private static final Logger logger = LoggerFactory.getLogger(AuctionRest.class);

    @Autowired
    private AuctionService auctionService;

    @GetMapping("/own/{id}")
    public ResponseEntity<Page<Auction>> getOwnAuctions(
            @PathVariable Integer id,
            @RequestParam(name="page", defaultValue = "0") Integer page,
            @RequestParam(name="limit", defaultValue = "10") Integer limit
    ){
        try {
            return ResponseEntity.ok(auctionService.getOwnNotDeletedAuctionsByPageAndId(id, page, limit));
        }catch (HttpUnauthorizedException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/others/{id}")
    public ResponseEntity<Page<Auction>> getOthersAuctions(
            @PathVariable Integer id,
            @RequestParam(name="page", defaultValue = "0") Integer page,
            @RequestParam(name="limit", defaultValue = "10") Integer limit
    ){
        try {
            return ResponseEntity.ok(auctionService.getOthersNotDeletedAuctionsByPageAndId(id, page, limit));
        }catch (HttpUnauthorizedException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping()
    public ResponseEntity<Page<Auction>> getAllNotDeletedAuctions(
            @RequestParam(name="page", defaultValue = "0") Integer page,
            @RequestParam(name="limit", defaultValue = "10") Integer limit){
        try {
            return ResponseEntity.ok(auctionService.getAllNotDeletedAndNotFinishedAuctionsByPage(page, limit));
        } catch (InvalidCredentialsException e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/users/{auctionId}")
    ResponseEntity<List<User>> getUsersFromAuction(@PathVariable Integer auctionId){
        try {
            return ResponseEntity.ok(auctionService.getUsersByAuctionId(auctionId));
        } catch (AuctionNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/assignment/{idAuction}/adduser/{idUser}")
    ResponseEntity<Auction> addUserToAuction(@PathVariable Integer idAuction, @PathVariable Integer idUser){
        try {
            return ResponseEntity.ok(auctionService.addUserToAuction(idAuction, idUser));
        } catch (AuctionNotFoundException | UserNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (HttpUnauthorizedException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/assignment/{idAuction}/deleteuser/{idUser}")
    ResponseEntity<Auction> deleteUserFromAuction(@PathVariable Integer idAuction, @PathVariable Integer idUser){
        try {
            return ResponseEntity.ok(auctionService.removeUserFromAuction(idAuction, idUser));
        } catch (AuctionNotFoundException | UserNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }catch (HttpForbidenException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (HttpUnauthorizedException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
