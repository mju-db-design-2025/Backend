package com.mju.db_project.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "evaluation")
@Getter
@Setter
@NoArgsConstructor
public class Evaluation {

    @Id
    // DB에 ID 생성을 위임하고, 생성된 ID를 객체에 받아오도록 설정합니다.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluation_id")
    private Integer evaluationId;

    @Column(name = "project_id", nullable = false)
    private Integer projectId;

    @Column(name = "evaluation_type", nullable = false, length = 10)
    private String evaluationType; // 동료, PM, 고객

    @ManyToOne
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private Project project;

    @Column(name = "submission_date") // DB 컬럼과 매핑
    private LocalDateTime submissionDate; // 제출일을 저장할 필드

    @PrePersist
    public void prePersist() {
        // 엔티티가 DB에 처음 저장되기 직전에 현재 시간을 설정합니다.
        // 이 로직은 서비스나 컨트롤러에서 시간을 설정할 필요가 없게 만듭니다.
        if (this.submissionDate == null) { // 필드가 null일 때만 현재 시간 설정(수정 시점엔 동작X)
            this.submissionDate = LocalDateTime.now();
        }
    }
}
