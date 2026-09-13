package pdf.utilities;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageToPdf {

    private static final Logger logger = Logger.getLogger(ImageToPdf.class.getName());
    private static final String DEFAULT_INPUT_ROOT = "./setup/input";
    private static final String DEFAULT_OUTPUT_DIR = "./setup/output";

    public static void main(String[] arg) {
        try {
            File root = new File(DEFAULT_INPUT_ROOT);
            File outputDir = PdfUtils.setupOutputDirectory(DEFAULT_OUTPUT_DIR, logger);
            if (outputDir == null) {
                return;
            }

            File[] directories = root.listFiles(File::isDirectory);
            if (directories == null) return;
            for (File dir : directories) {
                makePdfFromFolder(dir, outputDir.getAbsolutePath(), dir.getName() + ".pdf");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during batch PDF creation", e);
        }
    }

    private static void makePdfFromFolder(File inputDir, String outputDirPath, String outputFileName) throws Exception {
        File[] imageFiles = inputDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg")
                || name.toLowerCase().endsWith(".png"));

        if (imageFiles == null || imageFiles.length == 0) {
            logger.warning("No images found in " + inputDir.getAbsolutePath());
            return;
        }
        Arrays.sort(imageFiles, new FilenameComparator());
        File outputDir = new File(outputDirPath);

        try (PDDocument document = new PDDocument()) {
            for (File file : imageFiles) {
                PDPage page = new PDPage(PDRectangle.LETTER);
                document.addPage(page);
                PDImageXObject pdImage = JPEGFactory.createFromImage(document, javax.imageio.ImageIO.read(file));

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    float newHeight = PDRectangle.LETTER.getHeight();
                    float newWidth = PDRectangle.LETTER.getWidth();
                    contentStream.drawImage(pdImage, 0, 0, newWidth, newHeight);
                }
            }
            String outputFilePath = new File(outputDir, outputFileName).getAbsolutePath();
            document.save(outputFilePath);
            logger.info("PDF created successfully at " + outputFilePath);
            String outputCompressedFilePath = new File(outputDir, inputDir.getName() + "-compressed.pdf").getAbsolutePath();
            PdfUtils.compressPdfWithPdfBox(outputFilePath, outputCompressedFilePath, outputDir.getAbsolutePath(), outputDir.getAbsolutePath());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to create PDF for folder " + inputDir.getName(), e);
        }
    }
}
