package com.mju.db_project.repository;

import com.mju.db_project.domain.Technology;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechnologyRepository extends JpaRepository<Technology, Integer> {
    
    Optional<Technology> findByTechName(String techName);

    @Query(value = "SELECT t.tech_name " +
            "FROM technology t " +
            "JOIN developer_skill es ON t.tech_code = es.tech_code " +
            "WHERE es.employee_id = :employeeId",
        nativeQuery = true)
    List<String> findTechNamesByEmployeeId(@Param("employeeId") Integer employeeId);
    
}

