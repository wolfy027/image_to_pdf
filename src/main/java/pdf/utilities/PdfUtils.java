package pdf.utilities;

import com.itextpdf.text.DocumentException;
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

public class PdfUtils {

    private PdfUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void compressPdfWithPdfBox(String src, String dest) throws IOException {
        try (PDDocument pdDocument = new PDDocument();
                PDDocument oDocument = PDDocument.load(new File(src))) {
            PDFRenderer pdfRenderer = new PDFRenderer(oDocument);
            int numberOfPages = oDocument.getNumberOfPages();

            for (int i = 0; i < numberOfPages; i++) {
                PDPage page = new PDPage(PDRectangle.LETTER);
                BufferedImage bim = pdfRenderer.renderImageWithDPI(i, 150, ImageType.RGB);
                PDImageXObject pdImage = JPEGFactory.createFromImage(pdDocument, bim);
                try (PDPageContentStream contentStream = new PDPageContentStream(pdDocument, page)) {
                    float newHeight = PDRectangle.LETTER.getHeight();
                    float newWidth = PDRectangle.LETTER.getWidth();
                    contentStream.drawImage(pdImage, 0, 0, newWidth, newHeight);
                }
                pdDocument.addPage(page);
            }
            pdDocument.save(dest);
        }
    }

    public static void compressPdfWithItext(String src, String dest) throws IOException, DocumentException {
        String baseDir = new File(".").getAbsolutePath();
        compressPdfWithItext(src, dest, baseDir, baseDir);
    }

    public static void compressPdfWithItext(String src, String dest, String expectedSrcDir, String expectedDestDir) throws IOException, DocumentException {
        File srcFile = new File(src);
        File destFile = new File(dest);

        File srcBase = new File(expectedSrcDir);
        File destBase = new File(expectedDestDir);

        if (!srcFile.getCanonicalPath().startsWith(srcBase.getCanonicalPath() + File.separator)) {
            throw new IOException("Invalid source path: " + src);
        }
        if (!destFile.getCanonicalPath().startsWith(destBase.getCanonicalPath() + File.separator)) {
            throw new IOException("Invalid destination path: " + dest);
        }

        PdfReader reader = new PdfReader(srcFile.getAbsolutePath());
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(destFile.getAbsolutePath()), PdfWriter.VERSION_1_5);
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
