package com.mju.db_project.repository;

import com.mju.db_project.domain.Project;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    
    @org.springframework.data.jpa.repository.Query(value = "SELECT COALESCE(MAX(project_id), 0) FROM PROJECT", nativeQuery = true)
    Integer findMaxProjectId();


    // ===== S3 =====
    @Query("SELECT p FROM Project p " +
            "WHERE p.startDate <= :endDate " +
            "AND (p.endDate >= :startDate OR p.endDate IS NULL)")
    List<Project> findProjectsInPeriod(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
}

