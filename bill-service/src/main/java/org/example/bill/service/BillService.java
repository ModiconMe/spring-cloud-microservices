package org.example.bill.service;

import org.example.bill.entity.Bill;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BillService {

    Bill getBillById(Long billId);

    @Transactional
    Long createBill(Bill bill);

    Bill updateBill(Long billId, Bill newBill);

    @Transactional
    Bill deleteBill(Long billId);

    List<Bill> deleteBillsByAccountId(Long accountId);

    List<Bill> getBillsByAccountId(Long accountId);

    @Transactional
    Bill makeBillDefault(Long billId);
}
