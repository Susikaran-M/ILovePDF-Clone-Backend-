package com.jega.iLovePDFClone.organizePDF.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SplitPDFService {
	public byte[] splitPdfToZip(MultipartFile file) throws IOException {
        try (
            PDDocument original = PDDocument.load(file.getInputStream());
            ByteArrayOutputStream zipBaos = new ByteArrayOutputStream();
            ZipOutputStream zipOut = new ZipOutputStream(zipBaos)
        ) {
            int totalPages = original.getNumberOfPages();

            for (int i = 0; i < totalPages; i++) {
                PDDocument singlePage = new PDDocument();
                singlePage.addPage(original.getPage(i));

                ByteArrayOutputStream pageBaos = new ByteArrayOutputStream();
                singlePage.save(pageBaos);
                singlePage.close();

                // Create ZIP entry
                ZipEntry entry = new ZipEntry("page_" + (i + 1) + ".pdf");
                zipOut.putNextEntry(entry);
                zipOut.write(pageBaos.toByteArray());
                zipOut.closeEntry();
            }

            zipOut.finish();
            return zipBaos.toByteArray();
        }
    }
}
