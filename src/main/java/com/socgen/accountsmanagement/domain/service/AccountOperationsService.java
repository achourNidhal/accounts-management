package com.socgen.accountsmanagement.domain.service;

import com.socgen.accountsmanagement.domain.Account;
import com.socgen.accountsmanagement.domain.Operation;

import java.util.List;
import java.util.UUID;

/**
 * Service handling different account operations.
 */
public interface AccountOperationsService {

    /**
     * Deposit an amount to the account.
     *
     * @param accountId the account id
     * @param amount    the amount
     * @return the account
     */
    Account deposit(UUID accountId, Double amount);

    /**
     * Withdraw an amount from the account.
     *
     * @param accountId the account id
     * @param amount    the amount
     * @return the account
     */
    Account withdraw(UUID accountId, Double amount);

    /**
     * Operations history of the account.
     *
     * @param accountId the account id
     * @return the list
     */
    List<Operation> operationsHistory(UUID accountId);

}