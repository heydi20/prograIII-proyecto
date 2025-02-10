package com.itsqmet.app_hotel.Servicio;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itsqmet.app_hotel.Entidad.Contrato;
import com.itsqmet.app_hotel.Repositorio.ContratoRepositorio;
import jakarta.persistence.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ContratoServicio {
    @Autowired
    ContratoRepositorio contratoRepositorio;

    public List<Contrato> mostrarContratos() {
        return contratoRepositorio.findAll();
    }

    public List<Contrato> buscarContratoPorNombre(String buscarContrato) {
        if (buscarContrato == null || buscarContrato.isEmpty()) {
            return contratoRepositorio.findAll();
        } else {
            return contratoRepositorio.findByNombreContratoContainingIgnoreCase(buscarContrato);
        }
    }

    public void guardarContrato(Contrato contrato) {
        contratoRepositorio.save(contrato);
    }

    public void eliminarContrato(Long id) {
        contratoRepositorio.deleteById(id);
    }
    public Optional <Contrato> buscarContratoId(Long id){return contratoRepositorio.findById(id);}


    public byte[] generarPdfContratos() throws DocumentException, IOException {
        List<Contrato> contratos = contratoRepositorio.findAll();
        Document document = new Document(PageSize.A4, 36, 36, 90, 36); // Márgenes personalizados
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);

        // Agregar eventos de página para encabezado y pie de página
        writer.setPageEvent(new PdfPageEventHelper() {
            public void onEndPage(PdfWriter writer, Document document) {
                try {
                    PdfContentByte cb = writer.getDirectContent();

                    // Encabezado (rectángulo azul)
                    Rectangle rect = new Rectangle(36, document.getPageSize().getHeight() - 72,
                            document.getPageSize().getWidth() - 36,
                            document.getPageSize().getHeight() - 36);
                    rect.setBackgroundColor(new BaseColor(75, 46, 204));
                    cb.rectangle(rect);

                    // Logo de la empresa
                    Image logo = Image.getInstance("static/css/imag/logo.png");
                    logo.setAbsolutePosition(46, document.getPageSize().getHeight() - 68);
                    logo.scaleToFit(80, 30);
                    cb.addImage(logo);

                    // Nombre de la empresa
                    BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
                    cb.beginText();
                    cb.setFontAndSize(bf, 12);
                    cb.setColorFill(BaseColor.WHITE);
                    cb.setTextMatrix(140, document.getPageSize().getHeight() - 60);
                    cb.showText("Workana - Plataforma de Trabajo Freelance");
                    cb.endText();

                    // Pie de página con información de contacto
                    cb.beginText();
                    cb.setFontAndSize(bf, 8);
                    cb.setColorFill(BaseColor.GRAY);
                    cb.showTextAligned(PdfContentByte.ALIGN_CENTER,
                            "Workana © " + Calendar.getInstance().get(Calendar.YEAR) + " | Tel: +246810111 | Email: contacto@workana.com",
                            (document.getPageSize().getWidth()) / 2, 20, 0);
                    cb.endText();

                    // Número de página
                    cb.beginText();
                    cb.setFontAndSize(bf, 8);
                    cb.showTextAligned(PdfContentByte.ALIGN_RIGHT,
                            "Página " + writer.getPageNumber(),
                            document.getPageSize().getWidth() - 40, 20, 0);
                    cb.endText();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        document.open();

        // Título del documento
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
        Paragraph title = new Paragraph("Listado de Contratos", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Fecha del reporte
        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.GRAY);
        Paragraph date = new Paragraph("Fecha de generación: " +
                new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), dateFont);
        date.setAlignment(Element.ALIGN_RIGHT);
        date.setSpacingAfter(20);
        document.add(date);

        // Tabla de contratos
        PdfPTable table = new PdfPTable(new float[]{1, 3, 2.5f, 2.5f});
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);

        // Encabezados de la tabla
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
        BaseColor headerBg = new BaseColor(75, 46, 204);
        String[] headers = {"ID", "Nombre Contrato", "Fecha Inicio", "Fecha Fin"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(headerBg);
            cell.setPadding(8);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Datos de la tabla para contratos
        Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
        for (Contrato contrato : contratos) {
            PdfPCell cell;

            cell = new PdfPCell(new Phrase(String.valueOf(contrato.getId()), contentFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(contrato.getNombreContrato(), contentFont));
            cell.setPadding(6);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(String.valueOf(contrato.getFechaInicio()), contentFont));
            cell.setPadding(6);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(String.valueOf(contrato.getFechaFin()), contentFont));
            cell.setPadding(6);
            table.addCell(cell);
        }


        document.add(table);

        // Información adicional
        Font noteFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY);
        Paragraph note = new Paragraph("\nEste documento es una lista oficial de contratos registrados en Workana. " +
                "Para consultas, por favor contacte con nuestro servicio de atención al cliente.", noteFont);
        note.setSpacingBefore(20);
        document.add(note);

        document.close();
        return baos.toByteArray();
    }


}
