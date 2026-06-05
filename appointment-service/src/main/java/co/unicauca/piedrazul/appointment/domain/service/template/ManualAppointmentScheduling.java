package co.unicauca.piedrazul.appointment.domain.service.template;

import co.unicauca.piedrazul.appointment.domain.model.Appointment;
import co.unicauca.piedrazul.appointment.domain.model.AppointmentStatus;
import co.unicauca.piedrazul.appointment.domain.port.out.AppointmentEventPort;
import co.unicauca.piedrazul.appointment.domain.port.out.AppointmentRepositoryPort;
import co.unicauca.piedrazul.appointment.domain.service.validator.AppointmentValidator;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Subclase concreta del Template Method para citas agendadas manualmente por el agendador.
 * Estado asignado: AGENDADA.
 */
@Component
public class ManualAppointmentScheduling extends AppointmentSchedulingTemplate {

    public ManualAppointmentScheduling(AppointmentRepositoryPort repositoryPort,
                                       List<AppointmentValidator> validators,
                                       AppointmentEventPort eventPort) {
        super(repositoryPort, validators, eventPort);
    }

    @Override
    protected void assignStatus(Appointment appointment) {
        appointment.setStatus(AppointmentStatus.AGENDADA);
    }
}
