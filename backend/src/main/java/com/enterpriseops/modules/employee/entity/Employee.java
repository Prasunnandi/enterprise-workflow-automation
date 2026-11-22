package com.enterpriseops.modules.employee.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String empCode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @ManyToOne(optional = false)
    @JoinColumn(name = "department_id")
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public enum Role {
        EMPLOYEE,
        TECHNICIAN,
        DEPARTMENT_LEAD,
        IT_ADMIN
    }

    public Employee() {}

    public Employee(String empCode, String name, String email, Department department, Role role) {
        this.empCode = empCode;
        this.name = name;
        this.email = email;
        this.department = department;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmpCode() { return empCode; }
    public void setEmpCode(String empCode) { this.empCode = empCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
