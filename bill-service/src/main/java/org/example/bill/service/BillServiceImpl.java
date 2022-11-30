package org.example.bill.service;

import org.example.bill.entity.Bill;
import org.example.bill.utils.exception.BillNotFoundException;
import org.example.bill.repository.BillRepository;
import org.example.bill.rest.AccountServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

import static java.lang.String.format;

@Service
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final AccountServiceClient accountServiceClient;
    private static final String BILL_NOT_FOUND_BY_ID = "Bill with id %d is not found";

    @Autowired
    public BillServiceImpl(BillRepository billRepository, AccountServiceClient accountServiceClient) {
        this.billRepository = billRepository;
        this.accountServiceClient = accountServiceClient;
    }

    /**
     * Obtain bill by id.
     *
     * @param billId bill id
     * @return bill
     */
    @Override
    public Bill getBillById(Long billId) {
        return billRepository.findById(billId).orElseThrow(
                () -> new BillNotFoundException(format(BILL_NOT_FOUND_BY_ID, billId))
        );
    }

    /**
     * Create bill and send message to account-service for bi-directional links.
     *
     * @param bill bill
     * @return bill id
     */
    @Override
    @Transactional
    public Long createBill(Bill bill) {
        bill.setCreationDate(OffsetDateTime.now());
        Long billId = billRepository.save(bill).getBillId();
        accountServiceClient.addBillToAccount(bill.getAccount(), billId);
        return billId;
    }

    /**
     * Update bill.
     *
     * @param billId  bill id
     * @param newBill updated bill
     * @return updated bill
     */
    @Override
    public Bill updateBill(
            Long billId,
            Bill newBill
    ) {
        Bill bill = getBillById(billId);
        bill.setAmount(newBill.getAmount());
        bill.setDefault(newBill.isDefault());
        bill.setOverdraftEnabled(newBill.isOverdraftEnabled());
        return billRepository.save(bill);
    }

    /**
     * Delete bill and send request to account-service
     *
     * @param billId bill id
     * @return bill
     */
    @Override
    @Transactional
    public Bill deleteBill(Long billId) {
        Bill deletedBill = getBillById(billId);
        billRepository.deleteById(billId);
        accountServiceClient.removeBillFromAccount(deletedBill.getAccount(), billId);
        return deletedBill;
    }

    /**
     * Delete all accounts bills by request from account-service when account is deleted.
     *
     * @param accountId account id
     * @return list of deleted bills
     */
    @Override
    public List<Bill> deleteBillsByAccountId(Long accountId) {
        List<Bill> billsToDelete = getBillsByAccountId(accountId);
        billRepository.deleteAll(billsToDelete);
        return billsToDelete;
    }

    /**
     * Obtain all account bills by account id.
     *
     * @param accountId account id
     * @return list of account bills
     */
    @Override
    public List<Bill> getBillsByAccountId(Long accountId) {
        return billRepository.findByAccountId(accountId);
    }

    /**
     * Mark bill as account default.
     *
     * @param billId bill id
     * @return bill
     */
    @Override
    @Transactional
    public Bill makeBillDefault(Long billId) {
        Bill bill = getBillById(billId);
        // get all accounts bill
        List<Bill> accountBills = getBillsByAccountId(bill.getAccount());
        accountBills.forEach(b -> b.setDefault(false));
        bill.setDefault(true);

        billRepository.saveAll(accountBills);
        return billRepository.save(bill);
    }

}
