package com.jega.iLovePDFClone.organizePDF.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jega.iLovePDFClone.organizePDF.services.ExcelToPDFService;

@RestController
@RequestMapping("/api/pdf")
public class ExcelToPDFController {
@Autowired
private ExcelToPDFService service;
@PostMapping("/excel/add")
r

}
