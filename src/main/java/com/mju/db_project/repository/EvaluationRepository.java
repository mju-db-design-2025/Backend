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
}

