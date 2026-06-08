package co.unicauca.piedrazul.appointment.domain.service.validator;

import co.unicauca.piedrazul.appointment.domain.exception.AppointmentValidationException;
import co.unicauca.piedrazul.appointment.domain.model.Appointment;
import co.unicauca.piedrazul.appointment.domain.port.out.AppointmentRepositoryPort;
import co.unicauca.piedrazul.appointment.domain.port.out.DoctorSpecialtyPort;

import java.util.List;

/**
 * Valida que un paciente haya pasado por Consulta General antes de acceder a otros servicios.
 * Clase pura de dominio — registrada como @Bean en AppConfig.
 */
public class MedicinaGeneralValidator implements AppointmentValidator {

    private final AppointmentRepositoryPort repositoryPort;
    private final DoctorSpecialtyPort       doctorSpecialtyPort;

    public MedicinaGeneralValidator(AppointmentRepositoryPort repositoryPort,
                                    DoctorSpecialtyPort doctorSpecialtyPort) {
        this.repositoryPort      = repositoryPort;
        this.doctorSpecialtyPort = doctorSpecialtyPort;
    }

    @Override
    public void validate(Appointment appointment, List<Appointment> existingOnDate) {
        // Al reagendar (appointmentId != 0) ya se validó al crear la cita — no re-validar
        if (appointment.getAppointmentId() != 0) {
            return;
        }

        String specialty = doctorSpecialtyPort.getSpecialtyByDoctorId(appointment.getDoctorId());

        if ("Consulta General".equalsIgnoreCase(specialty)) {
            return;
        }

        List<Appointment> patientAppointments =
                repositoryPort.findByPatientIdOrderByDateDesc(appointment.getPatientId());

        boolean hasConsultaGeneral = false;
        for (Appointment past : patientAppointments) {
            if (past.getAppointmentId() == appointment.getAppointmentId()) {
                continue;
            }
            String pastSpecialty = doctorSpecialtyPort.getSpecialtyByDoctorId(past.getDoctorId());
            if ("Consulta General".equalsIgnoreCase(pastSpecialty)) {
                hasConsultaGeneral = true;
                break;
            }
        }

        if (!hasConsultaGeneral) {
            throw new AppointmentValidationException(
                    "Debes tener al menos una cita con Consulta General antes de acceder a otros servicios");
        }
    }
}
