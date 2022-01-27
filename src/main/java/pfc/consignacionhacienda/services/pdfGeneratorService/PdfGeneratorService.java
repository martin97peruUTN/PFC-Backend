package pfc.consignacionhacienda.services.pdfGeneratorService;

import com.itextpdf.text.DocumentException;
import pfc.consignacionhacienda.exceptions.animalsOnGround.AnimalsOnGroundNotFound;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.exceptions.batch.BatchNotFoundException;
import pfc.consignacionhacienda.exceptions.soldBatch.SoldBatchNotFoundException;

import java.io.IOException;

public interface PdfGeneratorService {
    byte[] getStartingOrderListPDFByAuctionId(Integer auctionId) throws DocumentException, AuctionNotFoundException;
    byte[] getTicketPurchasePDFBySoldBatchId(Integer soldBatchId) throws SoldBatchNotFoundException, AnimalsOnGroundNotFound, BatchNotFoundException, AuctionNotFoundException, DocumentException, IOException;
}
