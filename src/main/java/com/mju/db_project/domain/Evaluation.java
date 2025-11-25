package com.mju.db_project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "EVALUATION")
@Getter
@Setter
@NoArgsConstructor
public class Evaluation {

    @Id
    @Column(name = "evaluation_id")
    private Integer evaluationId;

    @Column(name = "project_id", nullable = false)
    private Integer projectId;

    @Column(name = "evaluation_type", nullable = false, length = 10)
    private String evaluationType; // 동료, PM, 고객

    @ManyToOne
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private Project project;
}
