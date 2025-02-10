package com.itsqmet.app_hotel.Servicio;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itsqmet.app_hotel.Entidad.Cliente;
import com.itsqmet.app_hotel.Repositorio.ClienteRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.print.DocFlavor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteServicio {
    @Autowired
     ClienteRepositorio clienteRepositorio;
    public List<Cliente> mostrarClientes() {return clienteRepositorio.findAll();}

    public List<Cliente> buscarClienteNombre(String buscarCliente){
        if(buscarCliente != null || buscarCliente.isEmpty()){
            return clienteRepositorio.findAll();
        }else{
            return clienteRepositorio.findByNombreContainingIgnoreCase(buscarCliente);
        }
    }

    public void guardarCliente(Cliente cliente) {clienteRepositorio.save(cliente);}
    public void eliminarCliente(Long id) {clienteRepositorio.deleteById(id);}
    public Optional<Cliente> buscarClienteId(Long id) {return clienteRepositorio.findById(id);}
    @Transactional
    public Cliente obtenerClienteId(Long id){
        Cliente cliente=clienteRepositorio.findById(id).orElseThrow();
        System.out.println(("cliente: "+cliente.getNombre().length()));
        return cliente;
    }

    public byte[] generarPdf() throws DocumentException, IOException {
        List<Cliente> clientes = clienteRepositorio.findAll();
        Document document = new Document(PageSize.A4, 36, 36, 90, 36);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);

        // Agregar eventos de página para encabezado y pie de página
        writer.setPageEvent(new PdfPageEventHelper() {
            public void onEndPage(PdfWriter writer, Document document) {
                try {
                    PdfContentByte cb = writer.getDirectContent();

                    // Encabezado
                    Rectangle rect = new Rectangle(36, document.getPageSize().getHeight() - 72,
                            document.getPageSize().getWidth() - 36,
                            document.getPageSize().getHeight() - 36);
                    rect.setBackgroundColor(new BaseColor(75, 46, 204));
                    cb.rectangle(rect);

                    // Logo y nombre de la empresa
                    Image logo = Image.getInstance("static/css/imag/logo.png"); // Asegúrate de tener el logo en esta ruta
                    logo.setAbsolutePosition(46, document.getPageSize().getHeight() - 68);
                    logo.scaleToFit(80, 30);
                    cb.addImage(logo);

                    // Información de la empresa en el encabezado
                    BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
                    cb.beginText();
                    cb.setFontAndSize(bf, 12);
                    cb.setColorFill(BaseColor.WHITE);
                    cb.setTextMatrix(140, document.getPageSize().getHeight() - 60);
                    cb.showText("Workana - Plataforma de Trabajo Freelance");
                    cb.endText();

                    // Pie de página
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
        Paragraph title = new Paragraph("Listado de Clientes", titleFont);
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

        // Tabla de clientes
        PdfPTable table = new PdfPTable(new float[]{1, 2.5f, 2.5f, 3});
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);

        // Estilo para encabezados de tabla
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
        BaseColor headerBg = new BaseColor(75, 46, 204);


        // Encabezados de tabla
        String[] headers = {"ID", "Nombre", "Email", "Dirección"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(headerBg);
            cell.setPadding(8);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Datos de la tabla
        Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
        for (Cliente cliente : clientes) {
            PdfPCell cell;

            cell = new PdfPCell(new Phrase(String.valueOf(cliente.getId()), contentFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(cliente.getNombre(), contentFont));
            cell.setPadding(6);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(cliente.getEmail(), contentFont));
            cell.setPadding(6);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(cliente.getDireccion(), contentFont));
            cell.setPadding(6);
            table.addCell(cell);
        }

        document.add(table);

        // Información adicional
        Font noteFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY);
        Paragraph note = new Paragraph("\nEste documento es una lista oficial de clientes registrados en Workana. " +
                "Para cualquier consulta, por favor contacte con nuestro servicio de atención al cliente.", noteFont);
        note.setSpacingBefore(20);
        document.add(note);

        document.close();
        return baos.toByteArray();
    }
}

