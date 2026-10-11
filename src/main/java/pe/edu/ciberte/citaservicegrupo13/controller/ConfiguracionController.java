package pe.edu.ciberte.citaservicegrupo13.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.ciberte.citaservicegrupo13.dto.ConfiguracionResponseDTO;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/citas")
@RefreshScope
public class ConfiguracionController {
    @Value("${salud.monto-copago-base:50.00}")
    private BigDecimal montoCopagoBase;

    @Value("${salud.descuento-eps:0.80}")
    private BigDecimal descuentoEps;

    @Value("${salud.mensaje-bienvenida:Config Server no disponible}")
    private String mensajeBienvenida;

    @Value("${salud.api-key-aseguradora:}")
    private String apiKeyAseguradora;

    @Value("${spring.datasource.password:}")
    private String passwordBaseDatos;

    @GetMapping("/configuracion")
    public ConfiguracionResponseDTO obtenerConfiguracion() {
        return new ConfiguracionResponseDTO(montoCopagoBase, descuentoEps,
                mensajeBienvenida, apiKeyAseguradora, passwordBaseDatos);
    }
}
