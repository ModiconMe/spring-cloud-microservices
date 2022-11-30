package org.example.account.service;

import org.example.account.entity.Account;
import org.example.account.utils.exception.AccountAlreadyExistException;
import org.example.account.utils.exception.AccountNotFoundException;
import org.example.account.repository.AccountRepository;
import org.example.account.rest.BillServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Autowired
    private AccountService underTest;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BillServiceClient billServiceClient;

    private static final String ACCOUNT_ALREADY_EXIST_BY_EMAIL = "Account with email %s is already exist";
    private static final String ACCOUNT_ALREADY_EXIST_BY_PHONE = "Account with phone number %s is already exist";

    @BeforeEach
    void setUp() {
        underTest = new AccountServiceImpl(accountRepository, billServiceClient);
    }

    @Test
    void itShouldGetAccountById() {
        // given
        Long accountId = 1L;
        Account account = Account
                .builder()
                .accountId(accountId)
                .name("dmitry")
                .email("dmitry@gmail.com")
                .phone("+79520009939")
                .creationDate(OffsetDateTime.now())
                .build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // when
        Account expected = underTest.getAccountById(accountId);

        // then
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(accountRepository).findById(captor.capture());
        Long value = captor.getValue();
        assertThat(accountId).isEqualTo(value);
        assertThat(account).isEqualTo(expected);
    }

    @Test
    void itShouldNotGetAccountById_whenAccountDoesNotExist() {
        // given
        Long accountId = 1L;

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.getAccountById(accountId))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("Account with id %d is not found", accountId);
    }

    @Test
    void itShouldNotCreateAccount_whenAccountEmailIsAlreadyExist() {
        // given
        Long accountId = 1L;
        String email = "dmitry@gmail.com";
        Account account = Account
                .builder()
                .accountId(accountId)
                .name("dmitry")
                .email(email)
                .phone("+79520009939")
                .creationDate(OffsetDateTime.now())
                .build();

        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(account));

        // when
        // then
        assertThatThrownBy(() -> underTest.createAccount(account))
                .isInstanceOf(AccountAlreadyExistException.class)
                .hasMessageContaining(ACCOUNT_ALREADY_EXIST_BY_EMAIL, email);
    }

    @Test
    void itShouldNotCreateAccount_whenAccountPhoneNumberIsAlreadyExist() {
        // given
        Long accountId = 1L;
        String phone = "+79520009939";
        Account account = Account
                .builder()
                .accountId(accountId)
                .name("dmitry")
                .email("dmitry@gmail.com")
                .phone(phone)
                .creationDate(OffsetDateTime.now())
                .build();

        when(accountRepository.findByPhone(phone)).thenReturn(Optional.of(account));

        // when
        // then
        assertThatThrownBy(() -> underTest.createAccount(account))
                .isInstanceOf(AccountAlreadyExistException.class)
                .hasMessageContaining(ACCOUNT_ALREADY_EXIST_BY_PHONE, phone);
    }

    @Test
    void itShouldCreateAccount() {
        // given
        Long accountId = 1L;
        Account account = Account
                .builder()
                .accountId(accountId)
                .name("dmitry")
                .email("dmitry@gmail.com")
                .phone("+79520009939")
                .creationDate(OffsetDateTime.now())
                .build();

        when(accountRepository.save(account)).thenReturn(account);

        // when
        Long expected = underTest.createAccount(account);

        // then
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        Long value = captor.getValue().getAccountId();
        assertThat(accountId).isEqualTo(value);
        assertThat(accountId).isEqualTo(expected);
    }

    @Test
    void itShouldUpdateAccountById() {
        // given
        Long accountId = 1L;
        Account account = Account
                .builder()
                .accountId(accountId)
                .name("dmitry")
                .email("dmitry@gmail.com")
                .phone("+79520009939")
                .creationDate(OffsetDateTime.now())
                .build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        // when
        underTest.updateAccount(accountId, account);

        // then
        ArgumentCaptor<Account> captor1 = ArgumentCaptor.forClass(Account.class);
        ArgumentCaptor<Long> captor2 = ArgumentCaptor.forClass(Long.class);
        verify(accountRepository).save(captor1.capture());
        verify(accountRepository).findById(captor2.capture());
        Account value1 = captor1.getValue();
        Long value2 = captor2.getValue();
        assertThat(account).isEqualTo(value1);
        assertThat(accountId).isEqualTo(value2);
    }

    @Test
    void itShouldUpdateAccountById_whenAccountEmailAlreadyExistAndThisIsTheSameAccount() {
        // given
        Long accountId = 1L;
        String email = "dmitry@gmail.com";
        Account account = Account
                .builder()
                .accountId(accountId)
                .name("dmitry")
                .email(email)
                .phone("+79520009939")
                .creationDate(OffsetDateTime.now())
                .build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        // when
        underTest.updateAccount(accountId, account);

        // then
        ArgumentCaptor<Account> captor1 = ArgumentCaptor.forClass(Account.class);
        ArgumentCaptor<Long> captor2 = ArgumentCaptor.forClass(Long.class);
        verify(accountRepository).save(captor1.capture());
        verify(accountRepository).findById(captor2.capture());
        Account value1 = captor1.getValue();
        Long value2 = captor2.getValue();
        assertThat(account).isEqualTo(value1);
        assertThat(accountId).isEqualTo(value2);
    }

    @Test
    void itShouldUpdateAccountById_whenAccountPhoneNumberAlreadyExistAndThisIsTheSameAccount() {
        // given
        Long accountId = 1L;
        String phone = "+79520009939";
        Account account = Account
                .builder()
                .accountId(accountId)
                .name("dmitry")
                .email("dmitry@gmail.com")
                .phone(phone)
                .creationDate(OffsetDateTime.now())
                .build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.findByPhone(phone)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        // when
        underTest.updateAccount(accountId, account);

        // then
        ArgumentCaptor<Account> captor1 = ArgumentCaptor.forClass(Account.class);
        ArgumentCaptor<Long> captor2 = ArgumentCaptor.forClass(Long.class);
        verify(accountRepository).save(captor1.capture());
        verify(accountRepository).findById(captor2.capture());
        Account value1 = captor1.getValue();
        Long value2 = captor2.getValue();
        assertThat(account).isEqualTo(value1);
        assertThat(accountId).isEqualTo(value2);
    }

    @Test
    void itShouldNotUpdateAccountById_whenAccountDoesNotExist() {
        // given
        Long accountId = 1L;
        Account account = Account
                .builder()
                .accountId(accountId)
                .name("dmitry")
                .email("dmitry@gmail.com")
                .phone("+79520009939")
                .creationDate(OffsetDateTime.now())
                .build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.updateAccount(accountId, account))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("Account with id %d is not found", accountId);
    }

    @Test
    void itShouldNotUpdateAccount_whenNewAccountEmailIsAlreadyExist() {
        // given
        Long accountId = 1L;
        String email = "dmitry@gmail.com";
        Account account = Account
                .builder()
                .accountId(accountId)
                .name("dmitry")
                .email(email)
                .phone("+79520009939")
                .creationDate(OffsetDateTime.now())
                .build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(new Account()));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateAccount(accountId, account))
                .isInstanceOf(AccountAlreadyExistException.class)
                .hasMessageContaining(ACCOUNT_ALREADY_EXIST_BY_EMAIL, email);
    }

    @Test
    void itShouldNotUpdateAccount_whenNewAccountPhoneNumberIsAlreadyExist() {
        // given
        Long accountId = 1L;
        String phone = "+79520009939";
        Account account = Account
                .builder()
                .accountId(accountId)
                .name("dmitry")
                .email("dmitry@gmail.com")
                .phone(phone)
                .creationDate(OffsetDateTime.now())
                .build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.findByPhone(phone)).thenReturn(Optional.of(new Account()));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateAccount(accountId, account))
                .isInstanceOf(AccountAlreadyExistException.class)
                .hasMessageContaining(ACCOUNT_ALREADY_EXIST_BY_PHONE, phone);
    }

    @Test
    void itShouldDeleteAccountById() {
        // given
        Long accountId = 1L;
        Account account = Account
                .builder()
                .accountId(accountId)
                .name("dmitry")
                .email("dmitry@gmail.com")
                .phone("+79520009939")
                .creationDate(OffsetDateTime.now())
                .build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // when
        underTest.deleteAccount(accountId);

        // then
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(billServiceClient, times(1)).deleteBillByAccountId(accountId);
        verify(accountRepository).deleteById(captor.capture());
        Long value = captor.getValue();
        assertThat(accountId).isEqualTo(value);
    }

    @Test
    void itShouldNotDeleteAccountById_whenAccountDoesNotExist() {
        // given
        Long accountId = 1L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteAccount(accountId))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("Account with id %d is not found", accountId);
    }

    @Test
    void itShouldAddBillToAccount() {
        // given
        Long accountId = 1L;
        Long billId = 1L;
        Account account = Account
                .builder()
                .accountId(accountId)
                .name("dmitry")
                .email("dmitry@gmail.com")
                .phone("+79520009939")
                .creationDate(OffsetDateTime.now())
                .build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        // when
        Account expected = underTest.addBillToAccount(accountId, billId);

        // then
        assertThat(expected.getBills().contains(billId)).isTrue();
    }

    @Test
    void itShouldNotAddBillToAccount_whenAccountDoesNotExist() {
        // given
        Long accountId = 1L;
        Long billId = 1L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.addBillToAccount(accountId, billId))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("Account with id %d is not found", accountId);
    }

    @Test
    void itShouldRemoveBillToAccount() {
        // given
        Long accountId = 1L;
        Long billId = 1L;
        Account account = Account
                .builder()
                .accountId(accountId)
                .name("dmitry")
                .email("dmitry@gmail.com")
                .phone("+79520009939")
                .creationDate(OffsetDateTime.now())
                .build();
        account.addBill(billId);
        account.addBill(2L);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        // when
        Account expected = underTest.removeBillFromAccount(accountId, billId);

        // then
        assertThat(expected.getBills().contains(billId)).isFalse();
    }

    @Test
    void itShouldNotRemoveBillToAccount_whenAccountDoesNotExist() {
        // given
        Long accountId = 1L;
        Long billId = 1L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.removeBillFromAccount(accountId, billId))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("Account with id %d is not found", accountId);
    }
}