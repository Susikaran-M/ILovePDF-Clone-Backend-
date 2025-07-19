package com.jega.iLovePDFClone.organizePDF.services;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class SplitByPagesService {
		 public byte[] extractAndMerge(MultipartFile file, String range) {
		        try (PDDocument originalDoc = PDDocument.load(file.getInputStream());
		             PDDocument mergedDoc = new PDDocument();
		             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

		            int totalPages = originalDoc.getNumberOfPages();
		            List<Integer> pagesToExtract = parsePageRange(range, totalPages);

		            for (Integer pageNum : pagesToExtract) {
		                mergedDoc.addPage(originalDoc.getPage(pageNum - 1)); // Convert to 0-based
		            }

		            mergedDoc.save(outputStream);
		            return outputStream.toByteArray();

		        } catch (IOException e) {
		            throw new RuntimeException("Failed to process PDF: " + e.getMessage(), e);
		        }
		    }

		 private List<Integer> parsePageRange(String range, int totalPages) {
			    java.util.Set<Integer> result = new java.util.LinkedHashSet<>();

			    if (range == null || range.trim().isEmpty()) {
			        return new ArrayList<>();
			    }

			    range = range.replaceAll("[\\r\\n]", "").trim();

			    String[] parts = range.split(",");

			    for (String part : parts) {
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
			                } catch (NumberFormatException e) {
			                    System.out.println("Invalid range part: " + part);
			                }
			            }
			        } else {
			            try {
			                int page = Integer.parseInt(part);
			                if (page >= 1 && page <= totalPages) {
			                    result.add(page);
			                }
			            } catch (NumberFormatException e) {
			                System.out.println("Invalid single page: " + part);
			            }
			        }
			    }

//			    System.out.println("Parsed page range: " + range);
//			    System.out.println("Total pages in document: " + totalPages);
//			    System.out.println("Parsed pages to extract: " + result);

			    return new ArrayList<>(result);
			}



}

