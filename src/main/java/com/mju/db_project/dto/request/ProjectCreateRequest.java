package com.mju.db_project.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProjectCreateRequest {
    private String projectName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer clientId;
    
    // 프로젝트에 필요한 직무 목록 (예: ["백엔드", "프론트엔드", "풀스택"] 등)
    private List<String> requiredJobNames;
    
    // 프로젝트에 필요한 기술 목록 (예: ["Java", "Spring", "React"] 등)
    private List<String> requiredTechNames;
}

