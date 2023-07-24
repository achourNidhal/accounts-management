package com.socgen.accountsmanagement.domain.service;

import com.socgen.accountsmanagement.domain.Account;
import com.socgen.accountsmanagement.domain.Operation;
import com.socgen.accountsmanagement.domain.enums.OperationType;
import com.socgen.accountsmanagement.domain.exceptions.AccountNotFoundException;
import com.socgen.accountsmanagement.domain.exceptions.InsufficientFundException;
import com.socgen.accountsmanagement.domain.repository.AccountRepository;
import com.socgen.accountsmanagement.domain.repository.OperationRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.util.Pair;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AccountOperationsServiceTest {

    @Mock
    AccountRepository accountRepo;
    @Mock
    OperationRepository operationRepo;

    @InjectMocks
    AccountOperationsServiceImpl underTest;


    // =================== deposit + withdraw in Multithreading context  =================== \\

    @Test
    public void deposit_should_make_deposit_to_multiple_accounts_in_multithreaded_context() throws InterruptedException {

        // GIVEN
        List<Account> accounts = new ArrayList<>();
        List<Pair<Double, OperationType>> operations = Lists.list(
                Pair.of(1000d, OperationType.DEPOSIT),
                Pair.of(800d, OperationType.DEPOSIT),
                Pair.of(50d, OperationType.WITHDRAW),
                Pair.of(200d, OperationType.DEPOSIT),
                Pair.of(300d, OperationType.WITHDRAW)
        );
        IntFunction<Double> accountInitBalance = (int i) -> (double) (1000 * i);

        // mock multiple accounts
        IntStream.rangeClosed(0, 10).forEach(i -> {
            UUID accountId = UUID.randomUUID();

            double initialBalance = accountInitBalance.apply(i);
            Account account = Account.builder().id(accountId).balance(initialBalance).build();
            accounts.add(account);
            when(accountRepo.findAccountById(accountId)).thenReturn(Optional.of(account));
        });


        // WHEN
        ExecutorService executor = Executors.newFixedThreadPool(10);
        IntStream.rangeClosed(0, 10).forEach(i ->
                executor.execute(() -> {
                    UUID accountId = accounts.get(i).getId();
                    for (Pair<Double, OperationType> op : operations) {
                        if (OperationType.DEPOSIT == op.getSecond())
                            underTest.deposit(accountId, op.getFirst());

                        if (OperationType.WITHDRAW == op.getSecond())
                            underTest.withdraw(accountId, op.getFirst());
                    }
                }));
        executor.shutdown();

        // THEN
        assertTrue(executor.awaitTermination(1, TimeUnit.SECONDS));

        IntStream.rangeClosed(0, 10).forEach(i -> {
            // expected balance = initial balance + amount resulted of different operations
            double expectedBalance = accountInitBalance.apply(i)
                    + operations.stream().filter(o -> OperationType.DEPOSIT == o.getSecond()).mapToDouble(Pair::getFirst).sum()
                    - operations.stream().filter(o -> OperationType.WITHDRAW == o.getSecond()).mapToDouble(Pair::getFirst).sum();
            assertEquals(expectedBalance, accounts.get(i).getBalance());
        });
    }


    // =================== deposit =================== \\
    @Test
    public void deposit_should_make_deposit_to_existing_account() {

        // GIVEN
        UUID accountId = UUID.randomUUID();
        double balance = 500d;
        double depositAmount = 150d;
        Account account = Account.builder().id(accountId).balance(500d).build();

        // WHEN
        when(accountRepo.findAccountById(accountId)).thenReturn(Optional.of(account));

        account = underTest.deposit(accountId, depositAmount);

        // THEN
        verify(accountRepo).findAccountById(accountId);

        double expectedBalance = balance + depositAmount;
        assertEquals(expectedBalance, account.getBalance(), "Calculated new balance is incorrect");

        ArgumentCaptor<Account> savedAccountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepo).saveAccount(savedAccountCaptor.capture());

        Account savedAccount = savedAccountCaptor.getValue();
        assertEquals(expectedBalance, savedAccount.getBalance(), "Account balance is not updated with correct value");
    }

    @Test
    public void deposit_should_save_operation_history() {

        // GIVEN
        UUID accountId = UUID.randomUUID();
        double depositAmount = 150d;
        double balance = 1000d;
        Account account = Account.builder().id(accountId).balance(balance).build();

        // WHEN
        when(accountRepo.findAccountById(accountId)).thenReturn(Optional.of(account));

        account = underTest.deposit(accountId, depositAmount);

        // THEN
        ArgumentCaptor<Operation> operationCaptor = ArgumentCaptor.forClass(Operation.class);
        verify(operationRepo).addOperation(operationCaptor.capture());

        Operation addedOperation = operationCaptor.getValue();
        assertEquals(depositAmount, addedOperation.getAmount(), "Operation saved with incorrect amount");
        assertEquals(OperationType.DEPOSIT, addedOperation.getType(), "Operation saved with incorrect operation type");
        assertEquals(account, addedOperation.getAccount(), "Operation saved with incorrect account");
        assertNotNull(addedOperation.getDate(), "Operation saved without operation date");
    }

    @Test
    public void deposit_should_throw_exception_when_unknown_account() {

        // GIVEN
        UUID accountId = UUID.randomUUID();
        double depositAmount = 150d;

        // WHEN
        when(accountRepo.findAccountById(accountId)).thenReturn(Optional.empty());

        // THEN
        assertThrows(AccountNotFoundException.class, () -> underTest.deposit(accountId, depositAmount));
    }

    @Test
    public void deposit_should_not_update_account_or_add_operation_history_when_unknown_account() {

        // GIVEN
        UUID accountId = UUID.randomUUID();
        double depositAmount = 150d;

        // WHEN
        when(accountRepo.findAccountById(accountId)).thenReturn(Optional.empty());

        // THEN
        assertThrows(AccountNotFoundException.class, () -> underTest.deposit(accountId, depositAmount));

        verify(accountRepo).findAccountById(accountId);
        verifyNoMoreInteractions(accountRepo);
        verifyNoInteractions(operationRepo);

    }


    // =================== withdraw =================== \\
    @Test
    public void withdraw_should_make_deposit_to_existing_account() {

        // GIVEN
        UUID accountId = UUID.randomUUID();
        double balance = 1000d;
        double withdrawAmount = 350d;
        Account account = Account.builder().id(accountId).balance(balance).build();

        // WHEN
        when(accountRepo.findAccountById(accountId)).thenReturn(Optional.of(account));

        account = underTest.withdraw(accountId, withdrawAmount);

        // THEN
        verify(accountRepo).findAccountById(accountId);

        double expectedBalance = balance - withdrawAmount;
        assertEquals(expectedBalance, account.getBalance(), "Calculated new balance is incorrect");

        ArgumentCaptor<Account> savedAccountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepo).saveAccount(savedAccountCaptor.capture());

        Account savedAccount = savedAccountCaptor.getValue();
        assertEquals(expectedBalance, savedAccount.getBalance(), "Account balance is not updated with correct value");
    }

    @Test
    public void withdraw_should_save_operation_history() {

        // GIVEN
        UUID accountId = UUID.randomUUID();
        double withdrawAmount = 150d;
        double balance = 500d;
        Account account = Account.builder().id(accountId).balance(balance).build();

        // WHEN
        when(accountRepo.findAccountById(accountId)).thenReturn(Optional.of(account));

        account = underTest.withdraw(accountId, withdrawAmount);

        // THEN
        ArgumentCaptor<Operation> operationCaptor = ArgumentCaptor.forClass(Operation.class);
        verify(operationRepo).addOperation(operationCaptor.capture());

        Operation addedOperation = operationCaptor.getValue();
        assertEquals(withdrawAmount, addedOperation.getAmount(), "Operation saved with incorrect amount");
        assertEquals(OperationType.WITHDRAW, addedOperation.getType(), "Operation saved with incorrect operation type");
        assertEquals(account, addedOperation.getAccount(), "Operation saved with incorrect account");
        assertNotNull(addedOperation.getDate(), "Operation saved without operation date");
    }

    @Test
    public void withdraw_should_throw_exception_when_unknown_account() {

        // GIVEN
        UUID accountId = UUID.randomUUID();
        double withdrawAmount = 350d;

        // WHEN
        when(accountRepo.findAccountById(accountId)).thenReturn(Optional.empty());

        // THEN
        assertThrows(AccountNotFoundException.class, () -> underTest.withdraw(accountId, withdrawAmount));
    }

    @Test
    public void withdraw_should_not_update_account_or_add_operation_history_when_unknown_account() {

        // GIVEN
        UUID accountId = UUID.randomUUID();
        double withdrawAmount = 150d;

        // WHEN
        when(accountRepo.findAccountById(accountId)).thenReturn(Optional.empty());

        // THEN
        assertThrows(AccountNotFoundException.class, () -> underTest.withdraw(accountId, withdrawAmount));

        verify(accountRepo).findAccountById(accountId);
        verifyNoMoreInteractions(accountRepo);
        verifyNoInteractions(operationRepo);

    }

    @Test
    public void withdraw_should_throw_InsufficientFundException_when_unknown_account() {

        // GIVEN
        UUID accountId = UUID.randomUUID();
        double withdrawAmount = 1500d;
        double balance = 500d;
        Account account = Account.builder().id(accountId).balance(balance).build();


        // WHEN
        when(accountRepo.findAccountById(accountId)).thenReturn(Optional.of(account));

        // THEN
        assertThrows(InsufficientFundException.class, () -> underTest.withdraw(accountId, withdrawAmount));

        verify(accountRepo).findAccountById(accountId);
        verifyNoMoreInteractions(accountRepo);
        verifyNoInteractions(operationRepo);

    }


    // =================== operationsHistory =================== \\
    @Test
    public void operationsHistory_should_return_account_operations() {

        // GIVEN
        UUID accountId = UUID.randomUUID();
        Account account = Account.builder().id(accountId).build();
        Function<Integer, LocalDateTime> nowDaysBefore = (Integer days) -> LocalDateTime.now().minusDays(days);

        Operation.OperationBuilder bd = Operation.builder().account(account);
        List<Operation> operations = Lists.list(
                bd.type(OperationType.DEPOSIT).amount(100d).date(nowDaysBefore.apply(1)).build(),
                bd.type(OperationType.WITHDRAW).amount(50d).date(nowDaysBefore.apply(2)).build(),
                bd.type(OperationType.DEPOSIT).amount(200d).date(nowDaysBefore.apply(5)).build(),
                bd.type(OperationType.DEPOSIT).amount(10d).date(nowDaysBefore.apply(10)).build(),
                bd.type(OperationType.WITHDRAW).amount(30d).date(nowDaysBefore.apply(25)).build(),
                bd.type(OperationType.DEPOSIT).amount(20d).date(nowDaysBefore.apply(3)).build()
        );
        account.setOperations(operations);

        // WHEN
        when(accountRepo.findAccountById(accountId)).thenReturn(Optional.of(account));


        List<Operation> result = underTest.operationsHistory(accountId);

        // THEN
        verify(accountRepo).findAccountById(accountId);
        assertEquals(operations, result);
    }
}
