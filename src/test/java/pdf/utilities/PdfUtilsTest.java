package pdf.utilities;

import com.itextpdf.text.DocumentException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PdfUtilsTest {

    @TempDir
    Path tempDir;

    private File createDummyPdf(String filename) throws IOException {
        File file = tempDir.resolve(filename).toFile();
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);
            document.save(file);
        }
        return file;
    }

    @Test
    void testCompressPdfWithPdfBox() throws IOException {
        File sourceFile = createDummyPdf("source.pdf");
        File destFile = tempDir.resolve("dest_pdfbox.pdf").toFile();

        PdfUtils.compressPdfWithPdfBox(sourceFile.getAbsolutePath(), destFile.getAbsolutePath());

        assertTrue(destFile.exists(), "Destination PDF should be created");
        assertTrue(destFile.length() > 0, "Destination PDF should not be empty");
    }

    @Test
    void testCompressPdfWithItext() throws IOException, DocumentException {
        File sourceFile = createDummyPdf("source2.pdf");
        File destFile = tempDir.resolve("dest_itext.pdf").toFile();

        PdfUtils.compressPdfWithItext(sourceFile.getAbsolutePath(), destFile.getAbsolutePath());

        assertTrue(destFile.exists(), "Destination PDF should be created");
        assertTrue(destFile.length() > 0, "Destination PDF should not be empty");
    }

    @Test
    void testUtilityClassConstructor() throws NoSuchMethodException {
        Constructor<PdfUtils> constructor = PdfUtils.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertTrue(exception.getCause() instanceof IllegalStateException);
        assertEquals("Utility class", exception.getCause().getMessage());
    }
}
