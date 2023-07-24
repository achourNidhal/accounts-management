package com.socgen.accountsmanagement.bootstrap;

import com.socgen.accountsmanagement.domain.Account;
import com.socgen.accountsmanagement.domain.Client;
import com.socgen.accountsmanagement.domain.repository.AccountRepository;
import com.socgen.accountsmanagement.domain.repository.ClientRepository;
import com.socgen.accountsmanagement.domain.service.AccountOperationsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadData implements CommandLineRunner {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final AccountOperationsService accountOperationsService;

    public LoadData(ClientRepository clientRepository, AccountRepository accountRepository, AccountOperationsService accountOperationsService) {
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
        this.accountOperationsService = accountOperationsService;
    }

    @Override
    public void run(String... args) {
        // add clients
        Client client1 = clientRepository.addClient(Client.builder().firstName("Federer").lastName("Roger").build());
        Client client2 = clientRepository.addClient(Client.builder().firstName("Djokovic").lastName("Novak").build());

        // add accounts
        Account account1 = accountRepository.addAccount(Account.builder().client(client1).balance(1000).build());
        Account account2 = accountRepository.addAccount(Account.builder().client(client2).balance(500).build());

        accountOperationsService.deposit(account1.getId(), 500d);
        accountOperationsService.withdraw(account2.getId(), 20d);
    }
}