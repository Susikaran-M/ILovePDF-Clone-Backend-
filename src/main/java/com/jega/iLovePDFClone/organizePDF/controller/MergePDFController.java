package com.jega.iLovePDFClone.organizePDF.controller;

import org.springframework.http.MediaType; // ✅ Correct


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jega.iLovePDFClone.organizePDF.services.MergePDFService;

@RestController
@RequestMapping("/api/pdf")
public class MergePDFController {
	
	@Autowired
	private MergePDFService mergePDFService;
	
	
	    @PostMapping("/merge")
	    public ResponseEntity<byte[]> mergePDFs(@RequestParam("files") List<MultipartFile> files) {
	        try {
	            byte[] mergedPdf = mergePDFService.mergePDFs(files);

	            return ResponseEntity.ok()
	            		.header("Content-Disposition", "attachment; filename=merged.pdf")
	            		.contentType(MediaType.parseMediaType("application/pdf"))
	                    .body(mergedPdf);

	        } catch (Exception e) {
	            return ResponseEntity.internalServerError().body(null);
	        }
	    }
}
