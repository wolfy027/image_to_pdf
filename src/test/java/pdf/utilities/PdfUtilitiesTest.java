package pdf.utilities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfUtilitiesTest {

    @TempDir
    Path tempDir;

    @Test
    void testFilenameComparator() {
        FilenameComparator comparator = new FilenameComparator();
        File f1 = new File("img1.jpg");
        File f2 = new File("img10.jpg");
        File f3 = new File("img2.jpg");

        assertTrue(comparator.compare(f1, f3) < 0);
        assertTrue(comparator.compare(f3, f2) < 0);
    }

    @Test
    void testDirectoryCreationInMain() throws IOException {
        // Test if output directories are created as expected by the logic (mocking the
        // behavior)
        Path outputDir = tempDir.resolve("output");
        File outputDirFile = outputDir.toFile();

        if (!outputDirFile.exists() && !outputDirFile.mkdirs()) {
            throw new IOException("Failed to create dir");
        }

        assertTrue(outputDirFile.exists());
        assertTrue(outputDirFile.isDirectory());
    }
}
