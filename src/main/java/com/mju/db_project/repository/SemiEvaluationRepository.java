package com.mju.db_project.repository;

import com.mju.db_project.domain.SemiEvaluation;
import com.mju.db_project.domain.SemiEvaluationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SemiEvaluationRepository extends JpaRepository<SemiEvaluation, SemiEvaluationId> {

    @Query("SELECT AVG(se.score) FROM SemiEvaluation se WHERE se.id.evaluationId IN " +
           "(SELECT e.evaluationId FROM Evaluation e WHERE e.projectId IN " +
           "(SELECT pp.id.projectId FROM ProjectParticipation pp WHERE pp.id.employeeId = :employeeId AND pp.jobCode = :jobCode))")
    BigDecimal findAverageScoreByEmployeeAndJob(@Param("employeeId") Integer employeeId, @Param("jobCode") Integer jobCode);

    @Query("SELECT se FROM SemiEvaluation se WHERE se.id.evaluationId = :evaluationId")
    List<SemiEvaluation> findByEvaluationId(@Param("evaluationId") Integer evaluationId);

    @Query("SELECT se FROM SemiEvaluation se WHERE se.id.evaluationId IN :evaluationIds")
    List<SemiEvaluation> findByEvaluationIds(@Param("evaluationIds") List<Integer> evaluationIds);
}

