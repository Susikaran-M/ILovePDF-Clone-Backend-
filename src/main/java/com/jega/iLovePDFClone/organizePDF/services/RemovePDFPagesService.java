package com.jega.iLovePDFClone.organizePDF.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class RemovePDFPagesService {
	 public byte[] removePages(MultipartFile file, List<Integer> pagesToRemove) throws IOException {
	        try (PDDocument document = PDDocument.load(file.getInputStream())) {
	        	List<PDPage> allPages = new ArrayList<>();
	        	for (PDPage page : document.getPages()) {
	        	    allPages.add(page);
	        	}


	            // Remove pages (1-based index)
	            for (int i = pagesToRemove.size() - 1; i >= 0; i--) {
	                int pageIndex = pagesToRemove.get(i) - 1;
	                if (pageIndex >= 0 && pageIndex < document.getNumberOfPages()) {
	                    document.removePage(pageIndex);
	                }
	            }

	            ByteArrayOutputStream out = new ByteArrayOutputStream();
	            document.save(out);
	            return out.toByteArray();
	        }
	    }
}
