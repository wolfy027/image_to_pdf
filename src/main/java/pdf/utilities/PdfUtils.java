package pdf.utilities;

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
import java.io.IOException;
import java.util.logging.Logger;

public class PdfUtils {

    public static File setupOutputDirectory(String outputDirPath, Logger logger) {
        File outputDir = new File(outputDirPath);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            logger.severe("Could not create output directory: " + outputDirPath);
            return null;
        }
        return outputDir;
    }

    private PdfUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static void validatePath(String filePath, String expectedDir) throws IOException {
        File file = new File(filePath);
        File dir = new File(expectedDir);
        String canonicalPath = file.getCanonicalPath();
        String canonicalDir = dir.getCanonicalPath();

        if (!canonicalDir.endsWith(File.separator)) {
            canonicalDir += File.separator;
        }

        if (!canonicalPath.startsWith(canonicalDir)) {
            throw new IOException("Invalid destination path: " + filePath + " vs " + expectedDir);
        }
    }

    public static void compressPdfWithPdfBox(String src, String dest) throws IOException {
        String baseDir = new File(".").getAbsolutePath();
        compressPdfWithPdfBox(src, dest, baseDir, baseDir);
    }

    public static void compressPdfWithPdfBox(String src, String dest, String expectedSrcDir, String expectedDestDir) throws IOException {
        validatePath(src, expectedSrcDir);
        validatePath(dest, expectedDestDir);
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
}
