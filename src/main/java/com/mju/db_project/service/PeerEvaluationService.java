package com.mju.db_project.service;

import com.mju.db_project.domain.*;
import com.mju.db_project.dto.request.EvaluationRequestDto;
import com.mju.db_project.dto.response.EvaluationResponseDto;
import com.mju.db_project.dto.response.PeerEvaluationListDto;
import com.mju.db_project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PeerEvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final PeerEvaluationRepository peerEvaluationRepository;
    private final SemiEvaluationRepository semiEvaluationRepository;

    /**
     * 1. 동료평가 목록 조회 (작성자 ID 기반)
     */
    public List<PeerEvaluationListDto> getEvaluationList(Integer evaluatorId) {
        List<PeerEvaluation> peerEvaluations = peerEvaluationRepository.findByEvaluatorId(evaluatorId);

        return peerEvaluations.stream().map(peerEval -> {
            Evaluation eval = peerEval.getEvaluation(); 
            Project project = eval.getProject(); 
            Employee targetEmp = peerEval.getEvaluated();
            
            String projectName = (project != null) ? project.getProjectName() : "프로젝트명 조회 불가";

            return PeerEvaluationListDto.builder()
                    .evaluationId(eval.getEvaluationId())
                    .projectName(projectName)
                    .targetEmpName(targetEmp.getEmployeeName())
                    .evaluationType(eval.getEvaluationType())
                    .submissionDate(eval.getSubmissionDate()) // Evaluation 엔티티에서 @PrePersist로 자동 저장된 제출일 사용
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 2. 동료평가 상세 조회
     */
    public EvaluationResponseDto getEvaluationDetail(Integer evaluationId) {
        PeerEvaluation peerEval = peerEvaluationRepository.findByEvaluationId(evaluationId)
                .orElseThrow(() -> new RuntimeException("Peer Evaluation not found: " + evaluationId));
        
        Evaluation eval = peerEval.getEvaluation();
        Project project = eval.getProject();
        // N/A 처리 로직
        String projectName = (project != null) ? project.getProjectName() : "N/A";
        String targetName = peerEval.getEvaluated().getEmployeeName();
        String evaluatorName = peerEval.getEvaluator().getEmployeeName();

        List<SemiEvaluation> semiDetails = semiEvaluationRepository.findAllByEvaluationId(evaluationId);

        List<EvaluationResponseDto.DetailResponseDto> detailDtos = semiDetails.stream()
            .map(d -> EvaluationResponseDto.DetailResponseDto.builder()
                .itemId(d.getId().getItemId())
                .score(d.getScore())
                .narrative(d.getEvaluationText())
                .build())
            .collect(Collectors.toList());
        
        return EvaluationResponseDto.builder()
            .evaluationId(eval.getEvaluationId())
            .projectId(eval.getProjectId())
            .projectName(projectName)
            .targetEmpName(targetName)
            .evaluatorName(evaluatorName)
            .evaluationType(eval.getEvaluationType())
            .submissionDate(eval.getSubmissionDate()) // Evaluation 엔티티에서 @PrePersist로 자동 저장된 제출일 사용
            .details(detailDtos)
            .build();
    }
    
    /**
     * 3. 동료평가 작성 및 저장
     */
    @Transactional
    public Integer createEvaluation(Integer evaluatorId, EvaluationRequestDto dto) {
        Evaluation newEval = new Evaluation();
        newEval.setProjectId(dto.getProjectId());
        newEval.setEvaluationType("동료"); 
        newEval = evaluationRepository.save(newEval);
        
        Integer newEvaluationId = newEval.getEvaluationId();

        PeerEvaluation newPeerEval = new PeerEvaluation();
        newPeerEval.setEvaluationId(newEvaluationId);
        newPeerEval.setEvaluatorId(evaluatorId);
        newPeerEval.setEvaluatedId(dto.getTargetEmpId());
        newPeerEval.setEvaluationType("동료"); 
        peerEvaluationRepository.save(newPeerEval);

        for (EvaluationRequestDto.DetailRequestDto detailDto : dto.getDetails()) {
            SemiEvaluation semi = new SemiEvaluation();
            semi.setId(new SemiEvaluationId(newEvaluationId, detailDto.getItemId()));
            semi.setScore(detailDto.getScore());
            semi.setEvaluationText(detailDto.getNarrative());
            semiEvaluationRepository.save(semi);
        }

        return newEvaluationId;
    }
}