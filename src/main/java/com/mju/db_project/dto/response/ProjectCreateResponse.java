package com.mju.db_project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreateResponse {
    private Integer projectId;
    private String projectName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer clientId;
    private List<RecommendedDeveloperResponse> recommendedDevelopers;
}

