package com.jega.iLovePDFClone.organizePDF.services;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class PdfToPptServices {
	
	public byte[] convertPdfToPpt(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty or missing.");
        }

        try (PDDocument document = PDDocument.load(file.getInputStream());
             XMLSlideShow ppt = new XMLSlideShow()) {

            PDFRenderer renderer = new PDFRenderer(document);
            int pageCount = document.getNumberOfPages();

            // Set slide size
            Dimension pgsize = new Dimension(960, 720); // width, height
            ppt.setPageSize(pgsize);

            for (int i = 0; i < pageCount; i++) {
                BufferedImage bim = renderer.renderImageWithDPI(i, 150); // 150 DPI is decent quality

                // Convert BufferedImage to byte[]
                ByteArrayOutputStream imgBytes = new ByteArrayOutputStream();
                ImageIO.write(bim, "png", imgBytes);
                imgBytes.flush();
                byte[] imageBytes = imgBytes.toByteArray();
                imgBytes.close();

                // Add image to slide
                XSLFPictureData picData = ppt.addPicture(imageBytes, XSLFPictureData.PictureType.PNG);
                XSLFSlide slide = ppt.createSlide();
                XSLFPictureShape pic = slide.createPicture(picData);

                // Fit image to slide
                pic.setAnchor(new java.awt.Rectangle(0, 0, pgsize.width, pgsize.height));
            }

            // Output to byte array
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ppt.write(out);
            return out.toByteArray();
        }
    }


	
	
}
