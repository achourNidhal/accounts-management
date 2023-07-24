package com.socgen.accountsmanagement.domain.repository;

import com.socgen.accountsmanagement.domain.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {

    /**
     * Adds the account.
     *
     * @param account the account
     */
    Account addAccount(Account account);

    void saveAccount(Account account);

    Optional<Account> findAccountById(UUID accountId);
}