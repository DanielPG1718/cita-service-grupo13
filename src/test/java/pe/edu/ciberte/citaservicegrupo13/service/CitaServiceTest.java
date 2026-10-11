package pe.edu.ciberte.citaservicegrupo13.service;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.ciberte.citaservicegrupo13.client.PacienteClient;
import pe.edu.ciberte.citaservicegrupo13.dto.AtencionMedicaResponseDTO;
import pe.edu.ciberte.citaservicegrupo13.dto.DiagnosticoDTO;
import pe.edu.ciberte.citaservicegrupo13.dto.PacienteHistorialDTO;
import pe.edu.ciberte.citaservicegrupo13.dto.ResumenFichaMedicaConsolidadaDTO;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CitaServiceTest {

    @Test
    void epsPagaVeintePorCientoDelCopagoBase() {
        CitaService service = crearService("EPS", "SEVERO");

        AtencionMedicaResponseDTO cita = service.evaluarYCompletar(1L);

        assertEquals("COMPLETADA", cita.getEstadoCita());
        assertEquals(10.00, cita.getCopagoAplicado());
        assertEquals(1, cita.getListaDiagnosticos().size());
    }

    @Test
    void essaludTieneCoberturaTotal() {
        CitaService service = crearService("ESSALUD", "LEVE");

        assertEquals(0.00, service.evaluarYCompletar(2L).getCopagoAplicado());
    }

    @Test
    void particularPagaCopagoBaseCompleto() {
        CitaService service = crearService("PARTICULAR", "LEVE");

        assertEquals(50.00, service.evaluarYCompletar(1L).getCopagoAplicado());
    }

    @Test
    void fichaConDiagnosticoSeveroEsDeAltoRiesgo() {
        CitaService service = crearService("EPS", "SEVERO");
        service.evaluarYCompletar(1L);

        ResumenFichaMedicaConsolidadaDTO ficha = service.obtenerFichaMedica(1L);

        assertEquals(1, ficha.getTotalCitasAtendidas());
        assertEquals(10.00, ficha.getMontoTotalGasto());
        assertEquals(1, ficha.getDiagnosticosConsolidados().size());
        assertTrue(ficha.getRequiereAtencionEspecializada());
        assertEquals("ALTO_RIESGO", ficha.getClasificacionRiesgo());
        assertEquals(32, ficha.getEdad());
    }

    @Test
    void fichaSinDiagnosticoSeveroEsDeBajoRiesgo() {
        CitaService service = crearService("ESSALUD", "LEVE");
        service.evaluarYCompletar(2L);

        ResumenFichaMedicaConsolidadaDTO ficha = service.obtenerFichaMedica(2L);

        assertFalse(ficha.getRequiereAtencionEspecializada());
        assertEquals("BAJO_RIESGO", ficha.getClasificacionRiesgo());
        assertEquals(0.00, ficha.getMontoTotalGasto());
    }

    @Test
    void citaInexistenteResponde404() {
        CitaService service = crearService("EPS", "LEVE");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.obtenerCita(999L));
        assertEquals(404, exception.getStatusCode().value());
    }

    private CitaService crearService(String tipoSeguro, String gravedad) {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("salud.monto-copago-base", "50.00")
                .withProperty("salud.descuento-eps", "0.80");
        return new CitaService(new PacienteClientPrueba(tipoSeguro, gravedad), environment);
    }

    private record PacienteClientPrueba(String tipoSeguro, String gravedad) implements PacienteClient {
        @Override
        public PacienteHistorialDTO obtenerPaciente(Long pacienteId) {
            return new PacienteHistorialDTO(pacienteId, "00000001", "Paciente de prueba",
                    tipoSeguro, obtenerDiagnosticos(pacienteId), "ACTIVO");
        }

        @Override
        public List<DiagnosticoDTO> obtenerDiagnosticos(Long pacienteId) {
            return List.of(new DiagnosticoDTO(1L, "J18.9", "Diagnóstico de prueba",
                    LocalDateTime.of(2026, 10, 1, 10, 0), gravedad));
        }
    }
}
