package com.socgen.accountsmanagement.domain.repository;

import com.socgen.accountsmanagement.domain.Client;

public interface ClientRepository {

    /**
     * Adds the client.
     *
     * @param client the client
     */
    Client addClient(Client client);
}