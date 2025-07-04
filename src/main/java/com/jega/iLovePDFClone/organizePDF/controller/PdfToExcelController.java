package com.jega.iLovePDFClone.organizePDF.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jega.iLovePDFClone.organizePDF.services.PdfToExcelService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/pdf")
public class PdfToExcelController {
	
	@Autowired
	PdfToExcelService pdfToExcelService;
	
    @PostMapping("/pdf-to-excel/")
    public void convertPdfToExcel(@RequestParam("file") MultipartFile file,@RequestParam(value = "mode" ,defaultValue = "single") String mode,
    		
                                  HttpServletResponse response) throws IOException {
    	pdfToExcelService.convertPdfToExcel(file.getInputStream(), response,mode);
    }

}
