package pdf.utilities;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class PdfCombiner {
    public static void main(String[] args) {
        File root = new File("./setup/pdf_input");
        File[] pdfFiles = root.listFiles();
        Arrays.sort(pdfFiles, new FilenameComparator());

        String outputFilePath = "./setup/pdf_output/merged.pdf"; // Output file path

        try {
            mergePDFs(pdfFiles, outputFilePath);
            System.out.println("PDFs merged successfully.");
        } catch (IOException e) {
            System.err.println("Error merging PDFs: " + e.getLocalizedMessage());
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