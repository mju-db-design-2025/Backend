package com.mju.db_project.repository;

import com.mju.db_project.domain.Technology;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TechnologyRepository extends JpaRepository<Technology, Integer> {
    
    Optional<Technology> findByTechName(String techName);
}

