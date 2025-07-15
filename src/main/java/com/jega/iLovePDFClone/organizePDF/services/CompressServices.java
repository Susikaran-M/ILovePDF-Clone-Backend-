package com.jega.iLovePDFClone.organizePDF.services;

import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;

import jakarta.servlet.http.HttpServletResponse;

public class CompressServices {
    public void compressPdf(InputStream inputStream, HttpServletResponse response) throws IOException {
        PDDocument document = PDDocument.load(inputStream);

        // Remove unused objects (helps reduce size)
        document.setAllSecurityToBeRemoved(true);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=compressed.pdf");

        // Save with compression enabled
        document.save(response.getOutputStream());
        document.close();
    }

}
