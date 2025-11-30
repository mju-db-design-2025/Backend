package com.mju.db_project.repository;

import com.mju.db_project.domain.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Integer> {

    @Query("SELECT e FROM Evaluation e WHERE e.projectId = :projectId")
    List<Evaluation> findByProjectId(@Param("projectId") Integer projectId);

    @Query("SELECT e FROM Evaluation e WHERE e.projectId IN :projectIds")
    List<Evaluation> findByProjectIds(@Param("projectIds") List<Integer> projectIds);

    @Query(value = """
        SELECT e.* FROM evaluation e
        LEFT JOIN peer_evaluation pe ON e.evaluation_id = pe.evaluation_id
        LEFT JOIN pm_evaluation pm ON e.evaluation_id = pm.evaluation_id
        LEFT JOIN customer_evaluation ce ON e.evaluation_id = ce.evaluation_id
        WHERE e.project_id IN (:projectIds)
        AND (
            pe.evaluated_id = :employeeId 
            OR pm.evaluated_id = :employeeId 
            OR ce.evaluated_id = :employeeId
        )
    """, nativeQuery = true)
    List<Evaluation> findMyEvaluationsInProjects(
        @Param("projectIds") List<Integer> projectIds, 
        @Param("employeeId") Integer employeeId
    );

    /** 수동 ID 생성을 위한 max ID 조회 */
    @Query("SELECT COALESCE(MAX(e.evaluationId), 0) FROM Evaluation e")
    int findMaxId();
}

