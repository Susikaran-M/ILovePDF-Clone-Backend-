package com.jega.iLovePDFClone.organizePDF.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jega.iLovePDFClone.organizePDF.services.SplitPDFService;

@RestController
@RequestMapping("/api/pdf")
public class SplitPDFController {
	 @Autowired
	    private SplitPDFService splitPDFService;

	    @PostMapping("/split")
	    public ResponseEntity<byte[]> splitPdfAsZip(@RequestParam("file") MultipartFile file) throws Exception {
	        byte[] zipBytes = splitPDFService.splitPdfToZip(file);

	        return ResponseEntity.ok()
	            .header("Content-Disposition", "attachment; filename=\"split_pages.zip\"")
	            .contentType(MediaType.APPLICATION_OCTET_STREAM)
	            .body(zipBytes);
	    }
}
