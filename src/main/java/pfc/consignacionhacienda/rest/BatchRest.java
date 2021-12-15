package pfc.consignacionhacienda.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pfc.consignacionhacienda.dto.AnimalsOnGroundDTO;
import pfc.consignacionhacienda.exceptions.HttpForbidenException;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.model.AnimalsOnGround;
import pfc.consignacionhacienda.model.Batch;
import pfc.consignacionhacienda.services.batch.BatchService;

@RestController
@RequestMapping("/api/auction-batch")
public class BatchRest {
    private static final Logger logger = LoggerFactory.getLogger(BatchRest.class);

    @Autowired
    BatchService batchService;

    @PostMapping("/{auctionId}")
    ResponseEntity<Batch> saveBatch(@PathVariable Integer auctionId, @RequestBody Batch newBatch){
        try {
            return ResponseEntity.ok(batchService.saveBatch(newBatch, auctionId ));
        } catch (AuctionNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e){
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (HttpForbidenException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{batchId}")
    ResponseEntity<Batch> getBatchById(@PathVariable Integer batchId){
        return null;
    }

    @GetMapping("/animals-on-ground/by-auction/{auctionId}")
    ResponseEntity<Page<AnimalsOnGroundDTO>> getAnimalsOnGroundByAuctionId(@PathVariable Integer auctionId,
                                                                           @RequestParam(name = "sold", defaultValue = "false") Boolean sold,
                                                                           @RequestParam(name = "notSold", defaultValue = "false") Boolean notSold,
                                                                           @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                                           @RequestParam(name = "limit", defaultValue = "20") Integer limit){
//        logger.info(batchService.getAnimalListDTO(auctionId, sold, notSold, page, limit).toString());
        try {
            return ResponseEntity.ok(batchService.getAnimalListDTO(auctionId, sold, notSold, page, limit));
        } catch (AuctionNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/{batchId}")
    ResponseEntity<Batch> updateBatchById(@PathVariable Integer batchId){
        return null;
    }

    @PostMapping("/{batchId}/animals-on-ground")
    ResponseEntity<AnimalsOnGround> saveAnimalsOnGround(@PathVariable Integer batchId){
        return null;
    }

    @PatchMapping("/animals-on-ground/{animalsId}")
    ResponseEntity<AnimalsOnGround> updateAnimalsOnGroundById(@PathVariable Integer animalsId){
        return null;
    }

    @DeleteMapping("/animals-on-ground/{animalsId}")
    ResponseEntity<AnimalsOnGround> deleteAnimalsOnGroundById(@PathVariable Integer animalsId){
        return null;
    }

    @DeleteMapping("/{batchId}")
    ResponseEntity<AnimalsOnGround> deleteBatchById(@PathVariable Integer batchId){
        return null;
    }
}
