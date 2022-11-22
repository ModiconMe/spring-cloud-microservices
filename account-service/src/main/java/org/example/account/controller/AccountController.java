package org.example.account.controller;

import org.example.account.controller.dto.AccountRequestDTO;
import org.example.account.controller.dto.AccountResponseDTO;
import org.example.account.entity.Account;
import org.example.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{accountId}")
    public AccountResponseDTO getAccount(@PathVariable("accountId") Long accountId) {
        return new AccountResponseDTO(accountService.getAccountById(accountId));
    }

    @PostMapping("/")
    public Long createAccount(@RequestBody AccountRequestDTO accountRequestDTO) {
        return accountService.createAccount(
                accountRequestDTO.getName(),
                accountRequestDTO.getEmail(),
                accountRequestDTO.getEmail(),
                accountRequestDTO.getBills()
        );
    }

    @PutMapping("/{accountId}")
    public AccountResponseDTO updateAccount(
            @PathVariable("accountId") Long accountId,
            @RequestBody AccountRequestDTO accountRequestDTO
    ) {
        return new AccountResponseDTO(accountService.updateAccount(
                accountId,
                accountRequestDTO.getName(),
                accountRequestDTO.getEmail(),
                accountRequestDTO.getEmail(),
                accountRequestDTO.getBills()
        ));
    }

    @DeleteMapping("/{accountId}")
    public AccountResponseDTO deleteAccount(@PathVariable("accountId") Long accountId) {
        return new AccountResponseDTO(accountService.deleteAccount(accountId));
    }
}

