package pfc.consignacionhacienda.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pfc.consignacionhacienda.dto.SoldBatchDTO;
import pfc.consignacionhacienda.dto.SoldBatchResponseDTO;
import pfc.consignacionhacienda.exceptions.HttpForbidenException;
import pfc.consignacionhacienda.exceptions.HttpUnauthorizedException;
import pfc.consignacionhacienda.exceptions.animalsOnGround.AnimalsOnGroundNotFound;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.exceptions.batch.BatchNotFoundException;
import pfc.consignacionhacienda.exceptions.soldBatch.SoldBatchNotFoundException;
import pfc.consignacionhacienda.model.SoldBatch;
import pfc.consignacionhacienda.services.notSoldBatch.NotSoldBatchService;
import pfc.consignacionhacienda.services.soldBatch.SoldBatchService;

@RestController
@RequestMapping("/api/sold-batch")
public class SoldBatchRest {

    private static final Logger logger = LoggerFactory.getLogger(SoldBatchRest.class);

    @Autowired
    private SoldBatchService soldBatchService;

    @Autowired
    private NotSoldBatchService notSoldBatchService;

    @PostMapping("/{animalsOnGroundId}")
    ResponseEntity<SoldBatch> createSoldBatch(@PathVariable Integer animalsOnGroundId, @RequestBody SoldBatch newSoldBatch){
        try {
            return ResponseEntity.ok(soldBatchService.saveSoldBatch(newSoldBatch, animalsOnGroundId));
        } catch (AnimalsOnGroundNotFound | BatchNotFoundException | AuctionNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e){
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (HttpForbidenException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (HttpUnauthorizedException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/{soldBatchId}")
    ResponseEntity<SoldBatch> updateSoldBatchFromId(@RequestBody SoldBatchDTO soldBatchDTO, @PathVariable Integer soldBatchId){
        try {
            return ResponseEntity.ok(soldBatchService.updateSoldBatchById(soldBatchDTO, soldBatchId));
        } catch (SoldBatchNotFoundException | AnimalsOnGroundNotFound | BatchNotFoundException | AuctionNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }  catch (IllegalArgumentException e){
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (HttpForbidenException e) {
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

    @GetMapping("/by-auction/{auctionId}/sold")
    ResponseEntity<Page<SoldBatchResponseDTO>> getSoldBatches(@PathVariable Integer auctionId,
                                                              @RequestParam(required = false, defaultValue = "0") Integer page,
                                                              @RequestParam(required = false, defaultValue = "10") Integer limit){
        try {
            return ResponseEntity.ok(soldBatchService.getSoldBatchsByAuctionAndPage(auctionId, page, limit));
        } catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/by-auction/{auctionId}/not-sold")
    ResponseEntity<Page<SoldBatchResponseDTO>> getNotSoldBatches(@PathVariable Integer auctionId,
                                                              @RequestParam(required = false, defaultValue = "0") Integer page,
                                                              @RequestParam(required = false, defaultValue = "10") Integer limit){
        try {
            return ResponseEntity.ok(notSoldBatchService.getNotSoldBatchesByAuctionAndPage(auctionId, page, limit));
        } catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{soldBatchId}")
    ResponseEntity<SoldBatch> deleteById(@PathVariable Integer soldBatchId){
        try {
            return ResponseEntity.ok(soldBatchService.deleteById(soldBatchId));
        } catch (HttpUnauthorizedException e) {
           logger.error(e.getMessage());
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (AnimalsOnGroundNotFound | SoldBatchNotFoundException | AuctionNotFoundException | BatchNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (HttpForbidenException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
