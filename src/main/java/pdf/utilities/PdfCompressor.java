package pdf.utilities;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

public class PdfCompressor {

    static Logger logger = Logger.getLogger(PdfCompressor.class.getName());

    public static void main(String[] args) throws IOException {
        File root = new File("./setup/pdf_input");
        File[] pdfFiles = root.listFiles();
        Arrays.sort(pdfFiles, new FilenameComparator());

        for (int i = 0; i < pdfFiles.length; i++) {
            File file = pdfFiles[i];
            String outputCompressedFilePath = "./setup/pdf_output/compressed-" + file.getName();
            PdfUtils.compressPdfWithPdfBox(file.getAbsolutePath(), outputCompressedFilePath);
            logger.info(outputCompressedFilePath + " compressed successfully.");
        }
    }
}