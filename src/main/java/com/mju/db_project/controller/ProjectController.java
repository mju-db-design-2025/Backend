package com.mju.db_project.controller;

import com.mju.db_project.dto.request.ProjectCreateRequest;
import com.mju.db_project.dto.response.ProjectCreateResponse;
import com.mju.db_project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}

