package pe.edu.ciberte.citaservicegrupo13.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacienteHistorialDTO {
    private Long pacienteId;
    private String numeroDocumento;
    private String nombreCompleto;
    private String tipoSeguro;
    private List<DiagnosticoDTO> listaDiagnosticosActivos;
    private String estadoPaciente;
}
