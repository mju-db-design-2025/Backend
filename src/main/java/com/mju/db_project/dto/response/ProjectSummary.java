package com.mju.db_project.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSummary {
    private Integer projectId;
    private String projectName;
    private String projectStatus;
    private LocalDate startDate;
    private LocalDate endDate;
    private String period;
    private Integer totalPeople;
    
}
