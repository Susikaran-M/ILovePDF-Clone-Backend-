package com.jega.iLovePDFClone.organizePDF.controller;

import java.io.IOException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jega.iLovePDFClone.organizePDF.services.PdfToPptServices;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/pdf")
public class PdfToPptController {

	@Autowired
	PdfToPptServices pdfToPptServices;
	
    @PostMapping("/pdf-to-ppt/")
    public void convertPdfToPpt(@RequestParam("file") MultipartFile file,
                                HttpServletResponse response) throws IOException {
        byte[] pptBytes = null;
		try {
			pptBytes = pdfToPptServices.convertPdfToPpt(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
        response.setContentType("application/vnd.openxmlformats-officedocument.presentationml.presentation");
        response.setHeader("Content-Disposition", "attachment; filename=converted.pptx");
        response.getOutputStream().write(pptBytes);
    }
    
    
}
