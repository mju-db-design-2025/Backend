package com.mju.db_project.dto.response;

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
public class RecommendedDeveloperResponse {
    private Integer employeeId;
    private String employeeName;
    private Integer jobCode;
    private String jobName;
    private BigDecimal fitScore;
    private String recommendReason;
    private List<String> skills;
    private Integer careerYears;
    private String techGrade;
}

