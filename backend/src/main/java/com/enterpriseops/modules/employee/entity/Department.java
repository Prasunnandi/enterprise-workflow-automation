package com.enterpriseops.modules.employee.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    private String leadName;
    private String escalationEmail;

    public Department() {}

    public Department(String code, String name, String leadName, String escalationEmail) {
        this.code = code;
        this.name = name;
        this.leadName = leadName;
        this.escalationEmail = escalationEmail;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLeadName() { return leadName; }
    public void setLeadName(String leadName) { this.leadName = leadName; }

    public String getEscalationEmail() { return escalationEmail; }
    public void setEscalationEmail(String escalationEmail) { this.escalationEmail = escalationEmail; }
}
