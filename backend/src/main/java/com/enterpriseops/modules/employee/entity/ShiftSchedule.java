package com.enterpriseops.modules.employee.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "shift_schedules")
public class ShiftSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(nullable = false)
    private LocalDate shiftDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShiftType shiftType;

    private String notes;

    public enum ShiftType {
        MORNING_PRIMARY,
        AFTERNOON_SHIFT,
        NIGHT_STANDBY,
        ON_CALL_SUPPORT
    }

    public ShiftSchedule() {}

    public ShiftSchedule(Employee employee, LocalDate shiftDate, ShiftType shiftType, String notes) {
        this.employee = employee;
        this.shiftDate = shiftDate;
        this.shiftType = shiftType;
        this.notes = notes;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public LocalDate getShiftDate() { return shiftDate; }
    public void setShiftDate(LocalDate shiftDate) { this.shiftDate = shiftDate; }

    public ShiftType getShiftType() { return shiftType; }
    public void setShiftType(ShiftType shiftType) { this.shiftType = shiftType; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
