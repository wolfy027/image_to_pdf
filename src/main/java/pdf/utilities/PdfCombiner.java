package pdf.utilities;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

public class PdfCombiner {

    static Logger logger = Logger.getLogger(PdfCombiner.class.getName());

    public static void main(String[] args) throws IOException {
        File root = new File("./setup/pdf_input");
        File[] pdfFiles = root.listFiles();
        Arrays.sort(pdfFiles, new FilenameComparator());

        String outputFilePath = "./setup/pdf_output/merged.pdf"; // Output file path
        String outputCompressedFilePath = "./setup/pdf_output/merged-compressed.pdf"; // Output file path

        try {
            mergePDFs(pdfFiles, outputFilePath);
            logger.info("PDFs merged successfully.");
        } catch (IOException e) {
            logger.warning("Error merging PDFs: " + e.getLocalizedMessage());
        }
        PdfUtils.compressPdfWithPdfBox(outputFilePath, outputCompressedFilePath);
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