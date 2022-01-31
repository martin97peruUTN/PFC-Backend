package pfc.consignacionhacienda.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pfc.consignacionhacienda.exceptions.HttpForbidenException;
import pfc.consignacionhacienda.exceptions.HttpUnauthorizedException;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.exceptions.user.InvalidCredentialsException;
import pfc.consignacionhacienda.exceptions.user.UserNotFoundException;
import pfc.consignacionhacienda.services.auction.AuctionService;
import pfc.consignacionhacienda.utils.ErrorResponse;

import java.time.Instant;

@RestController
@RequestMapping("/api/auction-user")
public class AuctionUserRest {

    private static final Logger logger = LoggerFactory.getLogger(AuctionRest.class);

    @Autowired
    private AuctionService auctionService;

    @GetMapping("/own/{id}")
    public ResponseEntity<?> getOwnAuctions(
            @PathVariable Integer id,
            @RequestParam(name="page", defaultValue = "0") Integer page,
            @RequestParam(name="limit", defaultValue = "10") Integer limit
    ){
        try {
            return ResponseEntity.ok(auctionService.getOwnNotDeletedAuctionsByPageAndId(id, page, limit));
        }catch (HttpUnauthorizedException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/others/{id}")
    public ResponseEntity<?> getOthersAuctions(
            @PathVariable Integer id,
            @RequestParam(name="page", defaultValue = "0") Integer page,
            @RequestParam(name="limit", defaultValue = "10") Integer limit
    ){
        try {
            return ResponseEntity.ok(auctionService.getOthersNotDeletedAuctionsByPageAndId(id, page, limit));
        }catch (HttpUnauthorizedException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping()
    public ResponseEntity<?> getAllNotDeletedAuctions(
            @RequestParam(name="page", defaultValue = "0") Integer page,
            @RequestParam(name="limit", defaultValue = "10") Integer limit){
        try {
            return ResponseEntity.ok(auctionService.getAllNotDeletedAndNotFinishedAuctionsByPage(page, limit));
        } catch (InvalidCredentialsException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users/{auctionId}")
    ResponseEntity<?> getUsersFromAuction(@PathVariable Integer auctionId){
        try {
            return ResponseEntity.ok(auctionService.getUsersByAuctionId(auctionId));
        } catch (AuctionNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/assignment/{idAuction}/adduser/{idUser}")
    ResponseEntity<?> addUserToAuction(@PathVariable Integer idAuction, @PathVariable Integer idUser){
        try {
            return ResponseEntity.ok(auctionService.addUserToAuction(idAuction, idUser));
        } catch (AuctionNotFoundException | UserNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (HttpUnauthorizedException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/assignment/{idAuction}/deleteuser/{idUser}")
    ResponseEntity<?> deleteUserFromAuction(@PathVariable Integer idAuction, @PathVariable Integer idUser){
        try {
            return ResponseEntity.ok(auctionService.removeUserFromAuction(idAuction, idUser));
        } catch (AuctionNotFoundException | UserNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }catch (HttpForbidenException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.FORBIDDEN);
        } catch (HttpUnauthorizedException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/history/{userId}")
    ResponseEntity<?> getFinishedAuctions(@PathVariable Integer userId,
                                          @RequestParam(name="page", defaultValue = "0") Integer page,
                                          @RequestParam(name="limit", defaultValue = "10") Integer limit,
                                          @RequestParam(name="first-date", defaultValue = "null") String since,
                                          @RequestParam(name="last-date", defaultValue = "null") String until){
        if(since.equals("null")){
            since = "1900-01-01T00:00:00Z";
        }
        if(until.equals("null")){
            until = Instant.now().toString();
        }
        try {
            return ResponseEntity.ok(auctionService.getFishedAuctions(userId, Instant.parse(since) ,Instant.parse(until), page, limit));
        } catch (UserNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
