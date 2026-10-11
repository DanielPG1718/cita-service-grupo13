package pe.edu.ciberte.citaservicegrupo13;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
class CitaServiceGrupo13ApplicationTests {

    @Value("${local.server.port}")
    private int port;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Test
    void consultaCitaPorSuId() throws Exception {
        HttpResponse<String> response = consultar("/api/v1/citas/1");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"codigoAtencion\":\"AT-001\""));
        assertTrue(response.body().contains("\"estadoCita\":\"PROGRAMADA\""));
        assertTrue(response.body().contains("\"listaDiagnosticos\""));
    }

    @Test
    void configuracionEntregaJsonEstructurado() throws Exception {
        HttpResponse<String> response = consultar("/api/v1/citas/configuracion");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"monto-copago-base\":50.00"));
        assertTrue(response.body().contains("\"descuento-eps\":0.80"));
        assertTrue(response.body().contains("\"api-key-aseguradora\""));
        assertTrue(response.body().contains("\"datasource.password\""));
    }

    @Test
    void actuatorPublicaSalud() throws Exception {
        HttpResponse<String> response = consultar("/actuator/health");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"status\":\"UP\""));
    }

    private HttpResponse<String> consultar(String ruta) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + ruta))
                .GET()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
