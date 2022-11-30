package org.example.account.repository;

import org.example.account.entity.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Long> {

    Optional<Account> findByEmail(String email);

    Optional<Account> findByPhone(String phone);

}
