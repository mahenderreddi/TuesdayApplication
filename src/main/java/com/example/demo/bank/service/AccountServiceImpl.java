package com.example.demo.bank.service;

import com.example.demo.bank.model.Account;
import com.example.demo.bank.repository.AccountRepository;
import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;

    public AccountServiceImpl(AccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public Account createAccount(Account account) {
        if (account.getCreatedAt() == null) {
            account.setCreatedAt(java.time.LocalDateTime.now());
        }
        return repository.save(account);
    }

    @Override
    public List<Account> getAllAccounts() {
        return repository.findAll();
    }

    @Override
    public Account getAccountById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id " + id));
    }

    @Override
    public Account updateAccount(Long id, Account account) {
        Account existing = getAccountById(id);
        existing.setOwner(account.getOwner());
        existing.setBalance(account.getBalance());
        return repository.save(existing);
    }

    @Override
    public void deleteAccount(Long id) {
        Account existing = getAccountById(id);
        repository.delete(existing);
    }
}

