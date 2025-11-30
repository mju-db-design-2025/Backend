package com.mju.db_project.repository;

import com.mju.db_project.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

// 동료 평가 관계
public interface PeerEvaluationRepository extends JpaRepository<PeerEvaluation, Integer> {
    // Service에서 사용: 로그인한 직원 ID(evaluatorId)가 작성한 모든 평가 목록 조회
    List<PeerEvaluation> findByEvaluatorId(Integer evaluatorId);
    
    // Service에서 사용: 특정 평가 ID를 가진 동료 평가 상세 조회
    Optional<PeerEvaluation> findByEvaluationId(Integer evaluationId);
}