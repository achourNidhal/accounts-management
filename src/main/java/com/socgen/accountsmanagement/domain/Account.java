package com.socgen.accountsmanagement.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class Account {

    private UUID id;
    private Client client;
    private double balance;
    private List<Operation> operations;
}