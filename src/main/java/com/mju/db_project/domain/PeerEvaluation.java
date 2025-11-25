package com.mju.db_project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PEER_EVALUATION")
@Getter
@Setter
@NoArgsConstructor
public class PeerEvaluation {

    @Id
    @Column(name = "evaluation_id")
    private Integer evaluationId;

    @Column(name = "evaluator_id", nullable = false)
    private Integer evaluatorId;

    @Column(name = "evaluated_id", nullable = false)
    private Integer evaluatedId;

    @Column(name = "evaluation_type", nullable = false, length = 10)
    private String evaluationType = "동료";

    @OneToOne
    @JoinColumn(name = "evaluation_id", insertable = false, updatable = false)
    private Evaluation evaluation;

    @ManyToOne
    @JoinColumn(name = "evaluator_id", insertable = false, updatable = false)
    private Employee evaluator;

    @ManyToOne
    @JoinColumn(name = "evaluated_id", insertable = false, updatable = false)
    private Employee evaluated;
}

