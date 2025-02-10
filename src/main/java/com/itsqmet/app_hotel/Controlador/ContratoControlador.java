package com.itsqmet.app_hotel.Controlador;

import com.itextpdf.text.DocumentException;
import com.itsqmet.app_hotel.Entidad.Cliente;
import com.itsqmet.app_hotel.Entidad.Contrato;
import com.itsqmet.app_hotel.Entidad.Prestaciones;
import com.itsqmet.app_hotel.Servicio.ClienteServicio;
import com.itsqmet.app_hotel.Servicio.ContratoServicio;
import com.itsqmet.app_hotel.Servicio.PrestacionesServicios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class ContratoControlador {
    @Autowired
    ContratoServicio contratoServicio;
    @Autowired
    PrestacionesServicios prestacionesServicios;
    @Autowired
    ClienteServicio clienteServicio;
    //Listar
    @GetMapping("/contratos")
    public String listarContratos(@RequestParam(name = "buscarContratos", required = false) String buscarContratos, Model model) {
        List<Contrato>contratos = contratoServicio.buscarContratoPorNombre(buscarContratos);
        model.addAttribute("buscarContratos", buscarContratos);
        model.addAttribute("contratos", contratos);
        return "Contrato/listaContrato";
    }
    @GetMapping("/formularioContratos")
    public String mostrarFormularioContratos(Model model) {
        List<Cliente> clientes = clienteServicio.mostrarClientes();
        List<Prestaciones> prestaciones = prestacionesServicios.mostrarPrestaciones();
        model.addAttribute("contrato", new Contrato());
        model.addAttribute("prestaciones", prestaciones);
        model.addAttribute("clientes", clientes);
        return "Contrato/formulario";

    }

    //Insertar
    @PostMapping("/registrarContratos")
    public String insertarContratos(Contrato contrato) {
        contratoServicio.guardarContrato(contrato);
        return "redirect:/contratos";
    }
    //actualizar
    @GetMapping("/actualizarContratos/{id}")
    public String editarContratos(@RequestParam Long id, Model model) {
        Optional<Contrato> contrato = contratoServicio.buscarContratoId(id);
        if (contrato.isPresent()) {
            model.addAttribute("contrato", contrato.get());
        } else {
            model.addAttribute("contrato", new Contrato());
        }
        model.addAttribute("prestaciones", prestacionesServicios.mostrarPrestaciones());
        model.addAttribute("clientes", clienteServicio.mostrarClientes());
        return "Contrato/formulario";
    }
        //Eliminar
        @GetMapping("/eliminarContratos/{id}")
        public String eliminarContratos(@PathVariable Long id) {
            contratoServicio.eliminarContrato(id);
            return "redirect:/contratos";
        }

        @GetMapping("/generarPdf")
    public ResponseEntity<byte[]> descargarPdf() throws ExceptionInInitializerError, DocumentException, IOException{
        byte[] pdf = contratoServicio.generarPdfContratos();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "contratos.pdf");
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        }










}
