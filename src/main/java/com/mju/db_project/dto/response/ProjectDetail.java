package com.mju.db_project.dto.response;


import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDetail {
    private Integer projectId;
    private String projectName;
    private String projectStatus;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalPeople;
    

    private List<ProjectMember> members;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectMember{
        private String employeeName;
        private String jobName;
        private LocalDate inputStartDate;
        private LocalDate inputEndDate;
        private List<String> developerSkills;
    }
    
}
