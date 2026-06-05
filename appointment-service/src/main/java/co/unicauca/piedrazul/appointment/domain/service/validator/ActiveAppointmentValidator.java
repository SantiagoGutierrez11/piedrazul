package co.unicauca.piedrazul.appointment.domain.service.validator;

import co.unicauca.piedrazul.appointment.domain.model.Appointment;
import co.unicauca.piedrazul.appointment.domain.model.AppointmentStatus;
import co.unicauca.piedrazul.appointment.domain.port.out.AppointmentRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Valida que un paciente no tenga más de una cita activa (AGENDADA).
 * Regla de negocio: solo se permite 1 cita agendada por paciente a la vez.
 */
@Component
public class ActiveAppointmentValidator implements AppointmentValidator {

    private final AppointmentRepositoryPort repositoryPort;

    public ActiveAppointmentValidator(AppointmentRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public void validate(Appointment appointment, List<Appointment> existingOnDate) {
        if (appointment.getAppointmentId() != 0
                && appointment.getStatus() != AppointmentStatus.AGENDADA) {
            return;
        }

        List<Appointment> activeAppointments = repositoryPort
                .findByPatientIdAndStatus(appointment.getPatientId(), AppointmentStatus.AGENDADA);

        for (Appointment active : activeAppointments) {
            if (active.getAppointmentId() != appointment.getAppointmentId()) {
                throw new IllegalArgumentException(
                        "Ya tienes una cita agendada. Debes esperar a que sea atendida o cancelada antes de agendar otra");
            }
        }
    }
}
