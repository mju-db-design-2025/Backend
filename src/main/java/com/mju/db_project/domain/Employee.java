package com.mju.db_project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
public class Employee {

    @Id
    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "employee_name", nullable = false)
    private String employeeName;

    @Column(name = "resident_number", nullable = false, unique = true)
    private String residentNumber;

    @Column(name = "final_education", nullable = false)
    private String finalEducation;

    @Column(name = "login_id", unique = true)
    private String loginId;

    @Column(name = "login_password")
    private String loginPassword;

    @Column(name = "job_classification")
    private String jobClassification;
}

