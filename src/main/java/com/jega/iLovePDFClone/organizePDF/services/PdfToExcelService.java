package com.jega.iLovePDFClone.organizePDF.services;

import java.io.IOException;

import java.io.InputStream;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

@Service
public class PdfToExcelService {
	
	private String wrapTextEveryNWords(String paragraph, int wordsPerLine) {
	    String[] words = paragraph.split("\\s+");
	    StringBuilder wrapped = new StringBuilder();

	    for (int i = 0; i < words.length; i++) {
	        wrapped.append(words[i]);
	        if ((i + 1) % wordsPerLine == 0) {
	            wrapped.append("\n"); // New line after every N words
	        } else {
	            wrapped.append(" ");
	        }
	    }

	    return wrapped.toString().trim();
	}

    public void convertPdfToExcel(InputStream pdfInputStream, HttpServletResponse response,String mode) throws IOException {
    	
    	if(mode.equalsIgnoreCase("single")) {
        PDDocument document = PDDocument.load(pdfInputStream);
        ObjectExtractor extractor = new ObjectExtractor(document);
        SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
        PDFTextStripper stripper = new PDFTextStripper();
        

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Extracted Data");

        int rowIndex = 0;
        int totalPages = document.getNumberOfPages();

        for (int pageIndex = 1; pageIndex <= totalPages; pageIndex++) {
            Page page = extractor.extract(pageIndex);
            List<Table> tables = sea.extract(page);

            if (!tables.isEmpty()) {
                for (Table table : tables) {
                    for (List<RectangularTextContainer> row : table.getRows()) {
                        Row excelRow = sheet.createRow(rowIndex++);
                        for (int i = 0; i < row.size(); i++) {
                            excelRow.createCell(i).setCellValue(row.get(i).getText());
                        }
                    }
                }
            } else {
            	stripper.setStartPage(pageIndex);
            	stripper.setEndPage(pageIndex);
            	String text = stripper.getText(document);
            	String[] lines = text.split("\\r?\\n");

            	// Create bold font style
            	CellStyle boldStyle = workbook.createCellStyle();
            	Font boldFont = workbook.createFont();
            	boldFont.setBold(true);
            	boldStyle.setFont(boldFont);

            	String currentTitle = null;
            	StringBuilder paragraphBuilder = new StringBuilder();

            	for (String line : lines) {
            	    line = line.trim();
            	    if (line.isEmpty()) continue;

            	    if (currentTitle == null || (Character.isUpperCase(line.charAt(0)) && !line.endsWith(".") && line.length() < 60)) {
            	        if (currentTitle != null && paragraphBuilder.length() > 0) {
            	            // Title row
            	            Row titleRow = sheet.createRow(rowIndex++);
            	            Cell titleCell = titleRow.createCell(0);
            	            titleCell.setCellValue(currentTitle);
            	            titleCell.setCellStyle(boldStyle);

            	            // Paragraph row
            	            Row paragraphRow = sheet.createRow(rowIndex++);
            	            paragraphRow.createCell(0).setCellValue(paragraphBuilder.toString().trim());
            	        }

            	        currentTitle = line;
            	        paragraphBuilder.setLength(0);
            	    } else {
            	        paragraphBuilder.append(line).append(" ");
            	    }
            	}

            	// Last paragraph block
            	if (currentTitle != null && paragraphBuilder.length() > 0) {
            	    Row titleRow = sheet.createRow(rowIndex++);
            	    Cell titleCell = titleRow.createCell(0);
            	    titleCell.setCellValue(currentTitle);
            	    titleCell.setCellStyle(boldStyle);

            	    Row paragraphRow = sheet.createRow(rowIndex++);
            	    
            	    //this part put 15 words in each line
            	    Cell paragraphCell = paragraphRow.createCell(0);
            	    String wrappedParagraph = wrapTextEveryNWords(paragraphBuilder.toString().trim(), 15);
            	    paragraphCell.setCellValue(wrappedParagraph);

            	    // Enable word wrap style
            	    CellStyle wrapStyle = workbook.createCellStyle();
            	    wrapStyle.setWrapText(true);
            	    paragraphCell.setCellStyle(wrapStyle);
            	}



            }
        }

        document.close();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"smart_output.xlsx\"");
        sheet.autoSizeColumn(0);  // Auto-size column A

        try (ServletOutputStream out = response.getOutputStream()) {
            workbook.write(out);
            out.flush(); // Always flush
        }
        workbook.close();
      }
    	
    	
    	
    	else if(mode.equalsIgnoreCase("multiple")){
            PDDocument document = PDDocument.load(pdfInputStream);
            PDFTextStripper stripper = new PDFTextStripper();
            Workbook workbook = new XSSFWorkbook();
            String text = stripper.getText(document);
            StringBuilder sb = new StringBuilder();

            int sheetIndex = 1;
            Sheet sheet = workbook.createSheet("Sheet" + sheetIndex);
            int rowIndex = 0;

            for (int i = 0; i < text.length(); i++) {
                char ch = text.charAt(i);

                if (ch == '\n' || ch == '\r') {
                    // Only create a row if sb has content
                    if (sb.length() > 0) {
                        Row row = sheet.createRow(rowIndex++);
                        row.createCell(0).setCellValue(sb.toString());
                        sb.setLength(0); // Clear StringBuilder
                    }

                    // Create a new sheet for next block
                    sheet = workbook.createSheet("Sheet" + (++sheetIndex));
                    rowIndex = 0;
                } else {
                    sb.append(ch);
                }
            }

            // Add any leftover content to the last sheet
            if (sb.length() > 0) {
                Row row = sheet.createRow(rowIndex);
                row.createCell(0).setCellValue(sb.toString());
            }



            document.close();

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"smart_output.xlsx\"");
       
            try (ServletOutputStream out = response.getOutputStream()) {
                workbook.write(out);
                out.flush(); // Always flush
            }
            workbook.close();
    		
    		
    	}
    }
}
