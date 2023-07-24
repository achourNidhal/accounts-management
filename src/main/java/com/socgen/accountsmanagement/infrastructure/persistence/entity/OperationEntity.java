package com.socgen.accountsmanagement.infrastructure.persistence.entity;

import com.socgen.accountsmanagement.domain.enums.OperationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "operation")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OperationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(value = EnumType.STRING)
    private OperationType type;
    private Double amount;
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountEntity account;
}
