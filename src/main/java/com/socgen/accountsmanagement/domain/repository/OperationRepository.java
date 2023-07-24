package com.socgen.accountsmanagement.domain.repository;

import com.socgen.accountsmanagement.domain.Operation;

import java.util.List;
import java.util.UUID;

/**
 * handle account operations.
 */
public interface OperationRepository {

    /**
     * Adds the operation.
     *
     * @param operation the operation
     */
    void addOperation(Operation operation);

    /**
     * Retrieve operations history.
     * NOTICE: Here we should fetch only N previous
     * operations, or could manage pagination
     *
     * @param accountId the account id
     * @return the list operations
     */
    List<Operation> operationsHistory(UUID accountId);

}