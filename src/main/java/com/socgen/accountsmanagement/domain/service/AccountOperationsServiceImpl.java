package com.socgen.accountsmanagement.domain.service;

import com.socgen.accountsmanagement.domain.Account;
import com.socgen.accountsmanagement.domain.Operation;
import com.socgen.accountsmanagement.domain.enums.OperationType;
import com.socgen.accountsmanagement.domain.exceptions.AccountNotFoundException;
import com.socgen.accountsmanagement.domain.exceptions.InsufficientFundException;
import com.socgen.accountsmanagement.domain.repository.AccountRepository;
import com.socgen.accountsmanagement.domain.repository.OperationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service handling different account operations.
 */
@Service
public class AccountOperationsServiceImpl implements AccountOperationsService {

    private final AccountRepository accountRepository;

    private final OperationRepository operationRepository;

    public AccountOperationsServiceImpl(AccountRepository accountRepo, OperationRepository operationRepo) {
        accountRepository = accountRepo;
        operationRepository = operationRepo;
    }

    /**
     * Deposit an amount to the account.
     *
     * @param accountId the account id
     * @param amount    the amount
     * @return the updated account
     */
    @Override
    @Transactional
    public Account deposit(UUID accountId, Double amount) {

        Account account = accountRepository
                .findAccountById(accountId)
                .orElseThrow(AccountNotFoundException::new);

        account = processOperation(OperationType.DEPOSIT, account, amount);
        accountRepository.saveAccount(account);

        saveOperationHistory(OperationType.DEPOSIT, account, amount);
        return account;
    }

    /**
     * Withdraw an amount from the account.
     *
     * @param accountId the account id
     * @param amount    the amount
     * @return the updated account
     */
    @Override
    @Transactional
    public Account withdraw(UUID accountId, Double amount) {
        Account account = accountRepository.findAccountById(accountId)
                .orElseThrow(AccountNotFoundException::new);

        account = processOperation(OperationType.WITHDRAW, account, amount);
        accountRepository.saveAccount(account);

        saveOperationHistory(OperationType.WITHDRAW, account, amount);
        return account;
    }

    /**
     * Operations history of the account.
     *
     * @param accountId the account id
     * @return the list
     */
    @Override
    public List<Operation> operationsHistory(UUID accountId) {
        Account account = accountRepository
                .findAccountById(accountId)
                .orElseThrow(AccountNotFoundException::new);
        return account.getOperations();
    }

    /**
     * Process operation.
     *
     * @param operationType the operation type
     * @param account       the account
     * @param amount        the amount
     * @return the updated account
     */
    private synchronized Account processOperation(OperationType operationType, Account account, Double amount) {
        return switch (operationType) {
            case DEPOSIT -> processDeposit(account, amount);
            case WITHDRAW -> processWithDrawal(account, amount);
        };
    }

    /**
     * Save operation history.
     *
     * @param operationType the operation type
     * @param account       the account
     * @param amount        the amount
     */
    private void saveOperationHistory(OperationType operationType, Account account, Double amount) {
        Operation operation = Operation.builder()
                .type(operationType)
                .date(LocalDateTime.now())
                .account(account)
                .amount(amount)
                .build();

        operationRepository.addOperation(operation);
    }

    /**
     * Process deposit.
     *
     * @param account the account
     * @param amount  the amount
     * @return the updated account
     */
    private Account processDeposit(Account account, double amount) {
        // accept money whenever deposited
        account.setBalance(account.getBalance() + amount);
        return account;
    }

    /**
     * Process withdrawal.
     *
     * @param account the account
     * @param amount  the amount
     * @return the updated account
     * @throws UnsupportedOperationException the unsupported operation exception
     */
    private Account processWithDrawal(Account account, double amount) throws UnsupportedOperationException {
        // throw an exception if it has not sufficient funds
        if (account.getBalance() < amount)
            throw new InsufficientFundException();

        account.setBalance(account.getBalance() - amount);
        return account;
    }

}
