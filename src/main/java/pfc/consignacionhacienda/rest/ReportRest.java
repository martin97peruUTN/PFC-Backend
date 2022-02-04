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
import pfc.consignacionhacienda.services.report.ReportService;

@RestController
@RequestMapping("/api/report")
public class ReportRest {

    private static final Logger logger = LoggerFactory.getLogger(ReportRest.class);

    @Autowired
    ReportService reportService;

    @GetMapping("/{auctionId}")
    ResponseEntity<?> getReportByAuctionId(@PathVariable Integer auctionId){
        try {
            return ResponseEntity.ok(reportService.getReportByAuctionId(auctionId));
        } catch (AuctionNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
