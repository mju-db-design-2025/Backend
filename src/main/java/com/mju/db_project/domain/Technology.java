package com.mju.db_project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "technology")
@Getter
@Setter
@NoArgsConstructor
public class Technology {

    @Id
    @Column(name = "tech_code")
    private Integer techCode;

    @Column(name = "tech_name", nullable = false, unique = true)
    private String techName;
}

