package com.example.transfer.repository;

import com.example.transfer.entity.Transfer;
import org.springframework.data.repository.CrudRepository;

public interface TransferRepository extends CrudRepository<Transfer, Long> {
}
