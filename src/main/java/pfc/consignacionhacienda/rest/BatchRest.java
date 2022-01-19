package pfc.consignacionhacienda.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pfc.consignacionhacienda.dto.AnimalsOnGroundDTO;
import pfc.consignacionhacienda.dto.BatchDTO;
import pfc.consignacionhacienda.exceptions.BadHttpRequest;
import pfc.consignacionhacienda.exceptions.HttpForbidenException;
import pfc.consignacionhacienda.exceptions.HttpUnauthorizedException;
import pfc.consignacionhacienda.exceptions.animalsOnGround.AnimalsOnGroundNotFound;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.exceptions.batch.BatchNotFoundException;
import pfc.consignacionhacienda.exceptions.client.ClientNotFoundException;
import pfc.consignacionhacienda.model.AnimalsOnGround;
import pfc.consignacionhacienda.model.Batch;
import pfc.consignacionhacienda.services.batch.BatchService;
import pfc.consignacionhacienda.utils.ErrorResponse;

import java.util.List;

@RestController
@RequestMapping("/api/auction-batch")
public class BatchRest {
    private static final Logger logger = LoggerFactory.getLogger(BatchRest.class);

    @Autowired
    BatchService batchService;

    @PostMapping("/{auctionId}")
    ResponseEntity<?> saveBatch(@PathVariable Integer auctionId, @RequestBody Batch newBatch){
        try {
            return ResponseEntity.ok(batchService.saveBatch(newBatch, auctionId ));
        } catch (AuctionNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (HttpForbidenException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.FORBIDDEN);
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{batchId}")
    ResponseEntity<?> getBatchById(@PathVariable Integer batchId){
        try {
            return ResponseEntity.ok(batchService.findById(batchId));
        } catch (BatchNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/animals-on-ground/by-auction/{auctionId}")
    ResponseEntity<?> getAnimalsOnGroundByAuctionId(@PathVariable Integer auctionId,
                                                                           @RequestParam(name = "sold", defaultValue = "false") Boolean sold,
                                                                           @RequestParam(name = "notSold", defaultValue = "false") Boolean notSold,
                                                                           @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                                           @RequestParam(name = "limit", defaultValue = "20") Integer limit){
//        logger.info(batchService.getAnimalListDTO(auctionId, sold, notSold, page, limit).toString());
        try {
            return ResponseEntity.ok(batchService.getAnimalListDTO(auctionId, sold, notSold, page, limit));
        } catch (AuctionNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/animals-on-ground/by-auction/{auctionId}/allForSort")
    ResponseEntity<?> getAnimalsOnGroundByAuctionId(@PathVariable Integer auctionId){
        try {
            return ResponseEntity.ok(batchService.getAllAnimalsOnGroundDTO(auctionId));
        } catch (AuctionNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{batchId}")
    ResponseEntity<?> updateBatchById(@PathVariable Integer batchId, @RequestBody BatchDTO batchDTO){
        try {
            return ResponseEntity.ok(batchService.updateBatchById(batchId, batchDTO));
        } catch (BatchNotFoundException | AuctionNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (BadHttpRequest | IllegalArgumentException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }  catch (HttpForbidenException e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.FORBIDDEN);
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{batchId}/animals-on-ground")
    ResponseEntity<?> saveAnimalsOnGround(@PathVariable Integer batchId, @RequestBody AnimalsOnGround animalsOnGround){
        try {
            return ResponseEntity.ok(batchService.addAnimalsOnGround(batchId, animalsOnGround));
        } catch (BatchNotFoundException | AuctionNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch ( IllegalArgumentException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (HttpForbidenException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/animals-on-ground/{animalsId}")
    ResponseEntity<?> updateAnimalsOnGroundById(@PathVariable Integer animalsId, @RequestBody AnimalsOnGroundDTO animalsOnGroundDTO){
        try {
            return ResponseEntity.ok(batchService.updateAnimalsOnGroundById(animalsId, animalsOnGroundDTO));
        } catch (BadHttpRequest | IllegalArgumentException e) {
           logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (AnimalsOnGroundNotFound | AuctionNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (HttpForbidenException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.FORBIDDEN);
        } catch( Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/animals-on-ground/{animalsId}")
    ResponseEntity<?> deleteAnimalsOnGroundById(@PathVariable Integer animalsId){
        try {
            return ResponseEntity.ok(batchService.deleteAnimalsOnGroundById(animalsId));
        } catch (AnimalsOnGroundNotFound | AuctionNotFoundException | BatchNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }  catch (HttpForbidenException e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.FORBIDDEN);
        } catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{batchId}")
    ResponseEntity<?> deleteBatchById(@PathVariable Integer batchId){
        try {
            return ResponseEntity.ok(batchService.deleteBatchById(batchId));
        } catch (HttpForbidenException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.FORBIDDEN);
        } catch (AuctionNotFoundException | BatchNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-animals-on-ground/{animalsOnGroundId}")
    ResponseEntity<?> getBatchByAnimalsOnGroundId(@PathVariable Integer animalsOnGroundId){
        try {
            return ResponseEntity.ok(batchService.getBatchByAnimalsOnGroundIdWithClient(animalsOnGroundId));
        } catch (BatchNotFoundException | AnimalsOnGroundNotFound | ClientNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/animals-on-ground/sort/{auctionId}")
    ResponseEntity<?> sortAnimalsOnGround(
            @PathVariable Integer auctionId,
            @RequestBody List<AnimalsOnGroundDTO> animalsOnGroundDTOList
    ){
        try {
            return ResponseEntity.ok(batchService.sortAnimalsOnGround(animalsOnGroundDTOList, auctionId));
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (AnimalsOnGroundNotFound | AuctionNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }  catch (HttpUnauthorizedException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-auction/{auctionId}")
    ResponseEntity<?> getBatchesByAuctionId(@PathVariable Integer auctionId,
                                                      @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                      @RequestParam(name = "limit", defaultValue = "10") Integer limit){
        try{
            return ResponseEntity.ok(batchService.getBatchesByAuctionIdAndPage(auctionId, page, limit));
        }catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
