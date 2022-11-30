package org.example.account.service;

import lombok.extern.slf4j.Slf4j;
import org.example.account.entity.Account;
import org.example.account.utils.exception.AccountAlreadyExistException;
import org.example.account.utils.exception.AccountNotFoundException;
import org.example.account.repository.AccountRepository;
import org.example.account.rest.BillServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

import static java.lang.String.format;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final BillServiceClient billServiceClient;
    private static final String ACCOUNT_NOT_FOUND_BY_ID = "Account with id %d is not found";
    private static final String ACCOUNT_ALREADY_EXIST_BY_EMAIL = "Account with email %s is already exist";
    private static final String ACCOUNT_ALREADY_EXIST_BY_PHONE = "Account with phone number %s is already exist";

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, BillServiceClient billServiceClient) {
        this.accountRepository = accountRepository;
        this.billServiceClient = billServiceClient;
    }

    /**
     * Get account by id and
     *
     * @param accountId account id
     * @return account
     * @throws AccountNotFoundException if account does not exist by id
     */
    @Override
    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(
                        () -> new AccountNotFoundException(format(ACCOUNT_NOT_FOUND_BY_ID, accountId))
                );
    }

    /**
     * Create new account and check that account does not exist by email and phone.
     *
     * @param account account to create
     * @return account id.
     */
    @Override
    public Long createAccount(Account account) {
        if (accountRepository.findByEmail(account.getEmail()).isPresent()) // check that account does not exist by email
            throw new AccountAlreadyExistException(String.format(ACCOUNT_ALREADY_EXIST_BY_EMAIL, account.getEmail()));

        if (accountRepository.findByPhone(account.getPhone()).isPresent()) // check that account does not exist by phone number
            throw new AccountAlreadyExistException(String.format(ACCOUNT_ALREADY_EXIST_BY_PHONE, account.getPhone()));

        account.setCreationDate(OffsetDateTime.now());
        Long accountId = accountRepository.save(account).getAccountId();
        log.info("Create account with id " + accountId + ", email " + account.getEmail() + ", phone number " + account.getPhone());
        return accountId;
    }

    /**
     * Update account by id.
     *
     * @param accountId  account id
     * @param newAccount new account
     * @return updated account.
     */
    @Override
    public Account updateAccount(Long accountId, Account newAccount) {
        Account account = getAccountById(accountId);

        Optional<Account> optionalAccountByEmail = accountRepository.findByEmail(newAccount.getEmail());
        if (optionalAccountByEmail.isPresent()) // check if account exist by email
            if (!accountId.equals(optionalAccountByEmail.get().getAccountId())) // check that is not the same account
                throw new AccountAlreadyExistException(String.format(ACCOUNT_ALREADY_EXIST_BY_EMAIL, account.getEmail()));

        Optional<Account> optionalAccountByPhone = accountRepository.findByPhone(newAccount.getPhone());
        if (optionalAccountByPhone.isPresent()) // check if account exist by phone number
            if (!accountId.equals(optionalAccountByPhone.get().getAccountId())) // check that is not the same account
                throw new AccountAlreadyExistException(String.format(ACCOUNT_ALREADY_EXIST_BY_PHONE, account.getPhone()));

        account.setName(newAccount.getName());
        account.setEmail(newAccount.getEmail());
        account.setPhone(newAccount.getPhone());
        account.setBills(newAccount.getBills());
        log.info("Update account with id " + accountId);
        return accountRepository.save(account);
    }

    /**
     * Delete account by id and send request to bill-service to delete all account bills.
     *
     * @param accountId account id
     * @return deleted account
     */
    @Override
    @Transactional
    public Account deleteAccount(Long accountId) {
        // send request to delete all account bills
        billServiceClient.deleteBillByAccountId(accountId);
        // delete account
        Account deletedAccount = getAccountById(accountId);
        accountRepository.deleteById(accountId);
        log.info("Delete account with id " + accountId);
        return deletedAccount;
    }

    /**
     * Get request from bill-service when bill add to account.
     *
     * @param accountId account id
     * @param billId    id of added bill
     * @return updated account
     */
    @Override
    public Account addBillToAccount(Long accountId, Long billId) {
        Account account = getAccountById(accountId);
        account.addBill(billId);
        log.info("Add bill " + billId + " to account with id " + accountId);
        return accountRepository.save(account);
    }

    /**
     * Get request from bill-service when bill remove from account.
     *
     * @param accountId account id
     * @param billId    id of removed bill
     * @return updated account
     */
    @Override
    public Account removeBillFromAccount(Long accountId, Long billId) {
        Account account = getAccountById(accountId);
        account.removeBill(billId);
        log.info("Remove bill " + billId + " from account with id " + accountId);
        return accountRepository.save(account);
    }

}
