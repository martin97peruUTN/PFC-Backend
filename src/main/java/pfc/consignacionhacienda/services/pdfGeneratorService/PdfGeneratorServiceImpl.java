package pfc.consignacionhacienda.services.pdfGeneratorService;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService{

    @Override
    public byte[] getStartingOrderListPDFByAuctionId(Integer auctionId) throws DocumentException {
        // 1. Create document
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // 2. Create PdfWriter and te output is the byteArrayOutputStream
        PdfWriter.getInstance(document, byteArrayOutputStream);

        // 3. Open document
        document.open();

        // 4. Add content
        document.add(new Paragraph("PDF creado con iText en Java"));

        // 5. Close document
        document.close();

        return byteArrayOutputStream.toByteArray();
    }
}
