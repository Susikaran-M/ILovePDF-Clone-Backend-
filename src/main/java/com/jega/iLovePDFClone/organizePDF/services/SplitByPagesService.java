package com.jega.iLovePDFClone.organizePDF.services;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
@Service
public class SplitByPagesService {
	public byte[] extractRangesAsZip(MultipartFile file, String range) {
        try (PDDocument originalDoc = PDDocument.load(file.getInputStream());
             ByteArrayOutputStream zipOutStream = new ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(zipOutStream)) {

            int totalPages = originalDoc.getNumberOfPages();
            List<String> rangeParts = splitRangeParts(range);

            int partNumber = 1;
            for (String part : rangeParts) {
                List<Integer> pageNumbers = parsePageRange(part, totalPages);

                if (!pageNumbers.isEmpty()) {
                    try (PDDocument tempDoc = new PDDocument();
                         ByteArrayOutputStream pdfOutStream = new ByteArrayOutputStream()) {

                        for (int pageNum : pageNumbers) {
                            tempDoc.addPage(originalDoc.getPage(pageNum - 1)); // 0-indexed
                        }

                        tempDoc.save(pdfOutStream);
                        byte[] pdfBytes = pdfOutStream.toByteArray();

                        ZipEntry zipEntry = new ZipEntry("pages_" + part.replace("-", "_") + ".pdf");
                        zipOutputStream.putNextEntry(zipEntry);
                        zipOutputStream.write(pdfBytes);
                        zipOutputStream.closeEntry();
                    }
                }

                partNumber++;
            }

            zipOutputStream.finish();
            return zipOutStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Failed to process PDF: " + e.getMessage(), e);
        }
    }

    private List<String> splitRangeParts(String range) {
        if (range == null || range.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(range.replaceAll("[\\r\\n]", "").trim().split(","));
    }

    private List<Integer> parsePageRange(String part, int totalPages) {
        Set<Integer> result = new LinkedHashSet<>();
        part = part.trim();

        if (part.contains("-")) {
            String[] bounds = part.split("-");
            if (bounds.length == 2) {
                try {
                    int start = Integer.parseInt(bounds[0].trim());
                    int end = Integer.parseInt(bounds[1].trim());

                    if (start <= end) {
                        for (int i = start; i <= end; i++) {
                            if (i >= 1 && i <= totalPages) {
                                result.add(i);
                            }
                        }
                    }
                } catch (NumberFormatException ignored) {}
            }
        } else {
            try {
                int page = Integer.parseInt(part);
                if (page >= 1 && page <= totalPages) {
                    result.add(page);
                }
            } catch (NumberFormatException ignored) {}
        }

        return new ArrayList<>(result);
    }

}

