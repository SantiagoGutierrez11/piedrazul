package co.unicauca.piedrazul.appointment.domain.service.validator;

import co.unicauca.piedrazul.appointment.domain.model.Appointment;

import java.util.List;

/**
 * Contrato para validaciones de citas médicas.
 * Parte del patrón Chain of Responsibility aplicado al dominio.
 */
public interface AppointmentValidator {

    /**
     * Valida una cita antes de agendarla o reagendarla.
     *
     * @param appointment    la cita a validar
     * @param existingOnDate citas activas del mismo médico en la misma fecha
     */
    void validate(Appointment appointment, List<Appointment> existingOnDate);
}
