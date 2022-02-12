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
import pfc.consignacionhacienda.dto.SoldBatchResponseDTO;
import pfc.consignacionhacienda.exceptions.HttpForbidenException;
import pfc.consignacionhacienda.exceptions.animalsOnGround.AnimalsOnGroundNotFound;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.exceptions.batch.BatchNotFoundException;
import pfc.consignacionhacienda.exceptions.soldBatch.SoldBatchNotFoundException;
import pfc.consignacionhacienda.model.AnimalsOnGround;
import pfc.consignacionhacienda.model.Auction;
import pfc.consignacionhacienda.model.Batch;
import pfc.consignacionhacienda.model.SoldBatch;
import pfc.consignacionhacienda.reports.dto.*;
import pfc.consignacionhacienda.services.auction.AuctionService;
import pfc.consignacionhacienda.services.batch.BatchService;
import pfc.consignacionhacienda.services.client.ClientService;
import pfc.consignacionhacienda.services.report.ReportService;
import pfc.consignacionhacienda.services.soldBatch.SoldBatchService;
import pfc.consignacionhacienda.services.user.UserService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService{

    private static final Logger logger = LoggerFactory.getLogger(PdfGeneratorServiceImpl.class);

    @Autowired
    private BatchService batchService;

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private UserService userService;

    @Autowired
    private ClientService clientService;


    // /api/pdf/starting-order/{auctionId}
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

    // /api/pdf//boleta/3/{soldBatchId}
    @Autowired
    private SoldBatchService soldBatchService;

    @Override
    public byte[] getTicketPurchasePDFBySoldBatchId(Integer soldBatchId, Integer copyAmount) throws SoldBatchNotFoundException, AnimalsOnGroundNotFound, BatchNotFoundException, AuctionNotFoundException, DocumentException, IOException {
        SoldBatch soldBatch = soldBatchService.findSoldBatchById(soldBatchId);
        AnimalsOnGround animalsOnGround = soldBatch.getAnimalsOnGround();
        if(animalsOnGround.getDeleted() != null && animalsOnGround.getDeleted()){
            throw new AnimalsOnGroundNotFound("Los animales asociados a este lote vendido han sido eliminados.");
        }

        Batch batch = batchService.getBatchByAnimalsOnGroundId(animalsOnGround.getId());
        if(batch.getDeleted() != null && batch.getDeleted()){
            throw new BatchNotFoundException("El 'lote' correspondiente a este 'lote vendido' ha sido eliminado.");
        }

        Auction auction = batch.getAuction();
        if(auction.getDeleted() != null && auction.getDeleted()){
            throw new AuctionNotFoundException("El remate asociado a este 'lote vendido' ha sido eliminado");
        }

        String vendedorName = clientService.findByProvenanceId(batch.getProvenance().getId()).getName();
        String compradorName = soldBatch.getClient().getName();

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
        document.addTitle("Boleta "+" "+vendedorName+" "+compradorName);
        document.addCreator("Sistema de Gestión de Consignación de Hacienda");

        // 4. Open document
        document.open();

        // 5. Add content
        Image image = Image.getInstance("src/main/resources/images/ganados-logo.png");
        image.setAlignment(Element.ALIGN_CENTER);
        image.scaleAbsoluteHeight(image.getHeight()-50f);
        image.scaleAbsoluteWidth(image.getWidth()-150f);
        image.setSpacingAfter(20f);

        Paragraph paragraph = new Paragraph("SAN JUAN 957 - TEL: (0341) 4210223 - 4214311 - 4216107 - ROSARIO\n" +
                "info@ganadosremates.com.ar - www.ganadosremates.com.ar\n" +
                "Ventas en Mercado Rosario\n" +
                "Ferias: San Justo - Campo Andino - San Javier - La Criolla - Reconquista - Roldán\n" +
                "Remates televisados en directo por Canal Rural\n", fontTitle);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingBefore(20f);
        paragraph.setSpacingAfter(50f);

        //5.2 Add table to show the data
        PdfPTable pdfPTable = getPdfPTableForTicketPurchase(soldBatch, batch, auction, vendedorName, compradorName);

        for(int i=0; i<copyAmount;i++){
            document.add(image);
            document.add(paragraph);
            document.add(pdfPTable);
            if(i<copyAmount-1){
                document.newPage();
            }
        }
        // 5. Close document
        document.close();
        writer.close();

        return byteArrayOutputStream.toByteArray();
    }

    private PdfPTable getPdfPTableForTicketPurchase(SoldBatch soldBatch, Batch batch, Auction auction, String vendedorName, String compradorName) {
        //column widths
//        float[] columnWidths = {2f, 5f, 2f, 4f};
        final float HEIGHT = 35f;
        final float INDENT = 3f;
        PdfPTable pdfPTable = new PdfPTable(4);
        pdfPTable.setWidthPercentage(90f);
//        pdfPTable.setSplitLate(false); //Si una fila es muy alta, la tabla no se corta, sino que la fila es subdividida

        //insert column headings
        PdfPCell cell = getPdfTableCell("INFORMACIÓN GENERAL", 4,Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 0, HEIGHT );
        pdfPTable.addCell(cell);

        cell = getPdfTableCell("Fecha: " + getDateFormat(auction.getDate()), 1,  Element.ALIGN_UNDEFINED,Element.ALIGN_MIDDLE,INDENT, HEIGHT-2);
        pdfPTable.addCell(cell);

        cell = getPdfTableCell("Lugar: " + auction.getLocality().getName(), 2, Element.ALIGN_UNDEFINED, Element.ALIGN_MIDDLE, INDENT, HEIGHT-2);
        pdfPTable.addCell(cell);

        cell = getPdfTableCell("Corral: " + batch.getCorralNumber(), 2, Element.ALIGN_UNDEFINED, Element.ALIGN_MIDDLE, INDENT,HEIGHT-2);
        pdfPTable.addCell(cell);

//        cell = new PdfPCell(new Phrase());
//        cell.setFixedHeight(HEIGHT-2);
//        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//        pdfPTable.addCell(cell);

        cell = getPdfTableCell(" ", 4, Element.ALIGN_UNDEFINED, Element.ALIGN_MIDDLE, 0, HEIGHT-2);
        pdfPTable.addCell(cell);

        cell = getPdfTableCell("Vendedor", 1, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 0, HEIGHT);
        pdfPTable.addCell(cell);

        cell = getPdfTableCell(vendedorName, 3, Element.ALIGN_UNDEFINED, Element.ALIGN_MIDDLE, INDENT, HEIGHT);
        pdfPTable.addCell(cell);

        cell = getPdfTableCell("Comprador", 1, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 0, HEIGHT);
        pdfPTable.addCell(cell);

        cell = getPdfTableCell(compradorName, 3, Element.ALIGN_UNDEFINED, Element.ALIGN_MIDDLE, INDENT, HEIGHT);
        pdfPTable.addCell(cell);

        cell = getPdfTableCell(" ", 4, Element.ALIGN_UNDEFINED, Element.ALIGN_MIDDLE, 0, HEIGHT-2);
        pdfPTable.addCell(cell);

        cell = getPdfTableCell("DATOS DE VENTA", 4, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 0, HEIGHT);
        pdfPTable.addCell(cell);

        cell = getPdfTableCell("Categoría", 1, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 0, HEIGHT-2);
        pdfPTable.addCell(cell);

        cell = getPdfTableCell("Cantidad", 1, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 0, HEIGHT-2);
        pdfPTable.addCell(cell);

        cell = getPdfTableCell("Kilos en pie", 1, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 0, HEIGHT-2);
        pdfPTable.addCell(cell);

        if(soldBatch.getMustWeigh() != null && soldBatch.getMustWeigh()) {
            cell = getPdfTableCell("Precio ($/kg)", 1, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 0, HEIGHT - 2);
        } else {
            cell = getPdfTableCell("Precio ($/u)", 1, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 0, HEIGHT - 2);
        }
        pdfPTable.addCell(cell);

        cell = getPdfTableCell(soldBatch.getAnimalsOnGround().getCategory().getName(), 1, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 0, HEIGHT-2);
        pdfPTable.addCell(cell);

        cell = getPdfTableCell(String.valueOf(soldBatch.getAmount()), 1, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 0, HEIGHT-2);
        pdfPTable.addCell(cell);

        cell = getPdfTableCell(soldBatch.getWeight() != null ? String.valueOf(soldBatch.getWeight()) : "---", 1, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 0, HEIGHT-2);
        pdfPTable.addCell(cell);

        cell = getPdfTableCell(String.valueOf(soldBatch.getPrice()), 1, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 0, HEIGHT-2);
        pdfPTable.addCell(cell);

        cell = getPdfTableCell(" ", 4, Element.ALIGN_UNDEFINED, Element.ALIGN_MIDDLE, 0, HEIGHT-2);
        pdfPTable.addCell(cell);

        cell = getPdfTableCell("Plazo", 2, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 0, HEIGHT-2);
        pdfPTable.addCell(cell);

        if(soldBatch.getPaymentTerm() != null && soldBatch.getPaymentTerm() != 0) {
            cell = getPdfTableCell(soldBatch.getPaymentTerm() + " días", 2, Element.ALIGN_UNDEFINED, Element.ALIGN_MIDDLE, INDENT, HEIGHT-2);
            pdfPTable.addCell(cell);
        } else {
            cell = getPdfTableCell(soldBatch.getPaymentTerm() + " días", 2, Element.ALIGN_UNDEFINED, Element.ALIGN_MIDDLE, INDENT, HEIGHT-2);
            pdfPTable.addCell(cell);
        }
        return pdfPTable;
    }

    private PdfPCell getPdfTableCell(String text, int colspan, int horizontalAlignment, int verticalAlignment, float indent, float height) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setColspan(colspan);
        cell.setHorizontalAlignment(horizontalAlignment);
        cell.setVerticalAlignment(verticalAlignment);
        cell.setFixedHeight(height);
        cell.setIndent(indent);
        return cell;
    }

    private String getDateFormat(Instant date) {
        LocalDateTime datetime = LocalDateTime.ofInstant(date, ZoneOffset.of("-03:00"));
        return DateTimeFormatter.ofPattern("dd-MM-yyyy").format(datetime);
    }

    // /api/pdf/report
    @Autowired
    private ReportService reportService;

    @Override
    public byte[] getReportPdfByAuctionId(Integer auctionId, Boolean withCategoriesInfo, Boolean withSoldBatches) throws HttpForbidenException, AuctionNotFoundException, DocumentException, IOException {
        Report report = reportService.getReportByAuctionId(auctionId, withCategoriesInfo);
        CommonInfo auctionCommonInfo = report.getGeneralInfo().getCommonInfo();

        List<Seller> sellers = auctionCommonInfo.getSellers();
        List<Buyer> buyers = auctionCommonInfo.getBuyers();
        Integer totalAnimalsSold = auctionCommonInfo.getTotalAnimalsSold();
        Integer totalAnimalsNotSold = auctionCommonInfo.getTotalAnimalsNotSold();
        Double totalMoneyIncome = auctionCommonInfo.getTotalMoneyIncome();
        List<Assistant> assistants = report.getGeneralInfo().getAssistants();
        List<Consignee> consignees = report.getGeneralInfo().getConsignees();
        List<SoldBatchResponseDTO> soldBatches = soldBatchService.getAllSoldBatchesByAuctionId(auctionId);

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
        document.addTitle("Reporte estadístico del remate");
        document.addCreator("Sistema de Gestión de Consignación de Hacienda");


        // 4. Open document
        document.open();

        // 5. Add content
        // 5.1 add a title page
        int red = BaseColor.LIGHT_GRAY.getRed();
        int green = BaseColor.LIGHT_GRAY.getGreen();
        int blue = BaseColor.LIGHT_GRAY.getBlue();
        final int BRIGHTNESS = 35;
        final float INDENT = 4f;
        BaseColor baseColorLightGrayBrighter = new BaseColor(red+BRIGHTNESS, green+BRIGHTNESS, blue+BRIGHTNESS); //Para obtener un gris mas claro
        BaseColor baseColor = baseColorLightGrayBrighter;

        Image image = Image.getInstance("src/main/resources/images/ganados-logo.png");
        image.setAlignment(Element.ALIGN_CENTER);
        image.scaleAbsoluteHeight(image.getHeight()-50f);
        image.scaleAbsoluteWidth(image.getWidth()-150f);
        image.setSpacingAfter(20f);

        document.add(image);

        Paragraph paragraph = new Paragraph("SAN JUAN 957 - TEL: (0341) 4210223 - 4214311 - 4216107 - ROSARIO\n" +
                "info@ganadosremates.com.ar - www.ganadosremates.com.ar\n" +
                "Ventas en Mercado Rosario\n" +
                "Ferias: San Justo - Campo Andino - San Javier - La Criolla - Reconquista - Roldán\n" +
                "Remates televisados en directo por Canal Rural\n", fontTitle);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingBefore(20f);
        paragraph.setSpacingAfter(40f);
        document.add(paragraph);

        paragraph = new Paragraph("REPORTE DEL REMATE", fontTitle);
        paragraph.setSpacingAfter(20f);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        paragraph = new Paragraph("Informe del remate realizado en " + report.getGeneralInfo().getLocality() + " el día " + this.getDateFormat(report.getGeneralInfo().getDate()), fontBody);
        paragraph.setSpacingAfter(10f);
        document.add(paragraph);

        paragraph = new Paragraph("Número de Senasa: " + report.getGeneralInfo().getSenasaNumber(), fontBody);
        paragraph.setSpacingAfter(30f);
        document.add(paragraph);

        paragraph = new Paragraph("PARTICIPANTES: ", fontHeader);
        document.add(paragraph);
        paragraph = new Paragraph("Consignatario/s: " + getConsigneesString(consignees), fontBody);
        paragraph.setIndentationLeft(INDENT*2);
        document.add(paragraph);
        paragraph = new Paragraph("Asistente/s: " + getAssistansString(assistants), fontBody);
        paragraph.setIndentationLeft(INDENT*2);
        paragraph.setSpacingAfter(30f);
        document.add(paragraph);

        paragraph = new Paragraph("BALANCE GENERAL: ", fontHeader);
        document.add(paragraph);
        paragraph = new Paragraph("Total animales ingresados: " + (auctionCommonInfo.getTotalAnimalsSold() + auctionCommonInfo.getTotalAnimalsNotSold()), fontBody);
        document.add(paragraph);

        paragraph = new Paragraph("Total animales vendidos: " + auctionCommonInfo.getTotalAnimalsSold(), fontBody);
        paragraph.setIndentationLeft(INDENT*3);
        document.add(paragraph);
        paragraph = new Paragraph("Total animales sin vender: " + auctionCommonInfo.getTotalAnimalsNotSold(), fontBody);
        paragraph.setIndentationLeft(INDENT*3);
        paragraph.setSpacingAfter(10f);
        document.add(paragraph);

        BigDecimal totalMoney = BigDecimal.valueOf(auctionCommonInfo.getTotalMoneyIncome());
        paragraph = new Paragraph("Ingreso total: $" + totalMoney.toPlainString(), fontBody);
        paragraph.setSpacingAfter(10f);
        document.add(paragraph);

        paragraph = new Paragraph("Cantidad de lotes de entrada: " + report.getGeneralInfo().getTotalBatchesForSell(), fontBody);
        document.add(paragraph);
        paragraph = new Paragraph("Cantidad de lotes de salida: " + soldBatches.size(), fontBody);
        paragraph.setSpacingAfter(10f);
        document.add(paragraph);

        paragraph = new Paragraph("Cantidad de vendedores: " + report.getGeneralInfo().getTotalSeller(), fontBody);
        document.add(paragraph);
        paragraph = new Paragraph("Cantidad de compradores: " + report.getGeneralInfo().getTotalBuyers(), fontBody);
        paragraph.setSpacingAfter(15f);
        document.add(paragraph);

        PdfPTable pdfPTable = new PdfPTable(new float[]{2f, 1f, 2f});
        pdfPTable.setWidthPercentage(90f);

        final float FIXEDCELLHEIGHTFACTOR = 25f;
        PdfPCell cell = new PdfPCell(new Phrase("COMPRADORES", fontHeader));
        cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
        cell.setColspan(3);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(baseColorLightGrayBrighter);
        pdfPTable.addCell(cell);

        cell = new PdfPCell(new Phrase("Nombre", fontHeader));
        cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(baseColorLightGrayBrighter);
        pdfPTable.addCell(cell);

        cell = new PdfPCell(new Phrase("Cantidad", fontHeader));
        cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(baseColorLightGrayBrighter);
        pdfPTable.addCell(cell);

        cell = new PdfPCell(new Phrase("Dinero invertido", fontHeader));
        cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(baseColorLightGrayBrighter);
        pdfPTable.addCell(cell);

        addPdfTableBuyersData(pdfPTable, buyers, fontBody, FIXEDCELLHEIGHTFACTOR);
        pdfPTable.setSpacingAfter(15f);
        document.add(pdfPTable);

//        pdfPTable.setSplitLate(false); //Si una fila es muy alta, la tabla no se corta, sino que la fila es subdividida
        pdfPTable = new PdfPTable(new float[]{2f, 1f, 1f, 2f});
        pdfPTable.setWidthPercentage(90f);

        cell = new PdfPCell(new Phrase("VENDEDORES", fontHeader));
        cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
        cell.setColspan(4);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(baseColorLightGrayBrighter);
        pdfPTable.addCell(cell);

        cell = new PdfPCell(new Phrase("Nombre", fontHeader));
        cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(baseColorLightGrayBrighter);
        pdfPTable.addCell(cell);

        cell = new PdfPCell(new Phrase("Cantidad vendida", fontHeader));
        cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(baseColorLightGrayBrighter);
        pdfPTable.addCell(cell);

        cell = new PdfPCell(new Phrase("Cantidad no vendida", fontHeader));
        cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(baseColorLightGrayBrighter);
        pdfPTable.addCell(cell);

        cell = new PdfPCell(new Phrase("Dinero bruto obtenido", fontHeader));
        cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(baseColorLightGrayBrighter);
        pdfPTable.addCell(cell);


        addPdfTableSellersData(pdfPTable, sellers, fontBody, FIXEDCELLHEIGHTFACTOR);
        pdfPTable.setSpacingAfter(15f);
        document.add(pdfPTable);

        if(withCategoriesInfo) {

            List<CommonInfo> categoryList = report.getCategoryList();

            for(CommonInfo category: categoryList){
                document.newPage();
                paragraph = new Paragraph("RESUMEN POR CATEGORÍA DE ANIMALES", fontTitle);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                paragraph.setSpacingAfter(20f);
                document.add(paragraph);

                paragraph = new Paragraph("Categoría: " + category.getName(), fontHeader);
                paragraph.setSpacingAfter(15f);
                document.add(paragraph);

                paragraph = new Paragraph("Total de animales ingresados: " + (category.getTotalAnimalsSold()+category.getTotalAnimalsNotSold()), fontBody);
                paragraph.setSpacingAfter(10f);
                document.add(paragraph);

                paragraph = new Paragraph("Total de animales vendidos: " + category.getTotalAnimalsSold(), fontBody);
                paragraph.setSpacingAfter(10f);
                paragraph.setIndentationLeft(INDENT*3);
                document.add(paragraph);

                paragraph = new Paragraph("Total de animales sin vender: " + category.getTotalAnimalsNotSold(), fontBody);
                paragraph.setSpacingAfter(10f);
                paragraph.setIndentationLeft(INDENT*3);
                document.add(paragraph);

                BigDecimal totalMoneyByCategory = BigDecimal.valueOf(category.getTotalMoneyIncome());
                paragraph = new Paragraph("Ingreso monetario total: $" + totalMoneyByCategory.toPlainString(), fontBody);
                paragraph.setSpacingAfter(20f);
                document.add(paragraph);


                pdfPTable = new PdfPTable(new float[]{2f, 1f, 2f});
                pdfPTable.setWidthPercentage(90f);

                cell = new PdfPCell(new Phrase("COMPRADORES", fontHeader));
                cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
                cell.setColspan(3);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(baseColorLightGrayBrighter);
                pdfPTable.addCell(cell);

                cell = new PdfPCell(new Phrase("Nombre", fontHeader));
                cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(baseColorLightGrayBrighter);
                pdfPTable.addCell(cell);

                cell = new PdfPCell(new Phrase("Cantidad", fontHeader));
                cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(baseColorLightGrayBrighter);
                pdfPTable.addCell(cell);

                cell = new PdfPCell(new Phrase("Dinero invertido", fontHeader));
                cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(baseColorLightGrayBrighter);
                pdfPTable.addCell(cell);

                addPdfTableBuyersData(pdfPTable, category.getBuyers(), fontBody, FIXEDCELLHEIGHTFACTOR);
                pdfPTable.setSpacingAfter(15f);
                document.add(pdfPTable);

//        pdfPTable.setSplitLate(false); //Si una fila es muy alta, la tabla no se corta, sino que la fila es subdividida
                pdfPTable = new PdfPTable(new float[]{2f, 1f, 1f, 2f});
                pdfPTable.setWidthPercentage(90f);

                cell = new PdfPCell(new Phrase("VENDEDORES", fontHeader));
                cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
                cell.setColspan(4);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(baseColorLightGrayBrighter);
                pdfPTable.addCell(cell);

                cell = new PdfPCell(new Phrase("Nombre", fontHeader));
                cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(baseColorLightGrayBrighter);
                pdfPTable.addCell(cell);

                cell = new PdfPCell(new Phrase("Cantidad vendida", fontHeader));
                cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(baseColorLightGrayBrighter);
                pdfPTable.addCell(cell);

                cell = new PdfPCell(new Phrase("Cantidad no vendida", fontHeader));
                cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(baseColorLightGrayBrighter);
                pdfPTable.addCell(cell);

                cell = new PdfPCell(new Phrase("Dinero bruto obtenido", fontHeader));
                cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(baseColorLightGrayBrighter);
                pdfPTable.addCell(cell);


                addPdfTableSellersData(pdfPTable, category.getSellers(), fontBody, FIXEDCELLHEIGHTFACTOR);
                pdfPTable.setSpacingAfter(15f);
                document.add(pdfPTable);

            }
        }

        if(withSoldBatches){
            pdfPTable = new PdfPTable(new float[]{1f, 2f});
            pdfPTable.setWidthPercentage(90f);
            document.newPage();
            cell = new PdfPCell(new Phrase("LOTES VENDIDOS", fontHeader));
            cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
            cell.setColspan(2);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(baseColorLightGrayBrighter);
            pdfPTable.addCell(cell);
            pdfPTable.setHeaderRows(1);
            if(soldBatches.isEmpty()) {
                cell = new PdfPCell(new Phrase("No hay lotes vendidos", fontBody));
                cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
                cell.setColspan(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPTable.addCell(cell);
            }
            for(SoldBatchResponseDTO soldBatch: soldBatches){
                cell = new PdfPCell(new Phrase("Vendedor: ", fontHeader));
                cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setIndent(INDENT);
                cell.setBorderWidthRight(0f);
                cell.setBorderWidthBottom(0f);
                pdfPTable.addCell(cell);

                cell = new PdfPCell(new Phrase(soldBatch.getSeller().getName(), fontBody));
                cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setIndent(INDENT);
                cell.setBorderWidthLeft(0f);
                cell.setBorderWidthBottom(0f);
                pdfPTable.addCell(cell);

                cell = new PdfPCell(new Phrase("Comprador: ", fontHeader));
                cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setIndent(INDENT);
                cell.setBorderWidthRight(0f);
                cell.setBorderWidthTop(0f);
                cell.setBorderWidthBottom(0f);
                pdfPTable.addCell(cell);

                cell = new PdfPCell(new Phrase(soldBatch.getBuyer().getName(), fontBody));
                cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setIndent(INDENT);
                cell.setBorderWidthTop(0f);
                cell.setBorderWidthLeft(0f);
                cell.setBorderWidthBottom(0f);
                pdfPTable.addCell(cell);

                cell = new PdfPCell(new Phrase("Categoría: ", fontHeader));
                cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setIndent(INDENT);
                cell.setBorderWidthTop(0f);
                cell.setBorderWidthRight(0f);
                cell.setBorderWidthBottom(0f);
                pdfPTable.addCell(cell);

                cell = new PdfPCell(new Phrase(soldBatch.getCategory().getName(), fontBody));
                cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorderWidthTop(0f);
                cell.setBorderWidthLeft(0f);
                cell.setBorderWidthBottom(0f);
                cell.setIndent(INDENT);
                pdfPTable.addCell(cell);

                cell = new PdfPCell(new Phrase("Cantidad: ", fontHeader));
                cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setIndent(INDENT);
                cell.setBorderWidthTop(0f);
                cell.setBorderWidthBottom(0f);
                cell.setBorderWidthRight(0f);
                pdfPTable.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(soldBatch.getAmount()), fontBody));
                cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setIndent(INDENT);
                cell.setBorderWidthTop(0f);
                cell.setBorderWidthLeft(0f);
                cell.setBorderWidthBottom(0f);
                pdfPTable.addCell(cell);

                BigDecimal bigDecimal;
                boolean mustWeight = soldBatch.getMustWeigh() != null && soldBatch.getMustWeigh();
                boolean hasPaymentTerm = soldBatch.getPaymentTerm() != null;
                if(mustWeight){
                    cell = new PdfPCell(new Phrase("Peso (kg): ", fontHeader));
                    cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setIndent(INDENT);
                    cell.setBorderWidthTop(0f);
                    cell.setBorderWidthRight(0f);
                    cell.setBorderWidthBottom(0f);
                    pdfPTable.addCell(cell);

                    bigDecimal = BigDecimal.valueOf(soldBatch.getWeight());
                    cell = new PdfPCell(new Phrase(bigDecimal.toPlainString() + " kg", fontBody));
                    cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setIndent(INDENT);
                    cell.setBorderWidthTop(0f);
                    cell.setBorderWidthLeft(0f);
                    cell.setBorderWidthBottom(0f);
                    pdfPTable.addCell(cell);
                }

                String precioString = mustWeight ? "($/kg)" : ("$/u");
                cell = new PdfPCell(new Phrase("Precio " + precioString + ": ", fontHeader));
                cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorderWidthTop(0f);
                cell.setBorderWidthRight(0f);
                cell.setIndent(INDENT);
                if(hasPaymentTerm){
                    cell.setBorderWidthBottom(0f);
                }
                pdfPTable.addCell(cell);

                bigDecimal = BigDecimal.valueOf(soldBatch.getPrice());
                cell = new PdfPCell(new Phrase("$" + bigDecimal.toPlainString(), fontBody));
                cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setIndent(INDENT);
                cell.setBorderWidthLeft(0f);
                cell.setBorderWidthTop(0f);
                if(hasPaymentTerm){
                    cell.setBorderWidthBottom(0f);
                }
                pdfPTable.addCell(cell);

                if(hasPaymentTerm){
                    cell = new PdfPCell(new Phrase("Plazo: ", fontHeader));
                    cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setIndent(INDENT);
                    cell.setBorderWidthRight(0f);
                    cell.setBorderWidthTop(0f);
                    pdfPTable.addCell(cell);

                    cell = new PdfPCell(new Phrase(soldBatch.getPaymentTerm() + " días", fontBody));
                    cell.setFixedHeight(FIXEDCELLHEIGHTFACTOR);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setIndent(INDENT);
                    cell.setBorderWidthLeft(0f);
                    cell.setBorderWidthTop(0f);
                    pdfPTable.addCell(cell);
                }
            }
            document.add(pdfPTable);
        }
        // 5. Close document
        document.close();
        writer.close();

        return byteArrayOutputStream.toByteArray();
    }

    private void addPdfTableSellersData(PdfPTable pdfPTable, List<Seller> sellers, Font fontBody, float cellHeight){
        PdfPCell cell;

        if(sellers.isEmpty()){
            cell = new PdfPCell(new Phrase("No hay vendedores", fontBody));
            cell.setColspan(4);
            cell.setFixedHeight(cellHeight);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPTable.addCell(cell);
        }

        for(Seller s: sellers){
            cell = new PdfPCell(new Phrase(s.getName(), fontBody));
            cell.setFixedHeight(cellHeight);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPTable.addCell(cell);

            cell = new PdfPCell(new Phrase(s.getTotalAnimalsSold()!=0?String.valueOf(s.getTotalAnimalsSold()):"", fontBody));
            cell.setFixedHeight(cellHeight);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPTable.addCell(cell);

            cell = new PdfPCell(new Phrase(s.getTotalAnimalsNotSold()!=0?String.valueOf(s.getTotalAnimalsNotSold()):"", fontBody));
            cell.setFixedHeight(cellHeight);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPTable.addCell(cell);

            BigDecimal totalMoney = BigDecimal.valueOf(s.getTotalMoneyIncome());

            cell = new PdfPCell(new Phrase("$" + totalMoney.toPlainString(), fontBody));
            cell.setFixedHeight(cellHeight);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPTable.addCell(cell);
        }
    }

    private void addPdfTableBuyersData(PdfPTable pdfPTable, List<Buyer> buyers, Font fontBody, float cellHeight) {
        PdfPCell cell;
        if(buyers.isEmpty()){
            cell = new PdfPCell(new Phrase("No hay compradores", fontBody));
            cell.setColspan(3);
            cell.setFixedHeight(cellHeight);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPTable.addCell(cell);
        }
        for(Buyer b: buyers){
            cell = new PdfPCell(new Phrase(b.getName(), fontBody));
            cell.setFixedHeight(cellHeight);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPTable.addCell(cell);

            cell = new PdfPCell(new Phrase(b.getTotalBought()!=0?String.valueOf(b.getTotalBought()):"", fontBody));
            cell.setFixedHeight(cellHeight);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPTable.addCell(cell);

            BigDecimal totalMoney = BigDecimal.valueOf(b.getTotalMoneyInvested());
            cell = new PdfPCell(new Phrase("$" + totalMoney.toPlainString(), fontBody));
            cell.setFixedHeight(cellHeight);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPTable.addCell(cell);
        }
    }

    private String getConsigneesString(List<Consignee> consignees) {
        StringBuilder stringBuffer = new StringBuilder();
        for(Consignee c: consignees){
            stringBuffer.append(", ").append(c.getName());
        };
        return stringBuffer.length() > 0 ? stringBuffer.deleteCharAt(0).deleteCharAt(0).toString() : stringBuffer.append("No hay consignatarios").toString();
    }

    private String getAssistansString(List<Assistant> assistants) {
        StringBuilder stringBuffer = new StringBuilder();
        for(Assistant a: assistants){
            stringBuffer.append(", ").append(a.getName());
        };
        return stringBuffer.length() > 0 ? stringBuffer.deleteCharAt(0).deleteCharAt(0).toString() : stringBuffer.append("No hay asistentes").toString();
    }

}