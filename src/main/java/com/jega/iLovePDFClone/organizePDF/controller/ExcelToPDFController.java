package com.jega.iLovePDFClone.organizePDF.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.jega.iLovePDFClone.organizePDF.services.ExcelToPDFService;

@RestController

@RequestMapping("/api/libre")
public class ExcelToPDFController {

    @Autowired
    private ExcelToPDFService libreService;
    private byte[] pdfBytes;
    @PostMapping("/convert")
    public ResponseEntity<byte[]> convertWithLibreOffice(@RequestParam("file") MultipartFile file) {
        try {
            pdfBytes = libreService.convertExcelToPdf(file);
         // returning the output
           return ResponseEntity.ok()
		    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ExcelPDF.pdf")
		    .contentType(MediaType.APPLICATION_PDF)
		    .body(pdfBytes);
           
        }//not giving  correct argument
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage().getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(("Internal Error: " + e.getMessage()).getBytes());
        }
    }
    @GetMapping("/download")
    public ResponseEntity<byte[]> download() {
        try {
        	
        	 if (pdfBytes == null || pdfBytes.length == 0) {
                 throw new IllegalArgumentException("No PDF available for download. Please convert first.");
             }

         // returning the output
           return ResponseEntity.ok()
		    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ExcelPDF.pdf")
		    .contentType(MediaType.APPLICATION_PDF)
		    .body(pdfBytes);
           
        }//not giving  correct argument
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage().getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(("Internal Error: " + e.getMessage()).getBytes());
        }
    }
}