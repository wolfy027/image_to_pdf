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
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageToPdf {

    static Logger logger = Logger.getLogger(ImageToPdf.class.getName());

    public static void main(String[] arg) throws Exception {
        File root = new File("./setup/input");
        String outputDirectory = "./setup/output";
        File[] inputFolders = root.listFiles(File::isDirectory);
        for (File inputDirectory : Objects.requireNonNull(inputFolders)) {
            String directoryName = inputDirectory.getName();
            String outputFileName = directoryName.concat(".pdf");
            makePdfFromFolder(inputDirectory, outputDirectory, outputFileName);
        }
    }

    private static void makePdfFromFolder(File inputDirectory, String outputDirectory, String outputFileName) throws DocumentException, IOException {
        File[] files = inputDirectory.listFiles();
        Arrays.sort(Objects.requireNonNull(files), new FilenameComparator());
        logger.log(Level.INFO, "File count : {0}", files.length);

        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(new File(outputDirectory, outputFileName)));
        writer.setFullCompression();
        document.open();
        for (File file : files) {
            document.newPage();
            Image image = Image.getInstance(file.getAbsolutePath());
            logger.info(file.getAbsolutePath());
            image.setAbsolutePosition(0, 0);
            image.setBorderWidth(0);
            image.scaleAbsolute(PageSize.A4);
            image.setCompressionLevel(9);
            document.add(image);
        }
        document.close();
        PdfUtils.compressPdfWithPdfBox(
                outputDirectory.concat("/").concat(outputFileName),
                outputDirectory.concat("/").concat(inputDirectory.getName()).concat("-compressed.pdf")
        );
    }

}