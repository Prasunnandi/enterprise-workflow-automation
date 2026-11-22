package com.enterpriseops.modules.employee.controller;

import com.enterpriseops.modules.employee.entity.*;
import com.enterpriseops.modules.employee.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workforce")
@Tag(name = "WorkForce - Shift & Attendance Planner", description = "Endpoints for employee rosters, shift assignments, and leave requests")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final ShiftScheduleRepository shiftRepository;
    private final LeaveRequestRepository leaveRepository;

    public EmployeeController(EmployeeRepository employeeRepository,
                              DepartmentRepository departmentRepository,
                              ShiftScheduleRepository shiftRepository,
                              LeaveRequestRepository leaveRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.shiftRepository = shiftRepository;
        this.leaveRepository = leaveRepository;
    }

    @GetMapping("/employees")
    @Operation(summary = "List all company employees")
    public ResponseEntity<List<Employee>> getEmployees() {
        return ResponseEntity.ok(employeeRepository.findAll());
    }

    @GetMapping("/departments")
    @Operation(summary = "List all departments")
    public ResponseEntity<List<Department>> getDepartments() {
        return ResponseEntity.ok(departmentRepository.findAll());
    }

    @GetMapping("/shifts")
    @Operation(summary = "List employee shifts and rosters")
    public ResponseEntity<List<ShiftSchedule>> getShifts() {
        return ResponseEntity.ok(shiftRepository.findAll());
    }

    @PostMapping("/shifts")
    @Operation(summary = "Assign a shift to an employee")
    public ResponseEntity<ShiftSchedule> createShift(@RequestBody ShiftSchedule shift) {
        return ResponseEntity.ok(shiftRepository.save(shift));
    }

    @GetMapping("/leaves")
    @Operation(summary = "List leave requests")
    public ResponseEntity<List<LeaveRequest>> getLeaves() {
        return ResponseEntity.ok(leaveRepository.findAll());
    }

    @PostMapping("/leaves")
    @Operation(summary = "Submit an employee leave request")
    public ResponseEntity<LeaveRequest> createLeave(@RequestBody LeaveRequest request) {
        request.setStatus(LeaveRequest.LeaveStatus.PENDING);
        return ResponseEntity.ok(leaveRepository.save(request));
    }
}
