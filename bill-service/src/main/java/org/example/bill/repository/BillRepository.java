package org.example.bill.repository;

import feign.Param;
import org.example.bill.entity.Bill;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BillRepository extends CrudRepository<Bill, Long> {

    @Query("SELECT b FROM Bill AS b WHERE account = :accountId")
    List<Bill> findByAccountId(@Param("accountId") Long accountId);

}
