package com.socgen.accountsmanagement.infrastructure.persistence.repository;

import com.socgen.accountsmanagement.domain.Account;
import com.socgen.accountsmanagement.domain.repository.AccountRepository;
import com.socgen.accountsmanagement.infrastructure.persistence.entity.AccountEntity;
import com.socgen.accountsmanagement.infrastructure.persistence.mapper.AccountMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class JpaAccountRepository implements AccountRepository {

    private final SpringDataAccountRepository accountRepository;

    public JpaAccountRepository(final SpringDataAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account addAccount(Account account) {
        return AccountMapper.fromEntity(accountRepository.save(AccountMapper.toEntity(account)));
    }

    @Override
    public void saveAccount(Account account) {
        accountRepository.save(AccountMapper.toEntity(account));
    }

    @Override
    public Optional<Account> findAccountById(UUID accountId) {
        AccountEntity account = accountRepository.findById(accountId).orElse(null);
        return Optional.ofNullable(AccountMapper.fromEntity(account));
    }
}