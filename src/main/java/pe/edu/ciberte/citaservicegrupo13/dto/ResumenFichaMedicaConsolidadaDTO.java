package pe.edu.ciberte.citaservicegrupo13.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumenFichaMedicaConsolidadaDTO {
    private Long pacienteId;
    private String nombrePaciente;
    private Integer edad;
    private String tipoSeguro;
    private Integer totalCitasAtendidas;
    private Double montoTotalGasto;
    private List<AtencionMedicaResponseDTO> historialAtenciones;
    private List<DiagnosticoDTO> diagnosticosConsolidados;
    private Boolean requiereAtencionEspecializada;
    private String clasificacionRiesgo;
}
