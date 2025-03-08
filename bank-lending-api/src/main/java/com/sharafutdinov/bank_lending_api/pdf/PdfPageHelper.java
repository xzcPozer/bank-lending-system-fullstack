package com.sharafutdinov.bank_lending_api.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class PdfPageHelper extends PdfPageEventHelper {

    private String authFio;
    private String title;
    private boolean isVerify;

    public PdfPageHelper(String lastName, String firstName, String surName, String title, boolean isVerify) {
        this.authFio = String.format("%s %s %s", lastName, firstName, surName);
        this.title = title;
        this.isVerify = isVerify;
    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        PdfPTable table = new PdfPTable(3);
        table.setTotalWidth(510);
        table.setWidths(new int[]{15, 50, 15});
        Font fontCell = new Font(Font.HELVETICA, 10, Font.NORMAL);

        PdfPCell emptyCell = new PdfPCell(new Paragraph(""));
        emptyCell.setBorder(Rectangle.NO_BORDER);

        table.addCell(emptyCell);

        Paragraph dateOfCreationText = new Paragraph("Дата создания документа:", fontCell);
        PdfPCell dateOfCreationTextCell = new PdfPCell(dateOfCreationText);
        dateOfCreationTextCell.setPaddingBottom(5);
        dateOfCreationTextCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        dateOfCreationTextCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(dateOfCreationTextCell);

        Paragraph date = new Paragraph(parseLocalDateNow(), fontCell);
        PdfPCell dateCell = new PdfPCell(date);
        dateCell.setPaddingBottom(5);
        dateCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        dateCell.setBorder(Rectangle.BOTTOM);
        table.addCell(dateCell);

        table.addCell(emptyCell);

        fontCell = new Font(Font.HELVETICA, 20, Font.BOLD);
        Paragraph title = new Paragraph(this.title, fontCell);
        PdfPCell titleCell = new PdfPCell(title);
        titleCell.setPaddingTop(40);
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(titleCell);
        table.addCell(emptyCell);

        table.writeSelectedRows(0, -1, 34, 828, writer.getDirectContent());
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfPTable table = new PdfPTable(6);
        table.setTotalWidth(510);
        table.setWidths(new int[]{4, 1, 2, 6, 2, 2});
        Font fontCell = new Font(Font.HELVETICA, 10, Font.NORMAL);

        Paragraph documentVerifyText = new Paragraph("Документ проверен:", fontCell);
        PdfPCell documentVerifyTextCell = new PdfPCell(documentVerifyText);
        documentVerifyTextCell.setPaddingTop(5);
        documentVerifyTextCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        documentVerifyTextCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(documentVerifyTextCell);

        Paragraph verifyAnswer = new Paragraph(this.isVerify ? "да" : "нет", fontCell);
        PdfPCell verifyAnswerCell = new PdfPCell(verifyAnswer);
        verifyAnswerCell.setPaddingTop(5);
        verifyAnswerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        verifyAnswerCell.setBorder(Rectangle.BOTTOM);
        table.addCell(verifyAnswerCell);

        Paragraph fioText = new Paragraph("ФИО:", fontCell);
        PdfPCell fioTextCell = new PdfPCell(fioText);
        fioTextCell.setPaddingTop(5);
        fioTextCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        fioTextCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(fioTextCell);

        Paragraph fio = new Paragraph(this.isVerify ? this.authFio : "", fontCell);
        PdfPCell fioCell = new PdfPCell(fio);
        fioCell.setPaddingTop(5);
        fioCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        fioCell.setBorder(Rectangle.BOTTOM);
        table.addCell(fioCell);

        Paragraph dateText = new Paragraph("Дата:", fontCell);
        PdfPCell dateTextCell = new PdfPCell(dateText);
        dateTextCell.setPaddingTop(5);
        dateTextCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        dateTextCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(dateTextCell);

        Paragraph date = new Paragraph(this.isVerify ? parseLocalDateNow() : "", fontCell);
        PdfPCell dateCell = new PdfPCell(date);
        dateCell.setPaddingTop(5);
        dateCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        dateCell.setBorder(Rectangle.BOTTOM);
        table.addCell(dateCell);

        table.writeSelectedRows(0, -1, 34, 36, writer.getDirectContent());
    }

    private String parseLocalDateNow() {
        LocalDate date = LocalDate.now();
        if (date.getMonth().ordinal() + 1 < 10)
            return String.format("%s.0%d.%s", date.getDayOfMonth(), date.getMonth().ordinal() + 1, date.getYear());
        else
            return String.format("%s.%d.%s", date.getDayOfMonth(), date.getMonth().ordinal() + 1, date.getYear());
    }
}
