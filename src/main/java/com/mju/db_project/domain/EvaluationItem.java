package com.mju.db_project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "EVALUATION_ITEM")
@Getter
@Setter
@NoArgsConstructor
public class EvaluationItem {

    @Id
    @Column(name = "item_id")
    private Integer itemId;

    @Column(name = "item_name", nullable = false, length = 50)
    private String itemName;
}
