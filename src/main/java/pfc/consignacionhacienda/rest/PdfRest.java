package pfc.consignacionhacienda.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pfc.consignacionhacienda.exceptions.HttpForbidenException;
import pfc.consignacionhacienda.exceptions.animalsOnGround.AnimalsOnGroundNotFound;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.exceptions.batch.BatchNotFoundException;
import pfc.consignacionhacienda.exceptions.soldBatch.SoldBatchNotFoundException;
import pfc.consignacionhacienda.services.pdfGeneratorService.PdfGeneratorService;
import pfc.consignacionhacienda.utils.ErrorResponse;

import java.util.Base64;

@RestController
@RequestMapping(value = "/api/pdf")
public class PdfRest {

    private static final Logger logger = LoggerFactory.getLogger(PdfRest.class);

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @GetMapping("/starting-order/{auctionId}")
    ResponseEntity<?> getStartingOrderPdfByAuctionId(@PathVariable Integer auctionId){
        try {
            return ResponseEntity.ok(Base64.getEncoder().encode(pdfGeneratorService.getStartingOrderListPDFByAuctionId(auctionId)));
        } catch (AuctionNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/boleta/{soldBatchId}")
    ResponseEntity<?> getTicketPurchasePdfBySoldBatchId(@PathVariable Integer soldBatchId,
                                                     @RequestParam(name = "copyAmount", defaultValue = "1")  Integer copyAmount){
        if(copyAmount < 1) {
            return new ResponseEntity<>("El número de copias debe ser mayor a cero", HttpStatus.BAD_REQUEST);
        }
        try {
            return ResponseEntity.ok(Base64.getEncoder().encode(pdfGeneratorService.getTicketPurchasePDFBySoldBatchId(soldBatchId, copyAmount)));
        } catch (SoldBatchNotFoundException | AnimalsOnGroundNotFound | BatchNotFoundException | AuctionNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/report/{auctionId}")
    ResponseEntity<?> getReportByAuctionId(@PathVariable Integer auctionId,
                                           @RequestParam(name = "withCategoriesInfo", defaultValue = "false", required = false)  Boolean withCategoriesInfo,
                                           @RequestParam(name = "withSoldBatches", defaultValue = "false", required = false)  Boolean withSoldBatches){
        try {
            return ResponseEntity.ok(Base64.getEncoder().encode(pdfGeneratorService.getReportPdfByAuctionId(auctionId, withCategoriesInfo, withSoldBatches)));
        } catch (AuctionNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (HttpForbidenException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.FORBIDDEN);
        }catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
