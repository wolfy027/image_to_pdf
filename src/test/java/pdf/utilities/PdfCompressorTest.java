package pdf.utilities;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfCompressorTest {

    @TempDir
    Path tempDir;

    private Path inputDir;
    private Path outputDir;

    private TestLogHandler logHandler;
    private Logger logger;

    @BeforeEach
    void setUp() throws IOException {
        inputDir = tempDir.resolve("input");
        outputDir = tempDir.resolve("output");
        Files.createDirectories(inputDir);
        // We let processPdfs create the output directory as part of its logic

        // Setup logger
        logger = Logger.getLogger(PdfCompressor.class.getName());
        logHandler = new TestLogHandler();
        logger.addHandler(logHandler);
        logger.setLevel(Level.ALL);
    }

    @AfterEach
    void tearDown() {
        // Remove logger handler
        if (logger != null) {
            logger.removeHandler(logHandler);
        }
    }

    @Test
    void testMainWithNoPdfFiles() {
        PdfCompressor.processPdfs(inputDir.toString(), outputDir.toString());

        List<LogRecord> records = logHandler.getRecords();
        boolean foundWarning = records.stream()
                .anyMatch(r -> r.getLevel() == Level.WARNING && r.getMessage().contains("No PDF files found"));
        assertTrue(foundWarning, "Expected warning about no PDF files found");
    }

    @Test
    void testMainWithValidPdfFile() throws IOException {
        // Create a valid dummy PDF file
        File pdfFile = inputDir.resolve("test.pdf").toFile();
        try (PDDocument document = new PDDocument()) {
            document.addPage(new PDPage());
            document.save(pdfFile);
        }

        PdfCompressor.processPdfs(inputDir.toString(), outputDir.toString());

        File expectedOutput = outputDir.resolve("compressed-test.pdf").toFile();
        assertTrue(expectedOutput.exists(), "Compressed PDF file should exist");

        List<LogRecord> records = logHandler.getRecords();
        boolean foundInfo = records.stream()
                .anyMatch(r -> r.getLevel() == Level.INFO && r.getMessage().contains("compressed successfully"));
        assertTrue(foundInfo, "Expected success info log");
    }

    @Test
    void testMainWithCorruptedPdfFile() throws IOException {
        // Create a corrupted PDF file (just a text file with .pdf extension)
        File corruptedFile = inputDir.resolve("corrupted.pdf").toFile();
        Files.writeString(corruptedFile.toPath(), "This is not a real PDF");

        PdfCompressor.processPdfs(inputDir.toString(), outputDir.toString());

        List<LogRecord> records = logHandler.getRecords();
        boolean foundWarning = records.stream()
                .anyMatch(r -> r.getLevel() == Level.WARNING && r.getMessage().contains("Failed to compress"));
        assertTrue(foundWarning, "Expected warning about failed compression");
    }

    // Helper class to capture log messages
    private static class TestLogHandler extends Handler {
        private final List<LogRecord> records = new ArrayList<>();

        @Override
        public void publish(LogRecord record) {
            records.add(record);
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws SecurityException {
        }

        public List<LogRecord> getRecords() {
            return records;
        }
    }
}
