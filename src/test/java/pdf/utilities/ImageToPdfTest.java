package pdf.utilities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ImageToPdfTest {

    @TempDir
    Path tempDir;

    @Test
    void testMakePdfFromFolder_withImages() throws Exception {
        // Setup input directory and image
        File inputDir = tempDir.resolve("input_test").toFile();
        assertTrue(inputDir.mkdirs(), "Failed to create input dir");

        File imageFile = new File(inputDir, "test_image.png");
        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ImageIO.write(img, "png", imageFile);

        // Setup output directory
        File outputDir = tempDir.resolve("output_test").toFile();
        assertTrue(outputDir.mkdirs(), "Failed to create output dir");
        String outputFileName = "output.pdf";

        // Invoke private method makePdfFromFolder via reflection
        Method makePdfMethod = ImageToPdf.class.getDeclaredMethod("makePdfFromFolder", File.class, String.class, String.class);
        makePdfMethod.setAccessible(true);
        makePdfMethod.invoke(null, inputDir, outputDir.getAbsolutePath(), outputFileName);

        // Verify that the output PDF was created
        File expectedOutputPdf = new File(outputDir, outputFileName);
        assertTrue(expectedOutputPdf.exists(), "Output PDF was not created");
        assertTrue(expectedOutputPdf.length() > 0, "Output PDF is empty");

        // Verify that the compressed PDF was created
        File expectedCompressedPdf = new File(outputDir, inputDir.getName() + "-compressed.pdf");
        assertTrue(expectedCompressedPdf.exists(), "Compressed PDF was not created");
        assertTrue(expectedCompressedPdf.length() > 0, "Compressed PDF is empty");
    }

    @Test
    void testMakePdfFromFolder_emptyDirectory() throws Exception {
        // Setup empty input directory
        File inputDir = tempDir.resolve("empty_input").toFile();
        assertTrue(inputDir.mkdirs(), "Failed to create input dir");

        // Setup output directory
        File outputDir = tempDir.resolve("output_test_empty").toFile();
        assertTrue(outputDir.mkdirs(), "Failed to create output dir");
        String outputFileName = "output_empty.pdf";

        // Invoke private method makePdfFromFolder via reflection
        Method makePdfMethod = ImageToPdf.class.getDeclaredMethod("makePdfFromFolder", File.class, String.class, String.class);
        makePdfMethod.setAccessible(true);
        makePdfMethod.invoke(null, inputDir, outputDir.getAbsolutePath(), outputFileName);

        // Verify that no PDF was created
        File expectedOutputPdf = new File(outputDir, outputFileName);
        assertTrue(!expectedOutputPdf.exists(), "Output PDF should not be created for empty directory");
    }
}
