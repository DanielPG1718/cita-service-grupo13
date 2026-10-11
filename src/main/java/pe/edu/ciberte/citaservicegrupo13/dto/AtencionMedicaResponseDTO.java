package pe.edu.ciberte.citaservicegrupo13.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtencionMedicaResponseDTO {
    private Long idCita;
    private String codigoAtencion;
    private LocalDateTime fechaCita;
    private Long pacienteId;
    private String medico;
    private String especialidad;
    private String estadoCita;
    private List<DiagnosticoDTO> listaDiagnosticos;
    private Double montoConsultaCalculado;
    private Double copagoAplicado;
}
