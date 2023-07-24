package com.socgen.accountsmanagement.infrastructure.persistence.repository;

import com.socgen.accountsmanagement.domain.Operation;
import com.socgen.accountsmanagement.domain.repository.OperationRepository;
import com.socgen.accountsmanagement.infrastructure.persistence.mapper.OperationMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class JpaOperationRepository implements OperationRepository {

    private final SpringDataOperationRepository operationRepository;

    public JpaOperationRepository(final SpringDataOperationRepository repository) {
        this.operationRepository = repository;
    }


    @Override
    public void addOperation(Operation operation) {
        operationRepository.save(OperationMapper.toEntity(operation));
    }

    @Override
    public List<Operation> operationsHistory(UUID accountId) {
        return null;
    }
}