package com.mju.db_project.repository;

import com.mju.db_project.domain.DeveloperRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeveloperRecommendationRepository extends JpaRepository<DeveloperRecommendation, Integer> {

    List<DeveloperRecommendation> findByProjectIdOrderByFitScoreDesc(Integer projectId);
}

