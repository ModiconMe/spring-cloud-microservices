package org.example.deposit.repository;

import org.example.deposit.entity.Deposit;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DepositRepository extends CrudRepository<Deposit, Long> {

    List<Deposit> findByEmail(String email);

}
