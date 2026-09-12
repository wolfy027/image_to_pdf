package pdf.utilities;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PdfUtilsTest {
    @Test
    public void testCompressWithItextTraversal() {
        File srcFile = new File("/tmp/foo/../etc/passwd");
        File destFile = new File("/tmp/bar/../etc/shadow");

        Exception e1 = assertThrows(IOException.class, () -> {
            PdfUtils.compressPdfWithItext(srcFile.getAbsolutePath(), "/tmp/valid.pdf", "/tmp/valid_src_dir", "/tmp");
        });
        assertTrue(e1.getMessage().contains("Invalid source path"));

        Exception e2 = assertThrows(IOException.class, () -> {
            PdfUtils.compressPdfWithItext("/tmp/valid.pdf", destFile.getAbsolutePath(), "/tmp", "/tmp/valid_dest_dir");
        });
        assertTrue(e2.getMessage().contains("Invalid destination path"));
    }

}
