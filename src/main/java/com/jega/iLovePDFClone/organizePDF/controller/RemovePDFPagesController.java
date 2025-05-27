package com.jega.iLovePDFClone.organizePDF.controller;


import org.springframework.http.MediaType;

import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jega.iLovePDFClone.organizePDF.services.RemovePDFPagesService;

@RestController
@RequestMapping("/api/pdf")
public class RemovePDFPagesController {
	 @Autowired
	    private RemovePDFPagesService removePDFPagesService;

	    @PostMapping("/remove-pages")
	    public ResponseEntity<byte[]> removePagesFromPDF(
	            @RequestParam("file") MultipartFile file,
	            @RequestParam("pages") List<Integer> pagesToRemove) throws IOException {

	        byte[] updatedPDF = removePDFPagesService.removePages(file, pagesToRemove);

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_PDF);
	        headers.setContentDisposition(ContentDisposition.builder("attachment")
	                .filename("updated.pdf")
	                .build());

	        return new ResponseEntity<>(updatedPDF, headers, HttpStatus.OK);
	    }
}
