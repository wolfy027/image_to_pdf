package pdf.utilities;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfCombinerTest {

    @TempDir
    Path tempDir;

    @Test
    void testMergePDFs() throws IOException {
        File pdf1 = createTempPdf("test1.pdf", 1);
        File pdf2 = createTempPdf("test2.pdf", 2);

        File outputFile = tempDir.resolve("merged.pdf").toFile();

        PdfCombiner.mergePDFs(new File[]{pdf1, pdf2}, outputFile.getAbsolutePath());

        assertTrue(outputFile.exists(), "Output file should be created");

        try (PDDocument doc = PDDocument.load(outputFile)) {
            assertEquals(3, doc.getNumberOfPages(), "Merged PDF should have 3 pages");
        }
    }

    @Test
    void testMergeEmptyFileList() throws Exception {
        File outputFile = tempDir.resolve("merged_empty.pdf").toFile();
        PdfCombiner.mergePDFs(new File[]{}, outputFile.getAbsolutePath());
    }

    private File createTempPdf(String fileName, int numPages) throws IOException {
        File file = tempDir.resolve(fileName).toFile();
        try (PDDocument doc = new PDDocument()) {
            for (int i = 0; i < numPages; i++) {
                doc.addPage(new PDPage());
            }
            doc.save(file);
        }
        return file;
    }
}
