package com.mju.db_project.repository;

import com.mju.db_project.domain.DeveloperSkill;
import com.mju.db_project.domain.DeveloperSkillId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeveloperSkillRepository extends JpaRepository<DeveloperSkill, DeveloperSkillId> {

    @Query("SELECT ds FROM DeveloperSkill ds WHERE ds.id.employeeId = :employeeId")
    List<DeveloperSkill> findByEmployeeId(@Param("employeeId") Integer employeeId);
}

