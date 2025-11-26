package com.mju.db_project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "semi_evaluation")
@Getter
@Setter
@NoArgsConstructor
public class SemiEvaluation {

    @EmbeddedId
    private SemiEvaluationId id;

    @Column(name = "score", nullable = false, precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "evaluation_text", length = 1000)
    private String evaluationText;

    @ManyToOne
    @JoinColumn(name = "evaluation_id", insertable = false, updatable = false)
    private Evaluation evaluation;

    @ManyToOne
    @JoinColumn(name = "item_id", insertable = false, updatable = false)
    private EvaluationItem evaluationItem;
}
