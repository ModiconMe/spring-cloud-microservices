package org.example.account.service;

import org.example.account.entity.Account;
import org.springframework.transaction.annotation.Transactional;

public interface AccountService {

    Account getAccountById(Long accountId);

    Long createAccount(Account account);

    Account updateAccount(Long accountId, Account newAccount);

    @Transactional
    Account deleteAccount(Long accountId);

    Account addBillToAccount(Long accountId, Long billId);

    Account removeBillFromAccount(Long accountId, Long billId);

}
