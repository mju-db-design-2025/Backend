package com.mju.db_project.service;

import com.mju.db_project.domain.*;
import com.mju.db_project.dto.response.RecommendedDeveloperResponse;
import com.mju.db_project.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeveloperRecommendationService {

    private final DeveloperRepository developerRepository;
    private final DeveloperSkillRepository developerSkillRepository;
    private final DeveloperRecommendationRepository developerRecommendationRepository;
    private final JobRepository jobRepository;
    private final ProjectParticipationRepository projectParticipationRepository;
    private final SemiEvaluationRepository semiEvaluationRepository;
    private final EvaluationRepository evaluationRepository;

    @Transactional
    public List<RecommendedDeveloperResponse> recommendDevelopers(
            Integer projectId, 
            List<Integer> requiredJobCodes, 
            List<Integer> requiredTechCodes) {
        List<Developer> developers = developerRepository.findAllWithEmployee();
        
        if (developers.isEmpty()) {
            log.warn("No developers found for recommendation");
            return new ArrayList<>();
        }
        
        List<RecommendedDeveloperResponse> recommendations = new ArrayList<>();
        
        // 프로젝트에 필요한 직무 목록 (없으면 모든 직무 고려)
        List<Job> targetJobs;
        if (requiredJobCodes != null && !requiredJobCodes.isEmpty()) {
            targetJobs = jobRepository.findAll().stream()
                    .filter(job -> requiredJobCodes.contains(job.getJobCode()))
                    .collect(Collectors.toList());
        } else {
            targetJobs = jobRepository.findAll();
        }
        
        if (targetJobs.isEmpty()) {
            log.warn("No target jobs found for recommendation");
            return new ArrayList<>();
        }

        for (Developer developer : developers) {
            try {
                // 보유 기술 목록 조회
                List<String> skills = getDeveloperSkills(developer.getEmployeeId());
                
                // 기본 점수 계산 (경력, 기술등급 기반)
                BigDecimal baseScore = calculateBaseScore(developer);
                
                // 기술 보유 점수 계산 (프로젝트 요구 기술과 매칭되는 경우 가산점)
                BigDecimal skillScore = calculateSkillScoreForProject(
                    developer.getEmployeeId(), 
                    requiredTechCodes
                );
                
                // 각 직무별로 적합도 점수 계산하여 가장 적합한 직무 찾기
                Integer bestJobCode = null;
                String bestJobName = "";
                BigDecimal bestFitScore = BigDecimal.ZERO;
                String bestReason = "";

                for (Job job : targetJobs) {
                    // 해당 직무에서의 평가 점수 조회
                    BigDecimal evaluationScore = getEvaluationScoreForJob(developer.getEmployeeId(), job.getJobCode());
                    
                    // 종합 적합도 점수 계산
                    // 기본점수(30%) + 기술점수(30%) + 평가점수(40%)
                    BigDecimal fitScore = baseScore.multiply(BigDecimal.valueOf(0.3))
                            .add(skillScore.multiply(BigDecimal.valueOf(0.3)))
                            .add(evaluationScore.multiply(BigDecimal.valueOf(0.4)))
                            .setScale(2, RoundingMode.HALF_UP);
                    
                    if (fitScore.compareTo(bestFitScore) > 0) {
                        bestFitScore = fitScore;
                        bestJobCode = job.getJobCode();
                        bestJobName = job.getJobName();
                        bestReason = generateRecommendReason(developer, skills, fitScore, evaluationScore, job.getJobName());
                    }
                }
                
                // 가장 적합한 직무가 없으면 기본값 사용
                if (bestJobCode == null) {
                    if (!targetJobs.isEmpty()) {
                        Job defaultJob = targetJobs.get(0);
                        bestJobCode = defaultJob.getJobCode();
                        bestJobName = defaultJob.getJobName();
                    } else {
                        bestJobCode = 1;
                        bestJobName = "";
                    }
                    bestFitScore = baseScore.multiply(BigDecimal.valueOf(0.6))
                            .add(skillScore.multiply(BigDecimal.valueOf(0.4)))
                            .setScale(2, RoundingMode.HALF_UP);
                    bestReason = generateRecommendReason(developer, skills, bestFitScore, BigDecimal.ZERO, bestJobName);
                }

                RecommendedDeveloperResponse response = RecommendedDeveloperResponse.builder()
                        .employeeId(developer.getEmployeeId())
                        .employeeName(developer.getEmployee() != null ? developer.getEmployee().getEmployeeName() : "")
                        .jobCode(bestJobCode)
                        .jobName(bestJobName)
                        .fitScore(bestFitScore)
                        .recommendReason(bestReason)
                        .skills(skills)
                        .careerYears(developer.getCareerYears())
                        .techGrade(developer.getTechGrade())
                        .build();

                recommendations.add(response);
            } catch (Exception e) {
                log.error("Error processing developer recommendation for employeeId: {}", 
                         developer.getEmployeeId(), e);
                // 개별 개발자 처리 실패 시 계속 진행
            }
        }

        // 적합도 점수 내림차순 정렬
        recommendations.sort((a, b) -> b.getFitScore().compareTo(a.getFitScore()));

        // 상위 5명만 선택
        List<RecommendedDeveloperResponse> topRecommendations = recommendations.stream()
                .limit(5)
                .collect(Collectors.toList());

        // 추천 결과를 데이터베이스에 저장 (평가가 있는 경우에만)
        // 프로젝트 생성 시점에는 평가가 없을 수 있으므로, 평가가 있을 때만 저장
        try {
            List<Evaluation> evaluations = evaluationRepository.findByProjectId(projectId);
            if (!evaluations.isEmpty()) {
                // 평가가 있으면 추천 결과 저장
                for (RecommendedDeveloperResponse recommendation : topRecommendations) {
                    saveRecommendation(
                        projectId,
                        recommendation.getEmployeeId(),
                        recommendation.getJobCode(),
                        recommendation.getFitScore(),
                        recommendation.getRecommendReason()
                    );
                }
            } else {
                log.info("No evaluation found for projectId: {}, skipping recommendation save", projectId);
            }
        } catch (Exception e) {
            log.warn("Error checking evaluation for projectId: {}, skipping recommendation save", projectId, e);
        }

        return topRecommendations;
    }

    private BigDecimal calculateBaseScore(Developer developer) {
        BigDecimal score = BigDecimal.ZERO;

        // 경력 점수 (최대 50점)
        if (developer.getCareerYears() != null) {
            int careerScore = Math.min(developer.getCareerYears() * 5, 50);
            score = score.add(BigDecimal.valueOf(careerScore));
        }

        // 기술등급 점수 (최대 50점)
        if (developer.getTechGrade() != null) {
            int gradeScore = getGradeScore(developer.getTechGrade());
            score = score.add(BigDecimal.valueOf(gradeScore));
        }

        return score;
    }

    private int getGradeScore(String techGrade) {
        if (techGrade == null) return 0;
        return switch (techGrade.toUpperCase()) {
            case "S", "시니어", "특급" -> 50;
            case "A", "시니어+", "고급" -> 45;
            case "B", "미들", "중급" -> 35;
            case "C", "주니어+", "초급" -> 25;
            case "D", "주니어" -> 15;
            default -> 10;
        };
    }

    private BigDecimal calculateSkillScore(Integer employeeId) {
        List<DeveloperSkill> skills = developerSkillRepository.findByEmployeeId(employeeId);
        // 보유 기술 수에 따라 점수 계산 (기술 1개당 10점, 최대 50점)
        int skillCount = skills.size();
        return BigDecimal.valueOf(Math.min(skillCount * 10, 50));
    }

    /**
     * 프로젝트 요구 기술을 고려한 기술 점수 계산
     */
    private BigDecimal calculateSkillScoreForProject(Integer employeeId, List<Integer> requiredTechCodes) {
        List<DeveloperSkill> developerSkills = developerSkillRepository.findByEmployeeId(employeeId);
        
        if (requiredTechCodes == null || requiredTechCodes.isEmpty()) {
            // 요구 기술이 없으면 기본 기술 점수 계산
            return calculateSkillScore(employeeId);
        }
        
        // 개발자가 보유한 기술 코드 목록
        List<Integer> developerTechCodes = developerSkills.stream()
                .map(ds -> ds.getTechnology() != null ? ds.getTechnology().getTechCode() : null)
                .filter(code -> code != null)
                .collect(Collectors.toList());
        
        // 프로젝트 요구 기술과 매칭되는 기술 수 계산
        long matchedCount = requiredTechCodes.stream()
                .filter(developerTechCodes::contains)
                .count();
        
        // 매칭률에 따라 점수 계산 (최대 50점)
        // 모든 요구 기술을 보유하면 50점, 일부만 보유하면 비례 점수
        double matchRate = requiredTechCodes.isEmpty() ? 0 : (double) matchedCount / requiredTechCodes.size();
        BigDecimal score = BigDecimal.valueOf(matchRate * 50).setScale(2, RoundingMode.HALF_UP);
        
        // 최소한 보유 기술 수에 대한 기본 점수도 고려 (최대 30점)
        int baseSkillScore = Math.min(developerSkills.size() * 3, 30);
        
        // 매칭 점수와 기본 점수 중 높은 값 사용
        return score.max(BigDecimal.valueOf(baseSkillScore));
    }

    /**
     * 개발자가 보유한 기술 코드 목록 조회
     */
    private List<Integer> getDeveloperTechCodes(Integer employeeId) {
        List<DeveloperSkill> developerSkills = developerSkillRepository.findByEmployeeId(employeeId);
        return developerSkills.stream()
                .map(ds -> ds.getTechnology() != null ? ds.getTechnology().getTechCode() : null)
                .filter(code -> code != null)
                .collect(Collectors.toList());
    }

    private List<String> getDeveloperSkills(Integer employeeId) {
        List<DeveloperSkill> developerSkills = developerSkillRepository.findByEmployeeId(employeeId);
        return developerSkills.stream()
                .map(ds -> ds.getTechnology() != null ? ds.getTechnology().getTechName() : "")
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * 특정 직무에서의 평가 점수 조회
     */
    private BigDecimal getEvaluationScoreForJob(Integer employeeId, Integer jobCode) {
        try {
            // Repository의 쿼리를 사용하여 해당 직무에서의 평가 점수 평균 조회
            BigDecimal avgScore = semiEvaluationRepository.findAverageScoreByEmployeeAndJob(employeeId, jobCode);
            
            if (avgScore != null) {
                // 평가 점수가 있으면 그대로 반환 (0-100점 범위)
                return avgScore;
            }
            
            // 평가 데이터가 없으면 기본 점수 반환
            return BigDecimal.valueOf(50);
        } catch (Exception e) {
            log.warn("Error getting evaluation score for employeeId: {}, jobCode: {}", employeeId, jobCode, e);
            return BigDecimal.valueOf(50); // 기본값
        }
    }

    private String generateRecommendReason(Developer developer, List<String> skills, BigDecimal fitScore, 
                                          BigDecimal evaluationScore, String jobName) {
        StringBuilder reason = new StringBuilder();
        
        if (jobName != null && !jobName.isEmpty()) {
            reason.append(String.format("%s 직무에서 ", jobName));
        }
        
        if (developer.getCareerYears() != null) {
            reason.append(String.format("경력 %d년, ", developer.getCareerYears()));
        }
        
        if (developer.getTechGrade() != null) {
            reason.append(String.format("기술등급 %s, ", developer.getTechGrade()));
        }
        
        if (evaluationScore.compareTo(BigDecimal.valueOf(50)) > 0) {
            reason.append(String.format("과거 평가 평균 %.1f점, ", evaluationScore));
        }
        
        if (!skills.isEmpty()) {
            reason.append(String.format("보유기술: %s", String.join(", ", skills)));
        }
        
        return reason.toString();
    }

    private void saveRecommendation(Integer projectId, Integer employeeId, Integer jobCode, 
                                   BigDecimal fitScore, String reason) {
        try {
            // 프로젝트에 대한 평가 조회 (이미 호출 전에 확인했지만, 안전을 위해 다시 확인)
            List<Evaluation> evaluations = evaluationRepository.findByProjectId(projectId);
            
            if (evaluations.isEmpty()) {
                log.warn("No evaluation found for projectId: {}, cannot save recommendation", projectId);
                return;
            }
            
            // 평가가 있으면 첫 번째 평가의 ID 사용
            Integer evaluationId = evaluations.get(0).getEvaluationId();

            DeveloperRecommendation recommendation = new DeveloperRecommendation();
            recommendation.setProjectId(projectId);
            recommendation.setRecommenderId(employeeId); // 추천받은 개발자 ID
            recommendation.setJobCode(jobCode);
            recommendation.setEvaluationId(evaluationId);
            recommendation.setFitScore(fitScore);
            recommendation.setRecommendReason(reason);
            recommendation.setRecommendDatetime(LocalDateTime.now());

            developerRecommendationRepository.save(recommendation);
            log.info("Saved recommendation for projectId: {}, employeeId: {}, jobCode: {}", 
                    projectId, employeeId, jobCode);
        } catch (Exception e) {
            log.error("Error saving recommendation for projectId: {}, employeeId: {}", 
                     projectId, employeeId, e);
            // 추천 저장 실패해도 응답은 반환
        }
    }
}

