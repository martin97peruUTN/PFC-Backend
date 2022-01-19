package pfc.consignacionhacienda.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pfc.consignacionhacienda.dto.AuctionDTO;
import pfc.consignacionhacienda.exceptions.HttpForbidenException;
import pfc.consignacionhacienda.exceptions.HttpUnauthorizedException;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.exceptions.user.InvalidCredentialsException;
import pfc.consignacionhacienda.model.Auction;
import pfc.consignacionhacienda.services.auction.AuctionService;
import pfc.consignacionhacienda.services.user.UserService;
import pfc.consignacionhacienda.utils.ErrorResponse;


@RestController
@RequestMapping("/api/auction")
public class AuctionRest {

    private static final Logger logger = LoggerFactory.getLogger(AuctionRest.class);

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> saveAuction(@RequestBody Auction newAuction){

        if(!userService.getCurrentUserAuthorities().toArray()[0].toString().equals("Administrador") && (newAuction.getUsers() == null || newAuction.getUsers().isEmpty())){
            logger.error("Al menos un usuario debe estar asociado al remate.");
            return new ResponseEntity<>(new ErrorResponse("Al menos un usuario debe estar asociado al remate."), HttpStatus.BAD_REQUEST);
        }
        try {
            return ResponseEntity.ok(auctionService.saveAuction(newAuction));
        } catch (InvalidCredentialsException e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (HttpUnauthorizedException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateAuctionById(@PathVariable Integer id, @RequestBody AuctionDTO changes){
        try {
            return ResponseEntity.ok(auctionService.updateAuctionById(id, changes));
        }catch (HttpUnauthorizedException e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.UNAUTHORIZED);
        }catch (AuctionNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (InvalidCredentialsException e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAuctionById(@PathVariable Integer id){
        try {
            return ResponseEntity.ok(auctionService.deleteAuctionById(id));
        } catch (AuctionNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (HttpUnauthorizedException e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (HttpForbidenException e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAuctionById(@PathVariable Integer id){
        try {
            return ResponseEntity.ok(auctionService.getAuctionById(id));
        } catch (AuctionNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping()
    public ResponseEntity<?> getNotDeletedAuctions(
            @RequestParam(name="page", defaultValue = "0") Integer page,
            @RequestParam(name="limit", defaultValue = "10") Integer limit){
        try {
            return ResponseEntity.ok(auctionService.getAllNotDeletedAuctionsByPage(page, limit));
        } catch (InvalidCredentialsException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/finish/{id}")
    public ResponseEntity<?> finishAuctionById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(auctionService.finishAuctionById(id));
        } catch (IllegalStateException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (AuctionNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (HttpUnauthorizedException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/resume/{id}")
    public ResponseEntity<?> resumeAuctionById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(auctionService.resumeAuctionById(id));
        } catch (HttpUnauthorizedException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (AuctionNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
