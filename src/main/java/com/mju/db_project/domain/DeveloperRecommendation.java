package com.mju.db_project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "DEVELOPER_RECOMMENDATION")
@Getter
@Setter
@NoArgsConstructor
public class DeveloperRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommendation_id")
    private Integer recommendationId;

    @Column(name = "recommender_id", nullable = false)
    private Integer recommenderId;

    @Column(name = "project_id", nullable = false)
    private Integer projectId;

    @Column(name = "job_code", nullable = false)
    private Integer jobCode;

    @Column(name = "evaluation_id", nullable = false)
    private Integer evaluationId;

    @Column(name = "fit_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal fitScore;

    @Column(name = "recommend_reason", length = 1000)
    private String recommendReason;

    @Column(name = "recommend_datetime", nullable = false)
    private LocalDateTime recommendDatetime;

    @Column(name = "expected_join_date")
    private LocalDate expectedJoinDate;

    @ManyToOne
    @JoinColumn(name = "recommender_id", insertable = false, updatable = false)
    private Employee recommender;

    @ManyToOne
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "job_code", insertable = false, updatable = false)
    private Job job;
}

