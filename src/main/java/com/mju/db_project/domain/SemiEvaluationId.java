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
public class SemiEvaluationId implements Serializable {

    @Column(name = "evaluation_id")
    private Integer evaluationId;

    @Column(name = "item_id")
    private Integer itemId;
}
