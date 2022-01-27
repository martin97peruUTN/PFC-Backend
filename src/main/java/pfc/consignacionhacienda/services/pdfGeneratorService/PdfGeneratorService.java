package pfc.consignacionhacienda.services.pdfGeneratorService;

import com.itextpdf.text.DocumentException;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;

public interface PdfGeneratorService {
    byte[] getStartingOrderListPDFByAuctionId(Integer auctionId) throws DocumentException, AuctionNotFoundException;
}
