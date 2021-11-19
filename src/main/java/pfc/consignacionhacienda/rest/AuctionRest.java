package pfc.consignacionhacienda.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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


@RestController
@RequestMapping("/api/auction")
public class AuctionRest {

    private static final Logger logger = LoggerFactory.getLogger(AuctionRest.class);

    @Autowired
    private AuctionService auctionService;

    @PostMapping
    public ResponseEntity<Auction> saveAuction(@RequestBody Auction newAuction){

        if(newAuction.getUsers() == null || newAuction.getUsers().isEmpty()){
            logger.error("Al menos un usuario debe estar asociado al remate.");
            return ResponseEntity.badRequest().build();
        }
        try {
            return ResponseEntity.ok(auctionService.saveAuction(newAuction));
        } catch (InvalidCredentialsException e){
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }catch (HttpUnauthorizedException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }


    @PatchMapping("/{id}")
    public ResponseEntity<Auction> updateAuctionById(@PathVariable Integer id, @RequestBody AuctionDTO changes){
        try {
            return ResponseEntity.ok(auctionService.updateAuctionById(id, changes));
        }catch (HttpUnauthorizedException e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }catch (AuctionNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (InvalidCredentialsException e){
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Auction> deleteAuctionById(@PathVariable Integer id){
        try {
            return ResponseEntity.ok(auctionService.deleteAuctionById(id));
        } catch (AuctionNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (HttpUnauthorizedException e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (HttpForbidenException e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Auction> getAuctionById(@PathVariable Integer id){
        try {
            return ResponseEntity.ok(auctionService.getNotDeletedAuctionById(id));
        } catch (AuctionNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping()
    public ResponseEntity<Page<Auction>> getNotDeletedAuctions(
            @RequestParam(name="page", defaultValue = "0") Integer page,
            @RequestParam(name="limit", defaultValue = "10") Integer limit){
        try {
            return ResponseEntity.ok(auctionService.getAllNotDeletedAuctionsByPage(page, limit));
        } catch (InvalidCredentialsException e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
