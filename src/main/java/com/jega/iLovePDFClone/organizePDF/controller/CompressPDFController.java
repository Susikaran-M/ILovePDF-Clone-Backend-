package com.jega.iLovePDFClone.organizePDF.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.jega.iLovePDFClone.organizePDF.services.PDFCompressorService;

@RestController
@RequestMapping("/api/pdf")
public class CompressPDFController {

	 @Autowired
	    private PDFCompressorService compressorService;

	    @PostMapping("/compress")
	    public ResponseEntity<byte[]> compressPdf(@RequestParam("file") MultipartFile file) {
	        try {
	            byte[] compressedPdf = compressorService.compressPdf(file);
	            HttpHeaders headers = new HttpHeaders();
	            headers.setContentType(MediaType.APPLICATION_PDF);
	            headers.setContentDisposition(ContentDisposition.attachment().filename("compressed.pdf").build());
	            return new ResponseEntity<>(compressedPdf, headers, HttpStatus.OK);
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(("Error compressing PDF: " + e.getMessage()).getBytes());
	        }
	    }
}
