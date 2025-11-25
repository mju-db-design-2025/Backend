package com.mju.db_project.service;

import com.mju.db_project.domain.Client;
import com.mju.db_project.dto.response.ClientResponse;
import com.mju.db_project.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {

    private final ClientRepository clientRepository;

    @Transactional(readOnly = true)
    public List<ClientResponse> getAllClients() {
        List<Client> clients = clientRepository.findAll();
        log.info("Retrieved {} clients", clients.size());
        
        return clients.stream()
                .map(client -> ClientResponse.builder()
                        .clientId(client.getClientId())
                        .clientName(client.getClientName())
                        .build())
                .collect(Collectors.toList());
    }
}

