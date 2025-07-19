package com.jega.iLovePDFClone.organizePDF.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.jega.iLovePDFClone.organizePDF.services.SplitByPagesService;


import java.util.List;
@RestController
@RequestMapping("/pdf")
public class SplitByPagesController {
	 @Autowired
	    private SplitByPagesService splitByPagesService;

	 @PostMapping("/extract-and-zip")
	    public ResponseEntity<byte[]> extractAndZipPages(
	            @RequestParam("file") MultipartFile file,
	            @RequestParam("range") String range // example: "1-3,5,7-9"
	    ) {
	        byte[] zipBytes = splitByPagesService.extractRangesAsZip(file, range);

	        return ResponseEntity.ok()
	                .contentType(MediaType.APPLICATION_OCTET_STREAM)
	                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=extracted_ranges.zip")
	                .body(zipBytes);
	    }
}
