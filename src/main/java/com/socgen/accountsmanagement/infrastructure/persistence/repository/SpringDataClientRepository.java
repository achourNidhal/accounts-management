package com.socgen.accountsmanagement.infrastructure.persistence.repository;


import com.socgen.accountsmanagement.infrastructure.persistence.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataClientRepository extends JpaRepository<ClientEntity, UUID> {
}