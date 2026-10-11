package pe.edu.ciberte.citaservicegrupo13.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosticoDTO {
    private Long idDiagnostico;
    private String codigoCIE10;
    private String descripcion;
    private LocalDateTime fechaDiagnostico;
    private String gravedad;
}
