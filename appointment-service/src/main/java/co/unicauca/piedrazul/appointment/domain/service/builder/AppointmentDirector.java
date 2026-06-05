package co.unicauca.piedrazul.appointment.domain.service.builder;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Director del patrón Builder para Appointment.
 * Orquesta el orden de los pasos de construcción según el tipo de cita.
 */
@Component
public class AppointmentDirector {

    /**
     * Configura el builder para una cita creada manualmente por el agendador.
     */
    public void buildManualAppointment(IAppointmentBuilder builder,
                                       int doctorId, int patientId,
                                       LocalDate date, LocalTime startTime, LocalTime endTime,
                                       String reason, String notes) {
        builder.doctorId(doctorId)
               .patientId(patientId)
               .date(date)
               .startTime(startTime)
               .endTime(endTime)
               .reason(reason != null ? reason : "Sin especificar")
               .notes(notes);
    }

    /**
     * Configura el builder para una cita agendada por el paciente desde la web.
     */
    public void buildWebAppointment(IAppointmentBuilder builder,
                                    int doctorId, int patientId,
                                    LocalDate date, LocalTime startTime, LocalTime endTime) {
        builder.doctorId(doctorId)
               .patientId(patientId)
               .date(date)
               .startTime(startTime)
               .endTime(endTime)
               .reason("Cita agendada por paciente");
    }
}
