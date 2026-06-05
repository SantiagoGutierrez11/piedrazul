package co.unicauca.piedrazul.appointment.domain.service.validator;

import co.unicauca.piedrazul.appointment.domain.model.Appointment;
import co.unicauca.piedrazul.appointment.domain.port.out.AppointmentRepositoryPort;
import co.unicauca.piedrazul.appointment.domain.port.out.DoctorSpecialtyPort;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Valida que un paciente haya pasado por Medicina General antes de acceder a especialidades.
 * Regla de negocio: todo paciente debe tener al menos una cita con Medicina General.
 */
@Component
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
        String specialty = doctorSpecialtyPort.getSpecialtyByDoctorId(appointment.getDoctorId());

        if ("Medicina General".equalsIgnoreCase(specialty)) {
            return;
        }

        List<Appointment> patientAppointments =
                repositoryPort.findByPatientIdOrderByDateDesc(appointment.getPatientId());

        boolean hasMedicinaGeneral = false;
        for (Appointment past : patientAppointments) {
            if (past.getAppointmentId() == appointment.getAppointmentId()) {
                continue;
            }
            String pastSpecialty = doctorSpecialtyPort.getSpecialtyByDoctorId(past.getDoctorId());
            if ("Medicina General".equalsIgnoreCase(pastSpecialty)) {
                hasMedicinaGeneral = true;
                break;
            }
        }

        if (!hasMedicinaGeneral) {
            throw new IllegalArgumentException(
                    "Debes tener al menos una cita con Medicina General antes de acceder a especialidades");
        }
    }
}
