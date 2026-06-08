package co.unicauca.piedrazul.appointment.domain.service;

import co.unicauca.piedrazul.appointment.domain.exception.AppointmentNotFoundException;
import co.unicauca.piedrazul.appointment.domain.model.Appointment;
import co.unicauca.piedrazul.appointment.domain.model.ServiceType;
import co.unicauca.piedrazul.appointment.domain.port.in.AppointmentUseCase;
import co.unicauca.piedrazul.appointment.domain.port.out.AppointmentEventPort;
import co.unicauca.piedrazul.appointment.domain.port.out.AppointmentRepositoryPort;
import co.unicauca.piedrazul.appointment.domain.service.template.ManualAppointmentScheduling;
import co.unicauca.piedrazul.appointment.domain.service.template.RescheduleAppointmentScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Servicio de aplicación que implementa el puerto de entrada AppointmentUseCase.
 * Orquesta los objetos de dominio; las invariantes de estado viven en el agregado Appointment.
 */
@Service
public class AppointmentService implements AppointmentUseCase {

    private final AppointmentRepositoryPort repositoryPort;
    private final AppointmentEventPort eventPort;
    private final ManualAppointmentScheduling manualScheduling;
    private final RescheduleAppointmentScheduling rescheduleScheduling;

    public AppointmentService(AppointmentRepositoryPort repositoryPort,
                               AppointmentEventPort eventPort,
                               ManualAppointmentScheduling manualScheduling,
                               RescheduleAppointmentScheduling rescheduleScheduling) {
        this.repositoryPort = repositoryPort;
        this.eventPort = eventPort;
        this.manualScheduling = manualScheduling;
        this.rescheduleScheduling = rescheduleScheduling;
    }

    @Override
    @Transactional
    public Appointment scheduleAppointment(Appointment appointment) {
        return manualScheduling.execute(appointment);
    }

    @Override
    @Transactional
    public Appointment rescheduleAppointment(int appointmentId, Integer newDoctorId, String doctorName,
                                              ServiceType serviceType, LocalDate newDate,
                                              LocalTime newStartTime, LocalTime newEndTime) {
        Appointment appointment = findById(appointmentId);
        if (newDoctorId != null && newDoctorId > 0) {
            appointment.setDoctorId(newDoctorId);
        }
        if (doctorName != null && !doctorName.isBlank()) {
            appointment.setDoctorName(doctorName);
        }
        if (serviceType != null) {
            appointment.setServiceType(serviceType);
        }
        appointment.setDate(newDate);
        appointment.setStartTime(newStartTime);
        appointment.setEndTime(newEndTime);
        return rescheduleScheduling.execute(appointment);
    }

    @Override
    @Transactional
    public Appointment cancelAppointment(int appointmentId) {
        Appointment appointment = findById(appointmentId);
        appointment.cancel();
        Appointment saved = repositoryPort.save(appointment);
        eventPort.publishAppointmentEvent(saved);
        return saved;
    }

    @Override
    @Transactional
    public Appointment markAsAttended(int appointmentId) {
        Appointment appointment = findById(appointmentId);
        appointment.markAsAttended();
        Appointment saved = repositoryPort.save(appointment);
        eventPort.publishAppointmentEvent(saved);
        return saved;
    }

    @Override
    public Appointment findById(int appointmentId) {
        return repositoryPort.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(
                        "Cita no encontrada con ID: " + appointmentId));
    }

    @Override
    public List<Appointment> listByDoctorAndDate(int doctorId, LocalDate date) {
        return repositoryPort.findByDoctorIdAndDate(doctorId, date);
    }

    @Override
    public List<Appointment> listAll() {
        return repositoryPort.findAll();
    }

    @Override
    public List<Appointment> listByPatient(int patientId) {
        return repositoryPort.findByPatientIdOrderByDateDesc(patientId);
    }
}
