package pfc.consignacionhacienda.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pfc.consignacionhacienda.services.pdfGeneratorService.PdfGeneratorService;

import java.util.Base64;

@RestController
@RequestMapping(value = "/api/pdf")
public class PdfRest {
    @Autowired
    private PdfGeneratorService pdfGeneratorService;
    @GetMapping("/starting-order/{auctionId}")
    ResponseEntity<?> getStartingOrderPdfByAuctionId(@PathVariable Integer auctionId){
        try {
            return ResponseEntity.ok(Base64.getEncoder().encode(pdfGeneratorService.getStartingOrderListPDFByAuctionId(auctionId)));
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
