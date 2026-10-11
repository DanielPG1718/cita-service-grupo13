package pe.edu.ciberte.citaservicegrupo13.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionResponseDTO {
    @JsonProperty("monto-copago-base")
    private BigDecimal montoCopagoBase;
    @JsonProperty("descuento-eps")
    private BigDecimal descuentoEps;
    @JsonProperty("mensaje-bienvenida")
    private String mensajeBienvenida;
    @JsonProperty("api-key-aseguradora")
    private String apiKeyAseguradora;
    @JsonProperty("datasource.password")
    private String passwordBaseDatos;
}
