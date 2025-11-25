package com.mju.db_project.repository;

import com.mju.db_project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    
    @org.springframework.data.jpa.repository.Query(value = "SELECT COALESCE(MAX(project_id), 0) FROM project", nativeQuery = true)
    Integer findMaxProjectId();
}

