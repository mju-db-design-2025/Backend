package com.mju.db_project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customer_evaluation")
@Getter
@Setter
@NoArgsConstructor
public class CustomerEvaluation {

    @Id
    @Column(name = "evaluation_id")
    private Integer evaluationId;

    @Column(name = "client_id", nullable = false)
    private Integer clientId;

    @Column(name = "evaluated_id", nullable = false)
    private Integer evaluatedId;

    @Column(name = "evaluation_type", nullable = false, length = 10)
    private String evaluationType = "고객";

    @OneToOne
    @JoinColumn(name = "evaluation_id", insertable = false, updatable = false)
    private Evaluation evaluation;

    @ManyToOne
    @JoinColumn(name = "client_id", insertable = false, updatable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "evaluated_id", insertable = false, updatable = false)
    private Employee evaluated;
}

