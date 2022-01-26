package pfc.consignacionhacienda.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
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
}
