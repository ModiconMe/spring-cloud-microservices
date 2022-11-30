package org.example.account.controller;

import org.example.account.controller.dto.AccountMapper;
import org.example.account.controller.dto.AccountRequestDTO;
import org.example.account.controller.dto.AccountResponseDTO;
import org.example.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final AccountMapper mapper;

    @Autowired
    public AccountController(AccountService accountService, AccountMapper mapper) {
        this.accountService = accountService;
        this.mapper = mapper;
    }

    /**
     * CREATE account
     *
     * @param accountRequestDTO request to create account
     * @return dto created account
     */
    @PostMapping
    public Long createAccount(@Valid @RequestBody AccountRequestDTO accountRequestDTO) {
        return accountService.createAccount(
                mapper.mapFromRequest(accountRequestDTO)
        );
    }

    /**
     * READ account by id.
     *
     * @param accountId account id
     * @return dto account
     */
    @GetMapping("/{accountId}")
    public AccountResponseDTO getAccount(@PathVariable("accountId") Long accountId) {
        return mapper.mapToResponse(accountService.getAccountById(accountId));
    }

    /**
     * UPDATE account by id and request.
     *
     * @param accountId         account id
     * @param accountRequestDTO request to create account
     * @return dto updated account
     */
    @PutMapping("/{accountId}")
    public AccountResponseDTO updateAccount(
            @PathVariable("accountId") Long accountId,
            @RequestBody AccountRequestDTO accountRequestDTO
    ) {
        return mapper.mapToResponse(accountService.updateAccount(
                accountId,
                mapper.mapFromRequest(accountRequestDTO)
        ));
    }

    /**
     * DELETE accoint by id.
     *
     * @param accountId account id
     * @return dto deleted account
     */
    @DeleteMapping("/{accountId}")
    public AccountResponseDTO deleteAccount(@PathVariable("accountId") Long accountId) {
        return mapper.mapToResponse(accountService.deleteAccount(accountId));
    }

    /**
     * Add bill to account by request from bill-service.
     *
     * @param accountId account id
     * @param billId    bill id
     * @return updated account
     */
    @PutMapping("add-bill/{accountId}/{billId}")
    public AccountResponseDTO addBillToAccount(
            @PathVariable("accountId") Long accountId,
            @PathVariable("billId") Long billId
    ) {
        return mapper.mapToResponse(accountService.addBillToAccount(accountId, billId));
    }

    /**
     * Remove bill from account by request from bill-service.
     *
     * @param accountId account id
     * @param billId    bill id
     * @return updated account
     */
    @PutMapping("remove-bill/{accountId}/{billId}")
    public AccountResponseDTO removeBillFromAccount(
            @PathVariable("accountId") Long accountId,
            @PathVariable("billId") Long billId
    ) {
        return mapper.mapToResponse(accountService.removeBillFromAccount(accountId, billId));
    }

}

