package com.mju.db_project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DeveloperSkillId implements Serializable {

    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "tech_code")
    private Integer techCode;
}

