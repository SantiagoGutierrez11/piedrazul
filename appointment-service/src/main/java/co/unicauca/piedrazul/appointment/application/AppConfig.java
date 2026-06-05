package co.unicauca.piedrazul.appointment.application;

import co.unicauca.piedrazul.appointment.domain.service.validator.ActiveAppointmentValidator;
import co.unicauca.piedrazul.appointment.domain.service.validator.AppointmentValidator;
import co.unicauca.piedrazul.appointment.domain.service.validator.ConflictAppointmentValidator;
import co.unicauca.piedrazul.appointment.domain.service.validator.DataAppointmentValidator;
import co.unicauca.piedrazul.appointment.domain.service.validator.ExistenceAppointmentValidator;
import co.unicauca.piedrazul.appointment.domain.service.validator.HolidayValidator;
import co.unicauca.piedrazul.appointment.domain.service.validator.MedicinaGeneralValidator;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Configuración de la aplicación: beans de Spring y cadena de validadores.
 */
@Configuration
public class AppConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Appointment Service API")
                        .description("Servicio de gestión de citas médicas — Piedrazul")
                        .version("0.1.0"));
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Cadena de validadores — patrón Chain of Responsibility.
     * Orden de ejecución:
     * 1. DataAppointmentValidator      — datos básicos
     * 2. HolidayValidator              — no festivos
     * 3. ExistenceAppointmentValidator — paciente y médico existen y están activos
     * 4. ActiveAppointmentValidator    — máximo 1 cita activa por paciente
     * 5. MedicinaGeneralValidator      — paso previo por Medicina General
     * 6. ConflictAppointmentValidator  — sin conflicto de horario
     */
    @Bean
    public List<AppointmentValidator> appointmentValidators(
            DataAppointmentValidator      dataValidator,
            HolidayValidator              holidayValidator,
            ExistenceAppointmentValidator existenceValidator,
            ActiveAppointmentValidator    activeValidator,
            MedicinaGeneralValidator      medicinaGeneralValidator,
            ConflictAppointmentValidator  conflictValidator) {

        return List.of(
                dataValidator,
                holidayValidator,
                existenceValidator,
                activeValidator,
                medicinaGeneralValidator,
                conflictValidator
        );
    }
}
