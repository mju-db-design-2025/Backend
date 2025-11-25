package com.mju.db_project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "DEVELOPER_SKILL")
@Getter
@Setter
@NoArgsConstructor
public class DeveloperSkill {

    @EmbeddedId
    private DeveloperSkillId id;

    @ManyToOne
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    private Developer developer;

    @ManyToOne
    @JoinColumn(name = "tech_code", insertable = false, updatable = false)
    private Technology technology;
}

