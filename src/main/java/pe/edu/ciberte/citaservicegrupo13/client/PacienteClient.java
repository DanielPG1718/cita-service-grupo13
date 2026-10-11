package pe.edu.ciberte.citaservicegrupo13.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pe.edu.ciberte.citaservicegrupo13.dto.DiagnosticoDTO;
import pe.edu.ciberte.citaservicegrupo13.dto.PacienteHistorialDTO;

import java.util.List;

@FeignClient(name = "paciente-service-grupo13")
public interface PacienteClient {
    @GetMapping("/api/v1/pacientes/{pacienteId}")
    PacienteHistorialDTO obtenerPaciente(@PathVariable("pacienteId") Long pacienteId);

    @GetMapping("/api/v1/pacientes/{pacienteId}/diagnosticos")
    List<DiagnosticoDTO> obtenerDiagnosticos(@PathVariable("pacienteId") Long pacienteId);
}
