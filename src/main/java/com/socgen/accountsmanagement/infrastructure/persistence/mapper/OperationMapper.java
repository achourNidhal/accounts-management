package com.socgen.accountsmanagement.infrastructure.persistence.mapper;


import com.socgen.accountsmanagement.domain.Account;
import com.socgen.accountsmanagement.domain.Operation;
import com.socgen.accountsmanagement.infrastructure.persistence.entity.AccountEntity;
import com.socgen.accountsmanagement.infrastructure.persistence.entity.OperationEntity;

import java.time.LocalDateTime;

public class OperationMapper {

    public static OperationEntity toEntity(Operation operation) {
        if (operation == null) return null;

        AccountEntity account = AccountMapper.toEntity(operation.getAccount());
        return OperationEntity.builder()
                .amount(operation.getAmount())
                .date(LocalDateTime.now())
                .type(operation.getType())
                .account(account)
                .build();
    }

    public static Operation fromEntity(OperationEntity operation) {
        if (operation == null) return null;

        Account account = AccountMapper.fromEntity(operation.getAccount());
        return Operation.builder()
                .amount(operation.getAmount())
                .date(LocalDateTime.now())
                .type(operation.getType())
                .account(account)
                .build();
    }
}