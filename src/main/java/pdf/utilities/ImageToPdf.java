package pdf.utilities;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

public class ImageToPdf {

    public static void main(String arg[]) throws Exception {
        File root = new File("./setup/input");
        String outputFile = "output.pdf";
        File[] files = root.listFiles();
        Arrays.sort(files, new FilenameComparator());
        System.out.println(files.length);

        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(new File("./setup/output", outputFile)));
        writer.setFullCompression();
        document.open();
        for (File file : files) {
            document.newPage();
            Image image = Image.getInstance(file.getAbsolutePath());
            System.out.println(file.getAbsolutePath());
            image.setAbsolutePosition(0, 0);
            image.setBorderWidth(0);
            image.scaleAbsolute(PageSize.A4);
            image.setCompressionLevel(9);
            document.add(image);
        }
        document.close();
        PdfUtils.compressPdfWithPdfBox("./setup/output/output.pdf", "./setup/output/output-compressed.pdf");
    }

}