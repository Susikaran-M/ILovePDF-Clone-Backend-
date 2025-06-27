package com.jega.iLovePDFClone.organizePDF.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jega.iLovePDFClone.organizePDF.services.ExcelToPDFService;

@RestController
@RequestMapping("/api/pdf")
public class ExcelToPDFController {
@Autowired
private ExcelToPDFService service;
//This accepts the file given using this end point 
@PostMapping("/excel/add")
public ResponseEntity<byte[]> convertExcelToPdf(@RequestParam("file") MultipartFile file){
//we are calling the service add store the processed PDF bytes to pdfBytes
try{
	byte[] pdfBytes=service.convertToPdf(file);
	//this gives the full control over our http response
	//it names file as ExcelPDF,gives the type of content,and body returns original PDF
	return ResponseEntity.ok()
		    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ExcelPDF.pdf")
		    .contentType(MediaType.APPLICATION_PDF)
		    .body(pdfBytes);
}
catch (Exception e) {
    return ResponseEntity
            .badRequest()
            .body(("Error while converting Excel to PDF: " + e.getMessage()).getBytes());
}

}
}