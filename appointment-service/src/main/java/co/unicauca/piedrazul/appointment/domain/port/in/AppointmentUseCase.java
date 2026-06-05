package co.unicauca.piedrazul.appointment.domain.port.in;

import co.unicauca.piedrazul.appointment.domain.model.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Puerto de entrada: define los casos de uso disponibles para el dominio de citas.
 * Los adaptadores de entrada (REST) dependen de esta interfaz, no del servicio directamente.
 */
public interface AppointmentUseCase {

    Appointment scheduleAppointment(Appointment appointment);

    Appointment rescheduleAppointment(int appointmentId, LocalDate newDate,
                                      LocalTime newStartTime, LocalTime newEndTime);

    Appointment cancelAppointment(int appointmentId);

    Appointment markAsAttended(int appointmentId);

    Appointment findById(int appointmentId);

    List<Appointment> listByDoctorAndDate(int doctorId, LocalDate date);

    List<Appointment> listAll();

    List<Appointment> listByPatient(int patientId);
}
