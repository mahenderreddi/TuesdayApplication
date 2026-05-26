package com.example.demo.bank;

import com.example.demo.bank.controller.AccountController;
import com.example.demo.bank.model.Account;
import com.example.demo.bank.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AccountControllerTest {

    private MockMvc mvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        // register Java Time module so LocalDateTime serializes in tests
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        // simple in-memory AccountService implementation for controller unit test
        AccountService inMemoryService = new AccountService() {
            private Map<Long, Account> store = new HashMap<>();
            private long seq = 1L;

            @Override
            public Account createAccount(Account account) {
                account.setId(seq++);
                store.put(account.getId(), account);
                return account;
            }

            @Override
            public java.util.List<Account> getAllAccounts() {
                return new java.util.ArrayList<>(store.values());
            }

            @Override
            public Account getAccountById(Long id) {
                Account a = store.get(id);
                if (a == null) throw new RuntimeException("Not found");
                return a;
            }

            @Override
            public Account updateAccount(Long id, Account account) {
                Account existing = getAccountById(id);
                existing.setOwner(account.getOwner());
                existing.setBalance(account.getBalance());
                return existing;
            }

            @Override
            public void deleteAccount(Long id) {
                store.remove(id);
            }
        };

        AccountController controller = new AccountController(inMemoryService);
        this.mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testCreateAndGetAccount() throws Exception {
        Account account = new Account("Bob", new BigDecimal("250.00"));
        String json = objectMapper.writeValueAsString(account);

        // create
        String resp = mvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.owner").value("Bob"))
                .andReturn().getResponse().getContentAsString();

        Account created = objectMapper.readValue(resp, Account.class);

        // get
        mvc.perform(get("/api/accounts/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.owner").value("Bob"));
    }
}

