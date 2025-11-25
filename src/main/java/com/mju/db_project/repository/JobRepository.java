package com.mju.db_project.repository;

import com.mju.db_project.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Integer> {
    
    Optional<Job> findByJobName(String jobName);
}

