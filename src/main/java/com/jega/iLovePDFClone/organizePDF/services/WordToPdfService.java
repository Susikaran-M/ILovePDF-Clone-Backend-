package com.jega.iLovePDFClone.organizePDF.services;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;

@Service
public class WordToPdfService {

    public byte[] convertToPdf(InputStream inputStream, String originalFilename) throws Exception {
        // Get extension
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));

        // Create temp input file
        File inputFile = File.createTempFile(UUID.randomUUID().toString(), extension);
        Files.copy(inputStream, inputFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        // Create temp output folder
        File outputDir = Files.createTempDirectory("pdf-output").toFile();

        // Build and execute command
        String command = String.format(
            "\"C:\\Program Files\\LibreOffice\\program\\soffice.exe\" --headless --convert-to pdf --outdir \"%s\" \"%s\"",
            outputDir.getAbsolutePath(),
            inputFile.getAbsolutePath()
        );

        Process process = Runtime.getRuntime().exec(command);
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("LibreOffice failed to convert the file. Exit code: " + exitCode);
        }

        // Look for generated PDF in output directory
        File[] pdfFiles = outputDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
        if (pdfFiles == null || pdfFiles.length == 0) {
            throw new RuntimeException("PDF file not generated. Check LibreOffice installation and input file format.");
        }

        File pdfFile = pdfFiles[0]; // pick the first PDF found

        byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());

        // Cleanup
        inputFile.delete();
        pdfFile.delete();
        outputDir.delete();

        return pdfBytes;
    }
}
