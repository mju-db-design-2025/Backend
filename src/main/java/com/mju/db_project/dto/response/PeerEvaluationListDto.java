package com.mju.db_project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeerEvaluationListDto {
    private Integer evaluationId; 
    private String projectName;
    private String targetEmpName;
    private String evaluationType;
    private LocalDateTime submissionDate;
}