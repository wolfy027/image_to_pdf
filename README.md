# Image to PDF Utilities

A simple set of Java utilities to convert images to PDF, combine multiple PDFs, and compress PDF files.

## Features

- **ImageToPdf**: Converts sequences of images from folders into high-quality PDFs and automatically generates a compressed version.
- **PdfCombiner**: Merges multiple PDF files into a single document and creates a compressed copy.
- **PdfCompressor**: Batch compresses PDF files in a directory.

## Usage

### Running the Utilities

You can run the different utilities directly using Gradle:

- **Image to PDF**: `./gradlew runImageToPdf`
- **PDF Combiner**: `./gradlew runPdfCombiner`
- **PDF Compressor**: `./gradlew runPdfCompressor`

### Prerequisites
- JDK 17 or higher
- Gradle 8.8+

### Image to PDF Conversion
1. Place your images inside subfolders within `./setup/input/` (e.g., `./setup/input/Folder1/*.jpg`).
2. Run `./gradlew runImageToPdf`.
3. Find the generated PDF and its compressed version in `./setup/output/`.

### PDF Merging
1. Place the PDFs you want to merge into `./setup/pdf_input/`.
2. Run `./gradlew runPdfCombiner`.
3. The merged result `merged.pdf` and its compressed version will be in `./setup/pdf_output/`.

### PDF Compression
1. Place PDFs to be compressed in `./setup/pdf_input/`.
2. Run `./gradlew runPdfCompressor`.
3. Compressed files will be created in `./setup/pdf_output/` with a `compressed-` prefix.

## Technical Details

- **Libraries**: iText 5, Apache PDFBox, OpenPDF.
- **Improvements**: Refactored for better resource management (try-with-resources), improved error handling, and configurable paths.
