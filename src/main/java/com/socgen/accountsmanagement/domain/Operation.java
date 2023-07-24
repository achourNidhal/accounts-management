package com.socgen.accountsmanagement.domain;

import com.socgen.accountsmanagement.domain.enums.OperationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Operation {

    private OperationType type;
    private Double amount;
    private LocalDateTime date;
    private Account account;

}