package com.socgen.accountsmanagement.infrastructure.persistence.mapper;


import com.socgen.accountsmanagement.domain.Client;
import com.socgen.accountsmanagement.infrastructure.persistence.entity.ClientEntity;

public class ClientMapper {

    public static ClientEntity toEntity(Client client) {
        if (client == null) return null;

        return ClientEntity.builder()
                .id(client.getId())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .build();
    }

    public static Client fromEntity(ClientEntity client) {
        if (client == null) return null;

        return Client.builder()
                .id(client.getId())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .build();
    }
}