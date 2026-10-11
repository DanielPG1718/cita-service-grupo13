package pe.edu.ciberte.citaservicegrupo13.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.ciberte.citaservicegrupo13.dto.AtencionMedicaResponseDTO;
import pe.edu.ciberte.citaservicegrupo13.dto.ResumenFichaMedicaConsolidadaDTO;
import pe.edu.ciberte.citaservicegrupo13.service.CitaService;

@RestController
@RequestMapping("/api/v1/citas")
public class CitaController {
    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @GetMapping("/{idCita}")
    public AtencionMedicaResponseDTO obtenerCita(@PathVariable("idCita") Long idCita) {
        return citaService.obtenerCita(idCita);
    }

    @GetMapping("/evaluar-y-completar/{idCita}")
    public AtencionMedicaResponseDTO evaluarYCompletar(@PathVariable("idCita") Long idCita) {
        return citaService.evaluarYCompletar(idCita);
    }

    @GetMapping("/ficha-medica/{pacienteId}")
    public ResumenFichaMedicaConsolidadaDTO obtenerFichaMedica(@PathVariable("pacienteId") Long pacienteId) {
        return citaService.obtenerFichaMedica(pacienteId);
    }
}
