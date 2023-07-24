package com.socgen.accountsmanagement.infrastructure.persistence.mapper;


import com.socgen.accountsmanagement.domain.Account;
import com.socgen.accountsmanagement.domain.Client;
import com.socgen.accountsmanagement.domain.Operation;
import com.socgen.accountsmanagement.infrastructure.persistence.entity.AccountEntity;
import com.socgen.accountsmanagement.infrastructure.persistence.entity.ClientEntity;
import com.socgen.accountsmanagement.infrastructure.persistence.entity.OperationEntity;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AccountMapper {

    public static AccountEntity toEntity(Account account) {
        if (account == null) return null;

        ClientEntity client = ClientMapper.toEntity(account.getClient());

        List<OperationEntity> operations = Collections.emptyList();

        if (account.getOperations() != null)
            operations = account.getOperations().stream().map(OperationMapper::toEntity)
                    .collect(Collectors.toList());

        return AccountEntity.builder()
                .id(account.getId())
                .client(client)
                .operations(operations)
                .balance(account.getBalance())
                .build();
    }

    public static Account fromEntity(AccountEntity account) {
        if (account == null) return null;

        Client client = ClientMapper.fromEntity(account.getClient());

        List<Operation> operations = Collections.emptyList();
        if (account.getOperations() != null)
            operations = account.getOperations().stream().map(OperationMapper::fromEntity)
                    .collect(Collectors.toList());

        return Account.builder()
                .id(account.getId())
                .client(client)
                .operations(operations)
                .balance(account.getBalance())
                .build();
    }
}