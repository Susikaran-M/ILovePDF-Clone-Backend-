package com.jega.iLovePDFClone.organizePDF.services;





import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.Comparator;
import java.util.UUID;

@Service
public class PptToPDFService {

    public byte[] convertPptToPdf(MultipartFile file) throws Exception {
        // Validate input
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty or missing.");
        }

        // Create temp input/output directories
        Path tempDir = Files.createTempDirectory("libreoffice-ppt-temp");
        File inputFile = new File(tempDir.toFile(), UUID.randomUUID() + ".pptx");
        File outputFile = new File(
                tempDir.toFile(),
                inputFile.getName().replace(".pptx", ".pdf")
        );

        // Save uploaded file to temp location
        file.transferTo(inputFile);

        // Detect OS and run LibreOffice command
        String os = System.getProperty("os.name").toLowerCase();
        String libreCmd = os.contains("win") ? "soffice" : "libreoffice";

        ProcessBuilder pb = new ProcessBuilder(
                libreCmd,
                "--headless",
                "--convert-to", "pdf",
                "--outdir", tempDir.toAbsolutePath().toString(),
                inputFile.getAbsolutePath()
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();
        process.waitFor();

        // Load result and cleanup
        if (!outputFile.exists()) {
            throw new RuntimeException("Conversion failed — PDF was not generated.");
        }

        byte[] pdfBytes = Files.readAllBytes(outputFile.toPath());
        Files.walk(tempDir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);

        return pdfBytes;
    }
}