package co.unicauca.piedrazul.appointment.domain.service.validator;

import co.unicauca.piedrazul.appointment.domain.model.Appointment;
import co.unicauca.piedrazul.appointment.domain.model.UserCache;
import co.unicauca.piedrazul.appointment.domain.port.out.UserValidationPort;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Valida que el paciente y el médico existan y estén activos.
 * Delega la resolución al puerto de salida — sin conocer Redis ni HTTP.
 */
@Component
public class ExistenceAppointmentValidator implements AppointmentValidator {

    private final UserValidationPort userValidationPort;

    public ExistenceAppointmentValidator(UserValidationPort userValidationPort) {
        this.userValidationPort = userValidationPort;
    }

    @Override
    public void validate(Appointment appointment, List<Appointment> existingOnDate) {
        UserCache patient = resolveUser(appointment.getPatientId(), "Paciente");
        if (!patient.isActive()) {
            throw new IllegalArgumentException("El paciente está inactivo");
        }

        UserCache doctor = resolveUser(appointment.getDoctorId(), "Médico");
        if (!doctor.isActive()) {
            throw new IllegalArgumentException("El médico está inactivo");
        }
    }

    private UserCache resolveUser(int userId, String role) {
        return userValidationPort.findUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        role + " no encontrado con ID: " + userId));
    }
}
