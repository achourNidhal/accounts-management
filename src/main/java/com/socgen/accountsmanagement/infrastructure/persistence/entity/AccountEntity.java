package com.socgen.accountsmanagement.infrastructure.persistence.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "account")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Double balance;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "account")
    private List<OperationEntity> operations;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private ClientEntity client;
}