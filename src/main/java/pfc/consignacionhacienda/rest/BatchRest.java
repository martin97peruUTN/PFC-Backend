package pfc.consignacionhacienda.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pfc.consignacionhacienda.model.AnimalsOnGround;
import pfc.consignacionhacienda.model.Batch;

@RestController
@RequestMapping("/api/auction-batch")
public class BatchRest {
    private static final Logger logger = LoggerFactory.getLogger(BatchRest.class);

    @PostMapping("/{auctionId}")
    ResponseEntity<Batch> saveBatch(@PathVariable Integer auctionId){
        return null;
    }

    @GetMapping("/{batchId}")
    ResponseEntity<Batch> getBatchById(@PathVariable Integer batchId){
        return null;
    }

    @GetMapping("/animals-on-ground/by-auction/{auctionId}")
    ResponseEntity<Page<AnimalsOnGround>> getAnimalsOnGroundByAuctionId(@PathVariable Integer auctionId,
                                                                        @RequestParam(name = "sold", required = false) Boolean sold,
                                                                        @RequestParam(name = "notSold", required = false) Boolean notSold,
                                                                        @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                                        @RequestParam(name = "limit", defaultValue = "20") Integer limit){

        return null;
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
