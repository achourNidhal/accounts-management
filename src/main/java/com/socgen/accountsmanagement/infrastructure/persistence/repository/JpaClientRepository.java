package com.socgen.accountsmanagement.infrastructure.persistence.repository;

import com.socgen.accountsmanagement.domain.Client;
import com.socgen.accountsmanagement.domain.repository.ClientRepository;
import com.socgen.accountsmanagement.infrastructure.persistence.mapper.ClientMapper;
import org.springframework.stereotype.Component;

@Component
public class JpaClientRepository implements ClientRepository {

    private final SpringDataClientRepository clientRepository;

    public JpaClientRepository(final SpringDataClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Client addClient(Client client) {
        return ClientMapper.fromEntity(clientRepository.save(ClientMapper.toEntity(client)));
    }
}