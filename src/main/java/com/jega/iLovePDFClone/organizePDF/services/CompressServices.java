package com.jega.iLovePDFClone.organizePDF.services;

import java.io.File;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class CompressServices {
	public void compressPdfUsingGhostScript(MultipartFile file, String level, File outputFile) throws IOException {
	    File inputFile = File.createTempFile("input", ".pdf");
	    file.transferTo(inputFile);

	    try {
	        // Check if 'gs' is installed
	        Process checkGs = new ProcessBuilder("gs", "--version").start();
	        int checkExit = checkGs.waitFor();
	        if (checkExit != 0) {
	            System.err.println("Ghostscript not installed or not in PATH. Skipping compression.");
	            file.transferTo(outputFile); // fallback: return original PDF
	            return;
	        }

	        List<String> command = new ArrayList<>(List.of(
	            "gs",
	            "-sDEVICE=pdfwrite",
	            "-dCompatibilityLevel=1.4",
	            "-dNOPAUSE",
	            "-dQUIET",
	            "-dBATCH"
	        ));

	        // Add compression level settings
	        switch (level.toLowerCase()) {
	            case "low" -> command.addAll(List.of(
	                "-dPDFSETTINGS=/screen",
	                "-dColorImageDownsampleType=/Bicubic",
	                "-dColorImageResolution=72",
	                "-dGrayImageDownsampleType=/Bicubic",
	                "-dGrayImageResolution=72",
	                "-dMonoImageDownsampleType=/Subsample",
	                "-dMonoImageResolution=72"
	            ));
	            case "medium" -> command.addAll(List.of(
	                "-dPDFSETTINGS=/ebook",
	                "-dColorImageDownsampleType=/Bicubic",
	                "-dColorImageResolution=150",
	                "-dGrayImageDownsampleType=/Bicubic",
	                "-dGrayImageResolution=150",
	                "-dMonoImageDownsampleType=/Subsample",
	                "-dMonoImageResolution=150"
	            ));
	            case "high" -> command.addAll(List.of(
	                "-dPDFSETTINGS=/printer",
	                "-dColorImageDownsampleType=/Bicubic",
	                "-dColorImageResolution=300",
	                "-dGrayImageDownsampleType=/Bicubic",
	                "-dGrayImageResolution=300",
	                "-dMonoImageDownsampleType=/Subsample",
	                "-dMonoImageResolution=300"
	            ));
	            default -> throw new IllegalArgumentException("Invalid compression level: " + level);
	        }

	        command.add("-sOutputFile=" + outputFile.getAbsolutePath());
	        command.add(inputFile.getAbsolutePath());

	        Process process = new ProcessBuilder(command).start();
	        int exitCode = process.waitFor();

	        if (exitCode != 0) {
	            System.err.println("Ghostscript compression failed. Exit code: " + exitCode);
	            file.transferTo(outputFile); // fallback to original
	        } else {
	            System.out.println("PDF compressed successfully.");
	        }

	    } catch (Exception e) {
	        System.err.println("Error during Ghostscript compression: " + e.getMessage());
	        file.transferTo(outputFile); // fallback
	    } finally {
	        inputFile.delete();
	    }
	}

}
