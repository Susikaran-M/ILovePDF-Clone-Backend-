package com.jega.iLovePDFClone.organizePDF.services;

import java.io.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.itextpdf.text.Document;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class ExcelToPDFService {
	public byte[] convertToPdf(MultipartFile file) throws Exception {
		//validating input
		 if (file == null || file.isEmpty()) {
	            throw new IllegalArgumentException("Uploaded file is empty or missing.");
	        }
		//this well help us handle the resources efficiently 
	    try(XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
	         ByteArrayOutputStream out = new ByteArrayOutputStream()){
	        // creating a document , connecting it with out and opening it
	    	Document document = new Document();
	    	PdfWriter.getInstance(document, out);
	    	document.open();
	     //this loop take one by one spread sheet and convert to PDF
	    	 for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
	        XSSFSheet sheet = workbook.getSheetAt(i);
	        //validation null check
	        Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Excel sheet is empty or missing header row.");
            }
	    	//creating a column in PDF and creating a table structure
	    	PdfPTable table = new PdfPTable(headerRow.getPhysicalNumberOfCells());
	    	//adding a row to the table
	    	for (Row row : sheet) {
	    	    for (Cell cell : row) {
	    	        table.addCell(new PdfPCell(new Phrase(cell.toString())));
	    	    }
	    	}
	    	document.add(table);
	    	  document.add(new com.itextpdf.text.Paragraph("\n")); // spacing between sheets
	         }

	    	document.close();
	    	
	    	return out.toByteArray();
	    	
	    }
		}
}
