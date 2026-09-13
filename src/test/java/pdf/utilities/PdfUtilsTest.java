package pdf.utilities;

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

public class PdfUtilsTest {

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

        // use the overloaded method to avoid the current dir restriction!
        PdfUtils.compressPdfWithPdfBox(sourceFile.getAbsolutePath(), destFile.getAbsolutePath(), tempDir.toFile().getAbsolutePath(), tempDir.toFile().getAbsolutePath());

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

    @Test
    public void testCompressWithPdfBoxTraversal() {
        File srcFile = new File("/tmp/foo/../etc/passwd");
        File destFile = new File("/tmp/bar/../etc/shadow");

        Exception e1 = assertThrows(IOException.class, () -> {
            PdfUtils.compressPdfWithPdfBox(srcFile.getAbsolutePath(), "/tmp/valid.pdf", "/tmp/valid_src_dir", "/tmp");
        });
        assertTrue(e1.getMessage().contains("Invalid destination path") || e1.getMessage().contains("Path traversal"));

        Exception e2 = assertThrows(IOException.class, () -> {
            PdfUtils.compressPdfWithPdfBox("/tmp/valid.pdf", destFile.getAbsolutePath(), "/tmp", "/tmp/valid_dest_dir");
        });
        assertTrue(e2.getMessage().contains("Invalid destination path") || e2.getMessage().contains("Path traversal"));
    }
}
