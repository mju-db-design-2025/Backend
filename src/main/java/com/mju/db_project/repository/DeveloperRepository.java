package com.mju.db_project.repository;

import com.mju.db_project.domain.Developer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeveloperRepository extends JpaRepository<Developer, Integer> {

    @Query("SELECT d FROM Developer d LEFT JOIN FETCH d.employee")
    List<Developer> findAllWithEmployee();
}

