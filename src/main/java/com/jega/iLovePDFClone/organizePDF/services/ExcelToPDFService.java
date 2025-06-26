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
	    try(XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
	         ByteArrayOutputStream out = new ByteArrayOutputStream()){
	    
	    	XSSFSheet sheet = workbook.getSheetAt(0);
	    	Document document = new Document();
	    	PdfWriter.getInstance(document, out);
	    	document.open();
	    	PdfPTable table = new PdfPTable(sheet.getRow(0).getPhysicalNumberOfCells());
	    	for (Row row : sheet) {
	    	    for (Cell cell : row) {
	    	        table.addCell(new PdfPCell(new Phrase(cell.toString())));
	    	    }
	    	}
	    
	    	document.add(table);
	    	document.close();
	    	
	    	return out.toByteArray();
	    	
	    }
		}
}
