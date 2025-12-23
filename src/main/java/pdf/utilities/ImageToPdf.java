package pdf.utilities;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
            File outputDir = new File(DEFAULT_OUTPUT_DIR);

            if (!outputDir.exists() && !outputDir.mkdirs()) {
                logger.severe("Could not create output directory: " + DEFAULT_OUTPUT_DIR);
                return;
            }

            File[] inputFolders = root.listFiles(File::isDirectory);
            if (inputFolders == null) {
                logger.warning("No input folders found in " + DEFAULT_INPUT_ROOT);
                return;
            }

            for (File inputDirectory : inputFolders) {
                String directoryName = inputDirectory.getName();
                String outputFileName = directoryName.concat(".pdf");
                makePdfFromFolder(inputDirectory, DEFAULT_OUTPUT_DIR, outputFileName);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred during Image to PDF conversion", e);
        }
    }

    private static void makePdfFromFolder(File inputDirectory, String outputDirectory, String outputFileName)
            throws DocumentException, IOException {
        File[] files = inputDirectory.listFiles();
        if (files == null || files.length == 0) {
            logger.warning("No files found in directory: " + inputDirectory.getAbsolutePath());
            return;
        }
        Arrays.sort(files, new FilenameComparator());
        logger.log(Level.INFO, "Processing {0} files in {1}", new Object[] { files.length, inputDirectory.getName() });

        Document document = new Document();
        File outFile = new File(outputDirectory, outputFileName);
        try (FileOutputStream fos = new FileOutputStream(outFile)) {
            PdfWriter writer = PdfWriter.getInstance(document, fos);
            writer.setFullCompression();
            document.open();
            for (File file : files) {
                if (file.isDirectory())
                    continue;
                document.newPage();
                Image image = Image.getInstance(file.getAbsolutePath());
                logger.fine("Adding image: " + file.getAbsolutePath());
                image.setAbsolutePosition(0, 0);
                image.setBorderWidth(0);
                image.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
                image.setCompressionLevel(9);
                document.add(image);
            }
            document.close();
        }

        String compressedFileName = inputDirectory.getName().concat("-compressed.pdf");
        PdfUtils.compressPdfWithPdfBox(
                outFile.getAbsolutePath(),
                new File(outputDirectory, compressedFileName).getAbsolutePath());
        logger.info("Created PDF: " + outputFileName + " and compressed version: " + compressedFileName);
    }

}