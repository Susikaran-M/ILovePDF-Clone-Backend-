package com.jega.iLovePDFClone.organizePDF.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.jega.iLovePDFClone.organizePDF.services.SplitByPagesService;

import java.util.List;
@RestController
@RequestMapping("/pdf")
public class SplitByPagesController {
	 @Autowired
	    private SplitByPagesService splitByPagesService;

	    @PostMapping("/extract-and-merge")
	    public ResponseEntity<byte[]> extractAndMergePages(
	            @RequestParam("file") MultipartFile file,
	            @RequestParam("range") String range // example: "1-3,5,7"
	    ) {
	        byte[] mergedPdf = splitByPagesService.extractAndMerge(file, range);

	        return ResponseEntity.ok()
	                .header("Content-Disposition", "attachment; filename=merged.pdf")
	                .body(mergedPdf);
	    }
}
