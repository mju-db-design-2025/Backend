package com.mju.db_project.controller;

import com.mju.db_project.dto.response.ClientResponse;
import com.mju.db_project.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Tag(name = "Client", description = "발주처 관리 API")
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    @Operation(summary = "발주처 목록 조회", description = "모든 발주처 목록을 조회합니다.")
    public ResponseEntity<List<ClientResponse>> getAllClients() {
        List<ClientResponse> clients = clientService.getAllClients();
        return ResponseEntity.ok(clients);
    }
}

