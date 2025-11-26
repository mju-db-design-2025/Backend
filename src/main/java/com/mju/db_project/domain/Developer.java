package com.mju.db_project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "developer")
@Getter
@Setter
@NoArgsConstructor
public class Developer {

    @Id
    @Column(name = "employee_id")
    private Integer employeeId;

    @OneToOne
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    private Employee employee;

    @Column(name = "career_years")
    private Integer careerYears;

    @Column(name = "tech_grade")
    private String techGrade;
}

