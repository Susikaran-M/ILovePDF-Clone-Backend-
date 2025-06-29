package com.jega.iLovePDFClone.organizePDF.services;





import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PDFCompressorService {

    private static final float JPEG_QUALITY = 0.4f;       // Lower means more compression
    private static final int MAX_PIXELS = 3_000_000;      // Max image resolution

    public byte[] compressPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream());
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            for (PDPage page : document.getPages()) {
                PDResources resources = page.getResources();

                for (COSName name : resources.getXObjectNames()) {
                    if (!resources.isImageXObject(name)) continue;

                    PDImageXObject image = (PDImageXObject) resources.getXObject(name);
                    BufferedImage bufferedImage = image.getImage();
                    if (bufferedImage == null) continue;

                    BufferedImage rgbImage = toRGB(bufferedImage);
                    BufferedImage scaledImage = downSample(rgbImage);

                    PDImageXObject compressedImage = JPEGFactory.createFromImage(
                            document, scaledImage, JPEG_QUALITY);

                    resources.put(name, compressedImage);
                }
            }

            // Remove metadata
            document.setDocumentInformation(new PDDocumentInformation());
            document.setVersion(1.4f);
            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }

    private BufferedImage toRGB(BufferedImage src) {
        BufferedImage rgb = new BufferedImage(
                src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = rgb.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return rgb;
    }

    private BufferedImage downSample(BufferedImage img) {
        long pixels = (long) img.getWidth() * img.getHeight();
        if (pixels <= MAX_PIXELS) return img;

        double scale = Math.sqrt((double) MAX_PIXELS / pixels);
        int w = (int) (img.getWidth() * scale);
        int h = (int) (img.getHeight() * scale);

        BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(img, 0, 0, w, h, null);
        g.dispose();
        return scaled;
    }
}
