package co.unicauca.piedrazul.appointment.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entidad de dominio pura que representa una cita médica.
 * Sin dependencias de frameworks — solo lógica de negocio.
 */
public class Appointment {

    private int appointmentId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private AppointmentStatus status;
    private int doctorId;
    private int patientId;
    private String reason;
    private String notes;

    public Appointment() {
        this.status = AppointmentStatus.AGENDADA;
        this.reason = "Sin especificar";
    }

    public int getAppointmentId()                     { return appointmentId; }
    public void setAppointmentId(int appointmentId)   { this.appointmentId = appointmentId; }
    public LocalDate getDate()                        { return date; }
    public void setDate(LocalDate date)               { this.date = date; }
    public LocalTime getStartTime()                   { return startTime; }
    public void setStartTime(LocalTime startTime)     { this.startTime = startTime; }
    public LocalTime getEndTime()                     { return endTime; }
    public void setEndTime(LocalTime endTime)         { this.endTime = endTime; }
    public AppointmentStatus getStatus()              { return status; }
    public void setStatus(AppointmentStatus status)   { this.status = status; }
    public int getDoctorId()                          { return doctorId; }
    public void setDoctorId(int doctorId)             { this.doctorId = doctorId; }
    public int getPatientId()                         { return patientId; }
    public void setPatientId(int patientId)           { this.patientId = patientId; }
    public String getReason()                         { return reason; }
    public void setReason(String reason)              { this.reason = reason; }
    public String getNotes()                          { return notes; }
    public void setNotes(String notes)                { this.notes = notes; }
}
