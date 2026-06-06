package com.example.meditrackaiproject.utils;

import android.content.Context;
import com.example.meditrackaiproject.models.Medicine;
import com.example.meditrackaiproject.models.Prescription;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import java.io.File;
import java.io.FileOutputStream;

public class PdfGenerator {

    public static File generatePrescriptionPdf(Context context, Prescription prescription) throws Exception {
        String fileName = "Prescription_" + prescription.getId() + ".pdf";
        File file = new File(context.getExternalFilesDir(null), fileName);
        
        PdfWriter writer = new PdfWriter(new FileOutputStream(file));
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("MediTrack AI - Digital Prescription").setFontSize(20).setBold());
        document.add(new Paragraph("Date: " + prescription.getDate()));
        document.add(new Paragraph("Doctor: " + prescription.getDoctorName()));
        document.add(new Paragraph("Patient: " + prescription.getPatientName()));
        document.add(new Paragraph("Diagnosis: " + prescription.getDiagnosis()).setMarginTop(10));
        
        document.add(new Paragraph("Medicines:").setBold().setMarginTop(10));
        
        float[] columnWidths = {3, 2, 2, 3};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.useAllAvailableWidth();
        
        table.addHeaderCell("Medicine");
        table.addHeaderCell("Dosage");
        table.addHeaderCell("Duration");
        table.addHeaderCell("Instructions");

        for (Medicine med : prescription.getMedicines()) {
            table.addCell(med.getName());
            table.addCell(med.getDosage());
            table.addCell(med.getDuration() + " days");
            table.addCell(med.getInstructions());
        }
        
        document.add(table);
        
        if (prescription.getNotes() != null && !prescription.getNotes().isEmpty()) {
            document.add(new Paragraph("Notes: " + prescription.getNotes()).setMarginTop(10));
        }

        document.add(new Paragraph("\n\nDigital Signature\n___________________").setMarginTop(50));

        document.close();
        return file;
    }
}