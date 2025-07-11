package com.jega.iLovePDFClone.organizePDF.services;





import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.Comparator;
import java.util.UUID;

@Service
public class ExcelToPDFService {

    public byte[] convertExcelToPdf(MultipartFile file) throws Exception {
        // 1️⃣ Validate input
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty or missing.");
        }

        //  Creating temp input/output directories
        Path tempDir = Files.createTempDirectory("libreoffice-temp");// creates the temporary folder path uniquely
        File inputFile = new File(tempDir.toFile(), UUID.randomUUID() + ".xlsx");//it create a subfile inside the tempDir
        File outputFile = new File(tempDir.toFile(), inputFile.getName().replace(".xlsx", ".pdf"));// it changes the .xlsx extension

        //  Save uploaded file
        file.transferTo(inputFile);

        // finding the os
        String os = System.getProperty("os.name").toLowerCase();
        String libreCmd = os.contains("win") ? "soffice" : "libreoffice";

        // this build run the command in os to convert using libreoffice
        ProcessBuilder pb = new ProcessBuilder(
                libreCmd,
                "--headless",
                "--convert-to",
                "pdf",
                "--outdir", tempDir.toAbsolutePath().toString(),//output path
                inputFile.getAbsolutePath()
        );
        //merges stderr and stdout
        pb.redirectErrorStream(true);
        Process process = pb.start();
        process.waitFor();// it stops the java code until the process completed.

        // 5️⃣ Load result and clean up
        if (!outputFile.exists()) {
            throw new RuntimeException("Something went worng, failed to generate PDF.");
        }

        byte[] pdfBytes = Files.readAllBytes(outputFile.toPath());
        // clears the temporarily created files 
        Files.walk(tempDir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        return pdfBytes;
    }
}

