package com.mju.db_project.service;

import com.mju.db_project.domain.Job;
import com.mju.db_project.domain.Project;
import com.mju.db_project.domain.ProjectParticipation;
import com.mju.db_project.domain.Technology;
import com.mju.db_project.dto.request.ProjectCreateRequest;
import com.mju.db_project.dto.request.ProjectSearchRequest;
import com.mju.db_project.dto.response.ProjectCreateResponse;
import com.mju.db_project.dto.response.ProjectDashboard;
import com.mju.db_project.dto.response.ProjectDetail;
import com.mju.db_project.dto.response.ProjectSummary;
import com.mju.db_project.dto.response.RecommendedDeveloperResponse;
import com.mju.db_project.repository.JobRepository;
import com.mju.db_project.repository.ProjectParticipationRepository;
import com.mju.db_project.repository.ProjectRepository;
import com.mju.db_project.repository.TechnologyRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    // ===== S3 ===== 
    private final ProjectParticipationRepository participationRepository;

    // 1. 프로젝트 목록 조회
    @Transactional (readOnly = true)
    public List<ProjectSummary> getProjectList(ProjectSearchRequest request) {
        //날짜 확인
        LocalDate startDate = LocalDate.of(1900, 1, 1);
        LocalDate endDate = LocalDate.of(2099, 12, 31);
        LocalDate now = LocalDate.now();


        // 선택 별 범위
        if ("THIS_MONTH".equals(request.getPeriod())){
            startDate = now.withDayOfMonth(1);
            endDate = now.withDayOfMonth(now.lengthOfMonth());

        } else if ("THIS_QUARTER".equals(request.getPeriod())){
            int currentQuarterStartMonth = ((now.getMonthValue() - 1) / 3) * 3 + 1;
            startDate = LocalDate.of(now.getYear(), currentQuarterStartMonth, 1);
            endDate = startDate.plusMonths(2).withDayOfMonth(startDate.plusMonths(2).lengthOfMonth());
        }

        List<Project> projects = projectRepository.findProjectsInPeriod(startDate, endDate);

        return projects.stream()
                .filter(p-> filterByStatus(p, request.getStatus()))
                .map(p -> ProjectSummary.builder()
                    .projectId(p.getProjectId())
                    .projectName(p.getProjectName())
                    .projectStatus(calculateStatus(p))
                    .period(p.getStartDate().toString() + " ~ " + (p.getEndDate() != null ? p.getEndDate().toString() : "미정"))
                    .startDate(p.getStartDate())
                    .endDate(p.getEndDate())
                    .totalPeople(participationRepository.countByProjectId(p.getProjectId()))
                    .build())
                .collect(Collectors.toList());
            
    }

    // 2. 프로젝트 상세 조회
    @Transactional (readOnly = true)
    public ProjectDetail getProjectDetail(Integer projectId) {
        // (1) 프로젝트 기본 정보
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException(projectId + ": 일치 프로젝트 없음 "));
        
        
        List<ProjectParticipation> participations = participationRepository.findByProjectId(projectId);

        List<ProjectDetail.ProjectMember> members = participations.stream()
            .map(pp -> ProjectDetail.ProjectMember.builder()
            .employeeName(pp.getEmployee().getEmployeeName())
            .jobName(pp.getJob().getJobName())
            .inputStartDate(pp.getJoinDate())
            .inputEndDate(pp.getLeaveDate())
            .developerSkills(technologyRepository.findTechNamesByEmployeeId(pp.getId().getEmployeeId()))
            .build())
        .collect(Collectors.toList());

        return ProjectDetail.builder()
                .projectId(project.getProjectId())
                .projectName(project.getProjectName())
                .projectStatus(calculateStatus(project))
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .totalPeople(members.size())
                .members(members)
                .build();
    }

    // 대시보드 원형 그래프용
    @Transactional(readOnly = true)
    public ProjectDashboard getDashboardAnalytics(){
        LocalDate now = LocalDate.now();

        //DB에서 카운트 조회
        long totalProjects = projectRepository.count();
        long doneCount = projectRepository.countDoneProjects(now);
        long ingCount = projectRepository.countIngProjects(now);

        // 개발자 수 (중복 x)
        long totalDevelopers = participationRepository.countDistinctDevelopers();

        double donePercent = 0.0;
        double ingPercent = 0.0;

        if (totalProjects > 0){
            donePercent = Math.round((doneCount / (double) totalProjects) * 1000) / 10.0;
            ingPercent = Math.round((ingCount / (double) totalProjects) * 1000) / 10.0;
        }

        return ProjectDashboard.builder()
                .totalProjects(totalProjects)
                .ingProjectPercent(ingPercent)
                .doneProjectPercent(donePercent)
                .totalDevelopers(totalDevelopers)
                .build();
    }

    // 상태 -> 전체, 진행중, 완료
    private boolean filterByStatus(Project p, String statusFilter){

        if ("ALL".equals(statusFilter) || statusFilter == null){
            return true;
        }
        String currentStatus = calculateStatus(p);
        if ("ING".equals(statusFilter)){
            return "진행중".equals(currentStatus);
        }
        if ("DONE".equals(statusFilter)){
            return "완료".equals(currentStatus);
        }

        return true;
    }

    private String calculateStatus(Project p){
        LocalDate now = LocalDate.now();
        if (now.isBefore(p.getStartDate())) {
            return "준비중";
        }
        if (p.getEndDate() != null && now.isAfter(p.getEndDate())){
            return "완료";
        }

        return "진행중";
    }
}

