package com.mju.db_project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResponseDto {
    private Integer evaluationId;
    private Integer projectId;
    private String projectName;
    private String targetEmpName;
    private String evaluatorName;
    private String evaluationType;
    private LocalDateTime submissionDate; 
    private List<DetailResponseDto> details;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailResponseDto {
        private Integer itemId;
        private BigDecimal score;
        private String narrative;
    }
}