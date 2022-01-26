package pfc.consignacionhacienda.services.pdfGeneratorService;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pfc.consignacionhacienda.dto.AnimalsOnGroundDTO;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.services.batch.BatchService;
import pfc.consignacionhacienda.services.user.UserService;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService{

    private static final Logger logger = LoggerFactory.getLogger(PdfGeneratorServiceImpl.class);

    @Autowired
    private BatchService batchService;

    @Autowired
    private UserService userService;

    @Override
    public byte[] getStartingOrderListPDFByAuctionId(Integer auctionId) throws DocumentException, AuctionNotFoundException, DocumentException {
        //buscar los datos para llenar el PDF
        List<AnimalsOnGroundDTO> animalsOnGroundDTOList = batchService.getAllAnimalsOnGroundDTO(auctionId);

        // 1. Create document that contains the data
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);

        //Para usar Arial, tuve que descargar el archivo de la fuente
        Font fontTitle = FontFactory.getFont("/fonts/arial.ttf", 12, Font.BOLD, BaseColor.BLACK);
        Font fontHeader = FontFactory.getFont("/fonts/arial.ttf", 10, Font.BOLD, BaseColor.BLACK);
        Font fontBody = FontFactory.getFont("/fonts/arial.ttf", 10, Font.NORMAL, BaseColor.BLACK);

        //El PDF generado saldra por este buffer para que sea enviado al frontend
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // 2. Create PdfWriter and the output is the byteArrayOutputStream
        PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

        // 3. Add metainfo to document
        document.addAuthor(userService.getCurrentUser().getName() + " " + userService.getCurrentUser().getLastname());
        document.addCreationDate();
        document.addProducer();
        document.addTitle("Orden de salida a pista de animales");
        document.addCreator("Sistema de Gestión de Consignación de Hacienda");

        // 4. Open document
        document.open();

        // 5. Add content
        // 5.1 add a title page
        Paragraph paragraph = new Paragraph("ORDEN DE SALIDA A PISTA DE ANIMALES", fontTitle);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingAfter(15f);
        document.add(paragraph);

        //5.2 Add table to show the data
        //column widths
        float[] columnWidths = {2f, 5f, 2f, 4f};
        PdfPTable pdfPTable = new PdfPTable(columnWidths);
        pdfPTable.setWidthPercentage(90f);
//        pdfPTable.setSplitLate(false); //Si una fila es muy alta, la tabla no se corta, sino que la fila es subdividida

        //insert column headings
        createCellHeaderContent(pdfPTable, "N° de corral", fontHeader, BaseColor.GRAY.brighter());
        createCellHeaderContent(pdfPTable, "Vendedor", fontHeader, BaseColor.GRAY.brighter());
        createCellHeaderContent(pdfPTable, "Cantidad", fontHeader, BaseColor.GRAY.brighter());
        createCellHeaderContent(pdfPTable, "Categoría", fontHeader, BaseColor.GRAY.brighter());
        pdfPTable.setHeaderRows(1);

        int red = BaseColor.LIGHT_GRAY.getRed();
        int green = BaseColor.LIGHT_GRAY.getGreen();
        int blue = BaseColor.LIGHT_GRAY.getBlue();
        final int BRIGHTNESS = 35;
        BaseColor baseColorLightGrayBrighter = new BaseColor(red+BRIGHTNESS, green+BRIGHTNESS, blue+BRIGHTNESS); //Para obtener un gris mas claro
        BaseColor baseColor = baseColorLightGrayBrighter;

        //insert body cells
        for (AnimalsOnGroundDTO animalsOnGroundDTO : animalsOnGroundDTOList) {
            //Set content cell
            createCellBodyContent(pdfPTable, String.valueOf(animalsOnGroundDTO.getCorralNumber()), fontBody, baseColor);
            createCellBodyContent(pdfPTable, animalsOnGroundDTO.getSeller().getName(), fontBody, baseColor);
            createCellBodyContent(pdfPTable, String.valueOf(animalsOnGroundDTO.getAmount()), fontBody, baseColor);
            createCellBodyContent(pdfPTable, animalsOnGroundDTO.getCategory().getName(), fontBody, baseColor);

            //Change background color cell
            if (baseColor.equals(baseColorLightGrayBrighter)) {
                baseColor = BaseColor.WHITE;
            } else {
                baseColor = baseColorLightGrayBrighter;
            }
        }
        document.add(pdfPTable);

        // 5. Close document
        document.close();
        writer.close();

        return byteArrayOutputStream.toByteArray();
    }

    private void createCellBodyContent(PdfPTable pdfPTable, String text, Font font, BaseColor baseColor) {
        final int HORIZONTAL_ALIGN = Element.ALIGN_LEFT;
        final int VERTICAL_ALIGN = Element.ALIGN_MIDDLE;
        final float HEIGHT = 20f;
        final float INDENT = 3f;
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(HORIZONTAL_ALIGN);
        cell.setVerticalAlignment(VERTICAL_ALIGN);
        cell.setBackgroundColor(baseColor);
        cell.setFixedHeight(HEIGHT);
        cell.setIndent(INDENT);
        pdfPTable.addCell(cell);
    }

    private void createCellHeaderContent(PdfPTable pdfPTable, String text, Font font, BaseColor baseColor) {
        final int HORIZONTAL_ALIGN = Element.ALIGN_CENTER;
        final int VERTICAL_ALIGN = Element.ALIGN_MIDDLE;
        final float HEIGHT = 25f;

        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(baseColor);
        cell.setHorizontalAlignment(HORIZONTAL_ALIGN);
        cell.setVerticalAlignment(VERTICAL_ALIGN);
        cell.setFixedHeight(HEIGHT);
        pdfPTable.addCell(cell);
    }
}