package com.jega.iLovePDFClone.organizePDF.services;

import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.*;
import java.util.Comparator;
import java.util.UUID;

@Service
public class WordToPdfService {

	public byte[] convertToPdf(InputStream inputStream, String originalFilename) throws Exception {
	    //Validate input
	    if (inputStream == null || originalFilename == null || !originalFilename.contains(".")) {
	        throw new IllegalArgumentException("Invalid input stream or filename.");
	    }

	    String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));

	    //reate temp input file
	    Path inputPath = Files.createTempFile(UUID.randomUUID().toString(), extension);
	    Files.copy(inputStream, inputPath, StandardCopyOption.REPLACE_EXISTING);

	    //Create temp output directory
	    Path outputDir = Files.createTempDirectory("pdf-output");

	    //Build LibreOffice command
	    String os = System.getProperty("os.name").toLowerCase();
	    String libreCmd = os.contains("win") ? "soffice.exe" : "libreoffice";

	    ProcessBuilder pb = new ProcessBuilder(
	        libreCmd,
	        "--headless",
	        "--convert-to", "pdf",
	        "--outdir", outputDir.toAbsolutePath().toString(),
	        inputPath.toAbsolutePath().toString()
	    );

	    pb.redirectErrorStream(true);
	    Process process = pb.start();

	    //Log output stream
	    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
	        reader.lines().forEach(System.out::println);
	    }

	    int exitCode = process.waitFor();
	    if (exitCode != 0) {
	        throw new RuntimeException("LibreOffice failed with exit code: " + exitCode);
	    }

	    //Retrieve converted PDF
	    File[] pdfFiles = outputDir.toFile().listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
	    if (pdfFiles == null || pdfFiles.length == 0) {
	        throw new RuntimeException("PDF not generated.");
	    }

	    Path pdfPath = pdfFiles[0].toPath();
	    byte[] pdfBytes = Files.readAllBytes(pdfPath);

	    //Cleanup
	    Files.deleteIfExists(pdfPath);
	    Files.deleteIfExists(inputPath);
	    Files.walk(outputDir)
	         .sorted(Comparator.reverseOrder())
	         .map(Path::toFile)
	         .forEach(File::delete);

	    return pdfBytes;
	
    }
}