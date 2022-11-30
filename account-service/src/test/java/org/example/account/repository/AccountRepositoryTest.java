package org.example.account.repository;

import org.example.account.entity.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository underTest;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void itShouldSaveAccount() {
        // given
        Account account = Account.builder()
                .name("dmitry")
                .email("dmitry@gmail.com")
                .phone("+79520009939")
                .dateOfBirth(LocalDate.of(1999, 7, 9))
                .creationDate(OffsetDateTime.now())
                .bills(new ArrayList<>())
                .build();

        // when
        underTest.save(account);

        // then
        Optional<Account> optionalExpected = underTest.findById(account.getAccountId());
        assertThat(optionalExpected.isPresent());
        assertThat(account).isEqualTo(optionalExpected.get());
    }

    @Test
    void itShouldNotSaveAccount_whenAccountNullConstraintIsDisturbed() {
        // given
        Account account = Account.builder()
                .name(null)
                .email("dmitry@gmail.com")
                .phone("+79520009939")
                .dateOfBirth(LocalDate.of(1999, 7, 9))
                .creationDate(OffsetDateTime.now())
                .bills(new ArrayList<>())
                .build();

        // when
        // then
        assertThatThrownBy(() -> {
            underTest.save(account);
            entityManager.flush();
        }).isInstanceOf(PersistenceException.class)
                .hasMessageContaining("could not execute statement");
    }

    @Test
    void itShouldNotSaveAccount_whenAccountUniqueConstraintIsDisturbed() {
        // given
        Account account1 = Account.builder()
                .name("dmitry")
                .email("dmitry@gmail.com")
                .phone("+79520009939")
                .dateOfBirth(LocalDate.of(1999, 7, 9))
                .creationDate(OffsetDateTime.now())
                .bills(new ArrayList<>())
                .build();

        Account account2 = Account.builder()
                .name("dmitry")
                .email("dmitry@gmail.com")
                .phone("+79520009939")
                .dateOfBirth(LocalDate.of(1999, 7, 9))
                .creationDate(OffsetDateTime.now())
                .bills(new ArrayList<>())
                .build();

        // when
        underTest.save(account1);
        entityManager.flush();

        // then
        assertThatThrownBy(() -> {
            underTest.save(account2);
            entityManager.flush();
        }).isInstanceOf(PersistenceException.class)
                .hasMessageContaining("could not execute statement");
    }

    @Test
    void itShouldFindAccountByEmail() {
        // given
        String email = "dmitry@gmail.com";
        Account account = Account.builder()
                .name("dmitry")
                .email(email)
                .phone("+79520009939")
                .dateOfBirth(LocalDate.of(1999, 7, 9))
                .creationDate(OffsetDateTime.now())
                .bills(new ArrayList<>())
                .build();

        underTest.save(account);

        // when
        Optional<Account> expected = underTest.findByEmail(email);

        // then
        assertThat(expected.isPresent()).isTrue();
    }

    @Test
    void itShouldNotFindAccountByEmail() {
        // given
        String email = "dmitry@gmail.com";
        Account account = Account.builder()
                .name("dmitry")
                .email("dmitry1@gmail.com")
                .phone("+79520009939")
                .dateOfBirth(LocalDate.of(1999, 7, 9))
                .creationDate(OffsetDateTime.now())
                .bills(new ArrayList<>())
                .build();

        underTest.save(account);

        // when
        Optional<Account> expected = underTest.findByEmail(email);

        // then
        assertThat(expected.isPresent()).isFalse();
    }

    @Test
    void itShouldFindAccountByPhoneNumber() {
        // given
        String phone = "+79520009939";
        Account account = Account.builder()
                .name("dmitry")
                .email("dmitry@gmail.com")
                .phone(phone)
                .dateOfBirth(LocalDate.of(1999, 7, 9))
                .creationDate(OffsetDateTime.now())
                .bills(new ArrayList<>())
                .build();

        underTest.save(account);

        // when
        Optional<Account> expected = underTest.findByPhone(phone);

        // then
        assertThat(expected.isPresent()).isTrue();
    }

    @Test
    void itShouldNotFindAccountByPhoneNumber() {
        // given
        String phone = "+79520009939";
        Account account = Account.builder()
                .name("dmitry")
                .email("dmitry@gmail.com")
                .phone("+79520009938")
                .dateOfBirth(LocalDate.of(1999, 7, 9))
                .creationDate(OffsetDateTime.now())
                .bills(new ArrayList<>())
                .build();

        underTest.save(account);

        // when
        Optional<Account> expected = underTest.findByPhone(phone);

        // then
        assertThat(expected.isPresent()).isFalse();
    }
}