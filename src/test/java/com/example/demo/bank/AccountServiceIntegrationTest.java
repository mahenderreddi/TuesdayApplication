package com.example.demo.bank;

import com.example.demo.bank.model.Account;
import com.example.demo.bank.service.AccountService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@Transactional
public class AccountServiceIntegrationTest {

    @Autowired
    private AccountService service;

    @Test
    public void testCreateAndGetAndDelete() {
        Account account = new Account("Alice", new BigDecimal("100.00"));
        Account created = service.createAccount(account);
        Assertions.assertNotNull(created.getId());

        Account fetched = service.getAccountById(created.getId());
        Assertions.assertEquals("Alice", fetched.getOwner());

        List<Account> all = service.getAllAccounts();
        Assertions.assertTrue(all.size() >= 1);

        service.deleteAccount(created.getId());
        // after deletion, fetching should throw
        Assertions.assertThrows(RuntimeException.class, () -> service.getAccountById(created.getId()));
    }
}

