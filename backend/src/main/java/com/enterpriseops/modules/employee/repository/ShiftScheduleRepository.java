package com.enterpriseops.modules.employee.repository;

import com.enterpriseops.modules.employee.entity.ShiftSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShiftScheduleRepository extends JpaRepository<ShiftSchedule, Long> {
    List<ShiftSchedule> findByEmployeeId(Long employeeId);
    List<ShiftSchedule> findByShiftDate(LocalDate date);
}
