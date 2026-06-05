package co.unicauca.piedrazul.appointment.domain.service.builder;

import co.unicauca.piedrazul.appointment.domain.model.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Interfaz del patrón Builder para construir objetos Appointment del dominio.
 */
public interface IAppointmentBuilder {

    IAppointmentBuilder doctorId(int doctorId);
    IAppointmentBuilder patientId(int patientId);
    IAppointmentBuilder date(LocalDate date);
    IAppointmentBuilder startTime(LocalTime startTime);
    IAppointmentBuilder endTime(LocalTime endTime);
    IAppointmentBuilder reason(String reason);
    IAppointmentBuilder notes(String notes);
    Appointment build();
}
