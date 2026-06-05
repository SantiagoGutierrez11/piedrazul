package co.unicauca.piedrazul.appointment.domain.model;

/**
 * Estados posibles de una cita médica.
 * Definido en el dominio — sin dependencias de frameworks.
 */
public enum AppointmentStatus {
    AGENDADA,
    REAGENDADA,
    CANCELADA,
    ATENDIDA
}
