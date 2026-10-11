package pe.edu.ciberte.citaservicegrupo13;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CitaServiceGrupo13Application {

    public static void main(String[] args) {
        SpringApplication.run(CitaServiceGrupo13Application.class, args);
    }

}
