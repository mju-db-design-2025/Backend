package com.mju.db_project.service;

import com.mju.db_project.domain.Job;
import com.mju.db_project.domain.Project;
import com.mju.db_project.domain.Technology;
import com.mju.db_project.dto.request.ProjectCreateRequest;
import com.mju.db_project.dto.response.ProjectCreateResponse;
import com.mju.db_project.dto.response.RecommendedDeveloperResponse;
import com.mju.db_project.repository.JobRepository;
import com.mju.db_project.repository.ProjectRepository;
import com.mju.db_project.repository.TechnologyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final DeveloperRecommendationService recommendationService;
    private final JobRepository jobRepository;
    private final TechnologyRepository technologyRepository;

    @Transactional
    public ProjectCreateResponse createProject(ProjectCreateRequest request) {
        try {
            // 프로젝트 ID 수동 생성 (최대값 + 1)
            Integer maxId = projectRepository.findMaxProjectId();
            
            Integer newProjectId = (maxId == null ? 0 : maxId) + 1;
            
            // 프로젝트 생성
            Project project = new Project();
            project.setProjectId(newProjectId);
            project.setProjectName(request.getProjectName());
            project.setStartDate(request.getStartDate());
            project.setEndDate(request.getEndDate());
            project.setClientId(request.getClientId());
            

            Project savedProject = projectRepository.save(project);

            // 개발자 추천 실행 (예외 발생 시에도 프로젝트는 저장됨)
            List<RecommendedDeveloperResponse> recommendedDevelopers;
            try {
                // 직무 이름을 코드로 변환
                List<Integer> requiredJobCodes = convertJobNamesToCodes(request.getRequiredJobNames());
                
                // 기술 이름을 코드로 변환
                List<Integer> requiredTechCodes = convertTechNamesToCodes(request.getRequiredTechNames());
                
                recommendedDevelopers = recommendationService.recommendDevelopers(
                    savedProject.getProjectId(),
                    requiredJobCodes,
                    requiredTechCodes
                );      
            } catch (Exception e) {
                log.warn("Error during developer recommendation: {}", e.getMessage(), e);
                recommendedDevelopers = new java.util.ArrayList<>();
            }

            // 응답 생성
            return ProjectCreateResponse.builder()
                    .projectId(savedProject.getProjectId())
                    .projectName(savedProject.getProjectName())
                    .startDate(savedProject.getStartDate())
                    .endDate(savedProject.getEndDate())
                    .clientId(savedProject.getClientId())
                    .recommendedDevelopers(recommendedDevelopers)
                    .build();
        } catch (Exception e) {
            log.error("Error creating project: {}", e.getMessage(), e);
            log.error("Stack trace: ", e);
            throw new RuntimeException("프로젝트 생성 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 직무 이름 목록을 코드 목록으로 변환
     */
    private List<Integer> convertJobNamesToCodes(List<String> jobNames) {
        if (jobNames == null || jobNames.isEmpty()) {
            return new ArrayList<>();
        }
        
        return jobNames.stream()
                .map(jobName -> {
                    return jobRepository.findByJobName(jobName)
                            .map(Job::getJobCode)
                            .orElse(null);
                })
                .filter(code -> code != null)
                .collect(Collectors.toList());
    }

    /**
     * 기술 이름 목록을 코드 목록으로 변환
     */
    private List<Integer> convertTechNamesToCodes(List<String> techNames) {
        if (techNames == null || techNames.isEmpty()) {
            return new ArrayList<>();
        }
        
        return techNames.stream()
                .map(techName -> {
                    return technologyRepository.findByTechName(techName)
                            .map(Technology::getTechCode)
                            .orElse(null);
                })
                .filter(code -> code != null)
                .collect(Collectors.toList());
    }
}

