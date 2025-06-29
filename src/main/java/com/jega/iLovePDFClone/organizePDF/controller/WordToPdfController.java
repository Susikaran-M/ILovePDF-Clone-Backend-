package com.jega.iLovePDFClone.organizePDF.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.jega.iLovePDFClone.organizePDF.services.WordToPdfService;

@RestController
@RequestMapping("/api/convert")
public class WordToPdfController {

    @Autowired
    private WordToPdfService wordToPdfService;

    @PostMapping("/word-to-pdf")
    public ResponseEntity<byte[]> convertDocxToPdf(@RequestParam("file") MultipartFile file) {
        try {
            byte[] pdfBytes = wordToPdfService.convertToPdf(file.getInputStream(), file.getOriginalFilename());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment().filename("converted.pdf").build());

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
