package com.mju.db_project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pm_evaluation")
@Getter
@Setter
@NoArgsConstructor
public class PmEvaluation {

    @Id
    @Column(name = "evaluation_id")
    private Integer evaluationId;

    @Column(name = "pm_id", nullable = false)
    private Integer pmId;

    @Column(name = "evaluated_id", nullable = false)
    private Integer evaluatedId;

    @Column(name = "evaluation_type", nullable = false, length = 10)
    private String evaluationType = "PM";

    @OneToOne
    @JoinColumn(name = "evaluation_id", insertable = false, updatable = false)
    private Evaluation evaluation;

    @ManyToOne
    @JoinColumn(name = "pm_id", insertable = false, updatable = false)
    private Employee pm;

    @ManyToOne
    @JoinColumn(name = "evaluated_id", insertable = false, updatable = false)
    private Employee evaluated;
}

