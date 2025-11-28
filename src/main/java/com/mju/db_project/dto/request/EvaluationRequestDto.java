package com.mju.db_project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationRequestDto {
    private Integer targetEmpId;
    private Integer projectId;
    private List<DetailRequestDto> details;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailRequestDto {
        private Integer itemId;
        private BigDecimal score;
        private String narrative;
    }
}