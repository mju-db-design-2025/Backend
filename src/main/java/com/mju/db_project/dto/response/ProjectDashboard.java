package com.mju.db_project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDashboard {
    private long totalProjects;
    private double ingProjectPercent;
    private double doneProjectPercent;

    private long totalDevelopers;
}
