package com.jega.iLovePDFClone.organizePDF.controller;


import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.jega.iLovePDFClone.organizePDF.services.CompressServices;


import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/pdf")
public class CompressPDFController {
	
	
	@Autowired
	CompressServices compressServices;

    @PostMapping("/compress-pdf")
    public void compressPdf(@RequestParam("file") MultipartFile file,
                            @RequestParam(value = "level", defaultValue = "screen") String level,
                            HttpServletResponse response) throws IOException, InterruptedException {

        // Prepare output temp file
        File compressedFile = File.createTempFile("compressed", ".pdf");

        // Compress using Ghostscript
        compressServices.compressPdfUsingGhostScript(file, level, compressedFile);

        // Set response headers
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=compressed.pdf");

        // Stream compressed file to response
        try (FileInputStream fis = new FileInputStream(compressedFile);
             ServletOutputStream os = response.getOutputStream()) {

            fis.transferTo(os);
            os.flush();
        }

        // Clean up
        compressedFile.delete();
    }
}
