package pe.edu.ciberte.citaservicegrupo13.service;

import feign.FeignException;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.ciberte.citaservicegrupo13.client.PacienteClient;
import pe.edu.ciberte.citaservicegrupo13.dto.AtencionMedicaResponseDTO;
import pe.edu.ciberte.citaservicegrupo13.dto.DiagnosticoDTO;
import pe.edu.ciberte.citaservicegrupo13.dto.PacienteHistorialDTO;
import pe.edu.ciberte.citaservicegrupo13.dto.ResumenFichaMedicaConsolidadaDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CitaService {
    private final PacienteClient pacienteClient;
    private final Environment environment;
    private final Map<Long, AtencionMedicaResponseDTO> citas = new ConcurrentHashMap<>();

    // El DTO de paciente del enunciado no incluye edad; estos valores son datos de demostración.
    private final Map<Long, Integer> edadesEjemplo = Map.of(1L, 32, 2L, 48);

    public CitaService(PacienteClient pacienteClient, Environment environment) {
        this.pacienteClient = pacienteClient;
        this.environment = environment;

        citas.put(1L, new AtencionMedicaResponseDTO(1L, "AT-001",
                LocalDateTime.of(2026, 10, 10, 9, 0), 1L, "Dra. Elena Ramos",
                "Medicina Interna", "PROGRAMADA", List.of(
                new DiagnosticoDTO(1L, "J18.9", "Neumonía no especificada",
                        LocalDateTime.of(2026, 10, 1, 10, 0), "SEVERO")), 100.00, null));
        citas.put(2L, new AtencionMedicaResponseDTO(2L, "AT-002",
                LocalDateTime.of(2026, 10, 11, 11, 0), 2L, "Dr. Luis Medina",
                "Medicina General", "PROGRAMADA", List.of(
                new DiagnosticoDTO(2L, "J06.9", "Infección respiratoria aguda no especificada",
                        LocalDateTime.of(2026, 10, 2, 11, 30), "LEVE")), 120.00, null));
    }

    public AtencionMedicaResponseDTO obtenerCita(Long idCita) {
        AtencionMedicaResponseDTO cita = citas.get(idCita);
        if (cita == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada");
        }
        return cita;
    }

    public synchronized AtencionMedicaResponseDTO evaluarYCompletar(Long idCita) {
        AtencionMedicaResponseDTO cita = obtenerCita(idCita);
        PacienteHistorialDTO paciente = consultarPaciente(cita.getPacienteId());

        cita.setCopagoAplicado(calcularCopago(paciente.getTipoSeguro()));
        cita.setListaDiagnosticos(List.copyOf(paciente.getListaDiagnosticosActivos()));
        cita.setEstadoCita("COMPLETADA");
        return cita;
    }

    public ResumenFichaMedicaConsolidadaDTO obtenerFichaMedica(Long pacienteId) {
        PacienteHistorialDTO paciente = consultarPaciente(pacienteId);
        List<DiagnosticoDTO> diagnosticosPaciente;
        try {
            diagnosticosPaciente = pacienteClient.obtenerDiagnosticos(pacienteId);
        } catch (FeignException.NotFound exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente no encontrado", exception);
        }

        List<AtencionMedicaResponseDTO> atenciones = citas.values().stream()
                .filter(cita -> pacienteId.equals(cita.getPacienteId()))
                .filter(cita -> "COMPLETADA".equals(cita.getEstadoCita()))
                .sorted(Comparator.comparing(AtencionMedicaResponseDTO::getIdCita))
                .toList();

        BigDecimal gasto = atenciones.stream()
                .map(AtencionMedicaResponseDTO::getCopagoAplicado)
                .filter(Objects::nonNull)
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<Long, DiagnosticoDTO> diagnosticosUnicos = new LinkedHashMap<>();
        for (AtencionMedicaResponseDTO atencion : atenciones) {
            for (DiagnosticoDTO diagnostico : atencion.getListaDiagnosticos()) {
                diagnosticosUnicos.putIfAbsent(diagnostico.getIdDiagnostico(), diagnostico);
            }
        }
        for (DiagnosticoDTO diagnostico : diagnosticosPaciente) {
            diagnosticosUnicos.putIfAbsent(diagnostico.getIdDiagnostico(), diagnostico);
        }

        List<DiagnosticoDTO> diagnosticos = new ArrayList<>(diagnosticosUnicos.values());
        boolean requiereEspecialista = diagnosticos.stream()
                .anyMatch(diagnostico -> "SEVERO".equalsIgnoreCase(diagnostico.getGravedad()));

        return new ResumenFichaMedicaConsolidadaDTO(
                pacienteId,
                paciente.getNombreCompleto(),
                edadesEjemplo.get(pacienteId),
                paciente.getTipoSeguro(),
                atenciones.size(),
                gasto.setScale(2, RoundingMode.HALF_UP).doubleValue(),
                atenciones,
                diagnosticos,
                requiereEspecialista,
                requiereEspecialista ? "ALTO_RIESGO" : "BAJO_RIESGO"
        );
    }

    private PacienteHistorialDTO consultarPaciente(Long pacienteId) {
        try {
            return pacienteClient.obtenerPaciente(pacienteId);
        } catch (FeignException.NotFound exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente no encontrado", exception);
        }
    }

    private Double calcularCopago(String tipoSeguro) {
        BigDecimal base = new BigDecimal(environment.getProperty("salud.monto-copago-base", "50.00"));
        BigDecimal descuentoEps = new BigDecimal(environment.getProperty("salud.descuento-eps", "0.80"));

        BigDecimal copago = switch (tipoSeguro.toUpperCase(Locale.ROOT)) {
            case "EPS" -> base.multiply(BigDecimal.ONE.subtract(descuentoEps));
            case "ESSALUD" -> BigDecimal.ZERO;
            case "PARTICULAR" -> base;
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Tipo de seguro no reconocido: " + tipoSeguro);
        };
        return copago.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
