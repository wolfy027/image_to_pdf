package pdf.utilities;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class ImageToPdf {

    public static void main(String arg[]) throws Exception {
        File root = new File("./setup/input");
        String outputFile = "output.pdf";
        File[] files = root.listFiles();
        Arrays.sort(files, new FilenameComparator());
        System.out.println(files.length);

        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(new File("./setup/output", outputFile)));
        writer.setFullCompression();
        document.open();
        for (File file : files) {
            document.newPage();
            Image image = Image.getInstance(file.getAbsolutePath());
            System.out.println(file.getAbsolutePath());
            image.setAbsolutePosition(0, 0);
            image.setBorderWidth(0);
            image.scaleAbsolute(PageSize.A4);
            image.setCompressionLevel(9);
            document.add(image);
        }
        document.close();
        compressPdfWithPdfBox("./setup/output/output.pdf", "./setup/output/output-compressed.pdf");
    }

    public static void compressPdfWithPdfBox(String src, String dest) throws IOException {
        PDDocument pdDocument = new PDDocument();
        PDDocument oDocument = PDDocument.load(new File(src));
        PDFRenderer pdfRenderer = new PDFRenderer(oDocument);
        int numberOfPages = oDocument.getNumberOfPages();
        PDPage page = null;

        for (int i = 0; i < numberOfPages; i++) {
            page = new PDPage(PDRectangle.LETTER);
            BufferedImage bim = pdfRenderer.renderImageWithDPI(i, 300, ImageType.RGB);
            PDImageXObject pdImage = JPEGFactory.createFromImage(pdDocument, bim);
            PDPageContentStream contentStream = new PDPageContentStream(pdDocument, page);
            float newHeight = PDRectangle.LETTER.getHeight();
            float newWidth = PDRectangle.LETTER.getWidth();
            contentStream.drawImage(pdImage, 0, 0, newWidth, newHeight);
            contentStream.close();

            pdDocument.addPage(page);
        }
        pdDocument.save(dest);
        pdDocument.close();
    }

    public static void compressPdfWithItext(String src, String dest) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(src);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest), PdfWriter.VERSION_1_5);
        stamper.getWriter().setCompressionLevel(9);
        int total = reader.getNumberOfPages() + 1;
        for (int i = 1; i < total; i++) {
            reader.setPageContent(i, reader.getPageContent(i));
        }
        stamper.setFullCompression();
        stamper.close();
        reader.close();
    }
}