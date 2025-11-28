package com.mju.db_project.repository;
import com.mju.db_project.domain.EvaluationItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationItemRepository extends JpaRepository<EvaluationItem, Integer> {}