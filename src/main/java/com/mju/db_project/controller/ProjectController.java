package com.mju.db_project.controller;

import com.mju.db_project.dto.request.ProjectCreateRequest;
import com.mju.db_project.dto.request.ProjectSearchRequest;
import com.mju.db_project.dto.response.ProjectCreateResponse;
import com.mju.db_project.dto.response.ProjectDetail;
import com.mju.db_project.dto.response.ProjectSummary;
import com.mju.db_project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Project", description = "프로젝트 관리 API")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @Operation(summary = "프로젝트 등록 및 개발자 추천", 
               description = "프로젝트를 등록하고 해당 프로젝트에 적합한 개발자를 자동으로 추천합니다.")
    public ResponseEntity<ProjectCreateResponse> createProject(@RequestBody ProjectCreateRequest request) {
        ProjectCreateResponse response = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "프로젝트 목록 조회",
                description = "기간 [\"ALL\", \"THIS_MONTH\", \"THIS_QUARTER\"] / 상태 [\"ALL\", \"ING\", \"DONE\"] 필터를 적용하여 프로젝트 목록을 조회합니다.")
    public ResponseEntity<List<ProjectSummary>> getProjectList(
            @ModelAttribute ProjectSearchRequest request
    ){
        List<ProjectSummary> response = projectService.getProjectList(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "프로젝트 상세 조회",
                description = "특정 프로젝트 상세 정보와 투입 인력 정보 조회합니다.")
    public ResponseEntity<ProjectDetail> getProjectDetail(@PathVariable Integer projectId) {
        ProjectDetail response = projectService.getProjectDetail(projectId);
        return ResponseEntity.ok(response);
    }
}

