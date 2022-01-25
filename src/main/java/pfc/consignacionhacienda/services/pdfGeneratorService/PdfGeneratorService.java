package pfc.consignacionhacienda.services.pdfGeneratorService;

import com.itextpdf.text.DocumentException;

public interface PdfGeneratorService {
    byte[] getStartingOrderListPDFByAuctionId(Integer auctionId) throws DocumentException;
}
