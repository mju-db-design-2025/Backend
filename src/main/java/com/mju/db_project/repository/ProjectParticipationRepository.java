package com.mju.db_project.repository;

import com.mju.db_project.domain.ProjectParticipation;
import com.mju.db_project.domain.ProjectParticipationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectParticipationRepository extends JpaRepository<ProjectParticipation, ProjectParticipationId> {

    @Query("SELECT pp FROM ProjectParticipation pp WHERE pp.id.employeeId = :employeeId")
    List<ProjectParticipation> findByEmployeeId(@Param("employeeId") Integer employeeId);

    @Query("SELECT DISTINCT pp.jobCode FROM ProjectParticipation pp WHERE pp.id.employeeId = :employeeId")
    List<Integer> findDistinctJobCodesByEmployeeId(@Param("employeeId") Integer employeeId);
}

