package pdf.utilities;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PdfCombiner {

    private static final Logger logger = Logger.getLogger(PdfCombiner.class.getName());
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

            String outputFilePath = new File(outputDir, "merged.pdf").getAbsolutePath();
            String outputCompressedFilePath = new File(outputDir, "merged-compressed.pdf").getAbsolutePath();

            mergePDFs(pdfFiles, outputFilePath);
            logger.info("PDFs merged successfully into " + outputFilePath);

            PdfUtils.compressPdfWithPdfBox(outputFilePath, outputCompressedFilePath);
            logger.info("Compressed PDF created at " + outputCompressedFilePath);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during PDF merging or compression", e);
        }
    }

    public static void mergePDFs(File[] pdfFiles, String outputFilePath) throws IOException {
        PDFMergerUtility pdfMerger = new PDFMergerUtility();

        for (File pdfFile : pdfFiles) {
            pdfMerger.addSource(pdfFile);
        }

        pdfMerger.setDestinationFileName(outputFilePath);

        // Optional: Set memory usage policy (adjust as needed)
        MemoryUsageSetting memoryUsageSetting = MemoryUsageSetting.setupMainMemoryOnly();

        pdfMerger.mergeDocuments(memoryUsageSetting);
    }
}