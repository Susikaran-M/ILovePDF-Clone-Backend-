package com.jega.iLovePDFClone.organizePDF.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.jega.iLovePDFClone.organizePDF.services.CompressServices;

import jakarta.servlet.http.HttpServletResponse;

public class Compress {
	
	@Autowired
	CompressServices Compress_services;
	
    @PostMapping("/compress")
    public void compressPdf(
            @RequestParam("file") MultipartFile file,
            HttpServletResponse response) {

        try {
        	Compress_services.compressPdf(file.getInputStream(), response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
