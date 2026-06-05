package co.unicauca.piedrazul.appointment.application;

import co.unicauca.piedrazul.appointment.domain.model.Appointment;
import co.unicauca.piedrazul.appointment.domain.model.AppointmentStatus;
import co.unicauca.piedrazul.appointment.domain.port.out.AppointmentEventPort;
import co.unicauca.piedrazul.appointment.domain.port.out.AppointmentRepositoryPort;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Al arrancar el servicio, republica todas las citas no canceladas a RabbitMQ
 * para que el medical-staff-service reconstruya su caché Redis de slots ocupados.
 */
@Component
public class StartupCachePublisher implements CommandLineRunner {

    private final AppointmentRepositoryPort repositoryPort;
    private final AppointmentEventPort      eventPort;

    public StartupCachePublisher(AppointmentRepositoryPort repositoryPort,
                                  AppointmentEventPort eventPort) {
        this.repositoryPort = repositoryPort;
        this.eventPort      = eventPort;
    }

    @Override
    public void run(String... args) {
        List<Appointment> active = repositoryPort.findAllExcludingStatus(AppointmentStatus.CANCELADA);
        for (Appointment appointment : active) {
            eventPort.publishAppointmentEvent(appointment);
        }
        System.out.printf("[StartupCachePublisher] %d citas publicadas al cache de disponibilidad%n",
                active.size());
    }
}
