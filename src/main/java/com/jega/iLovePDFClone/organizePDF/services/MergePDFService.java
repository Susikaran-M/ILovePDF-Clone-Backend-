package com.jega.iLovePDFClone.organizePDF.services;

import java.io.ByteArrayOutputStream;

import java.io.InputStream;
import java.util.List;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MergePDFService {
	public byte[] mergePDFs(List<MultipartFile> files) throws Exception {
        PDFMergerUtility merger = new PDFMergerUtility();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        merger.setDestinationStream(outputStream);

        for (MultipartFile file : files) {
            InputStream inputStream = file.getInputStream();
            merger.addSource(inputStream);
        }

        merger.mergeDocuments(null);

        return outputStream.toByteArray();
    }
}
