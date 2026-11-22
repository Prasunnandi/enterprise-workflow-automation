package com.enterpriseops.modules.employee.repository;

import com.enterpriseops.modules.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmpCode(String empCode);
    Optional<Employee> findByEmail(String email);
    List<Employee> findByDepartmentId(Long departmentId);
}
