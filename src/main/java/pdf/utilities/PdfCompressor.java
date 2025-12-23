package pdf.utilities;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PdfCompressor {

    private static final Logger logger = Logger.getLogger(PdfCompressor.class.getName());
    private static final String DEFAULT_INPUT_ROOT = "./setup/pdf_input";
    private static final String DEFAULT_OUTPUT_DIR = "./setup/pdf_output";

    public static void main(String[] args) {
        try {
            File root = new File(DEFAULT_INPUT_ROOT);
            File outputDir = new File(DEFAULT_OUTPUT_DIR);

            if (!outputDir.exists() && !outputDir.mkdirs()) {
                logger.severe("Could not create output directory: " + DEFAULT_OUTPUT_DIR);
                return;
            }

            File[] pdfFiles = root.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
            if (pdfFiles == null || pdfFiles.length == 0) {
                logger.warning("No PDF files found in " + DEFAULT_INPUT_ROOT);
                return;
            }
            Arrays.sort(pdfFiles, new FilenameComparator());

            for (File file : pdfFiles) {
                String outputCompressedFilePath = new File(outputDir, "compressed-" + file.getName()).getAbsolutePath();
                try {
                    PdfUtils.compressPdfWithPdfBox(file.getAbsolutePath(), outputCompressedFilePath);
                    logger.info("File " + file.getName() + " compressed successfully to " + outputCompressedFilePath);
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Failed to compress " + file.getName(), e);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during PDF compression batch processing", e);
        }
    }
}