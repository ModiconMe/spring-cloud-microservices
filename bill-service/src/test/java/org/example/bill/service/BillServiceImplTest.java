package org.example.bill.service;

import org.example.bill.entity.Bill;
import org.example.bill.utils.exception.BillNotFoundException;
import org.example.bill.repository.BillRepository;
import org.example.bill.rest.AccountServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillServiceImplTest {

    @Autowired
    private BillService underTest;

    @Mock
    private BillRepository billRepository;

    @Mock
    private AccountServiceClient accountServiceClient;

    private static final String BILL_NOT_FOUND_BY_ID = "Bill with id %d is not found";

    @BeforeEach
    void setUp() {
        underTest = new BillServiceImpl(billRepository, accountServiceClient);
    }

    @Test
    void itShouldGetBillById() {
        // given
        Bill bill = getBill();
        when(billRepository.findById(anyLong())).thenReturn(Optional.of(bill));

        // when
        Long billId = bill.getBillId();
        underTest.getBillById(billId);

        // then
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(billRepository).findById(captor.capture());
        Long value = captor.getValue();
        assertThat(value).isEqualTo(billId);
    }

    @Test
    void itShouldNotGetBillById_whenAccountDoesNotExist() {
        // given
        Bill bill = getBill();
        Long billId = bill.getBillId();
        when(billRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.getBillById(billId))
                .isInstanceOf(BillNotFoundException.class)
                .hasMessageContaining(BILL_NOT_FOUND_BY_ID, billId);
    }

    @Test
    void itShouldCreateBill() {
        // given
        Bill bill = getBill();
        when(billRepository.save(bill)).thenReturn(bill);

        // when
        underTest.createBill(bill);

        // then
        ArgumentCaptor<Bill> captor = ArgumentCaptor.forClass(Bill.class);
        verify(accountServiceClient).addBillToAccount(bill.getAccount(), bill.getBillId());
        verify(billRepository).save(captor.capture());
        Bill value = captor.getValue();
        assertThat(value).isEqualTo(bill);
    }

    @Test
    void itShouldUpdateBill() {
        // given
        Bill bill = getBill();
        Bill newBill = getBill();
        newBill.setAmount(new BigDecimal(200));
        when(billRepository.findById(anyLong())).thenReturn(Optional.of(bill));
        when(billRepository.save(newBill)).thenReturn(newBill);

        // when
        Bill updatedBill = underTest.updateBill(bill.getBillId(), newBill);

        // then
        ArgumentCaptor<Long> captor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Bill> captor2 = ArgumentCaptor.forClass(Bill.class);
        verify(billRepository).findById(captor1.capture());
        verify(billRepository).save(captor2.capture());
        Long value1 = captor1.getValue();
        Bill value2 = captor2.getValue();
        assertThat(value1).isEqualTo(bill.getBillId());
        assertThat(value2).isEqualTo(newBill);
        assertThat(updatedBill).isEqualTo(newBill);
    }

    @Test
    void itShouldNotUpdateBill_whenBillDoesNotExistById() {
        // given
        Bill bill = getBill();
        Bill newBill = getBill();
        newBill.setAmount(new BigDecimal(200));
        Long billId = bill.getBillId();
        when(billRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.updateBill(billId, newBill))
                .isInstanceOf(BillNotFoundException.class)
                .hasMessageContaining(BILL_NOT_FOUND_BY_ID, billId);
        verify(billRepository, times(0)).save(newBill);
    }

    @Test
    void itShouldDeleteBill() {
        // given
        Bill bill = getBill();
        when(billRepository.findById(anyLong())).thenReturn(Optional.of(bill));

        // when
        Bill deletedBill = underTest.deleteBill(bill.getBillId());

        // then
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(billRepository).findById(captor.capture());
        verify(accountServiceClient).removeBillFromAccount(bill.getAccount(), bill.getBillId());
        Long value= captor.getValue();
        assertThat(value).isEqualTo(bill.getBillId());
        assertThat(deletedBill).isEqualTo(bill);
    }

    @Test
    void itShouldNotDeleteBill_whenBillDoesNotExistById() {
        // given
        Bill bill = getBill();
        Long billId = bill.getBillId();
        when(billRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteBill(billId))
                .isInstanceOf(BillNotFoundException.class)
                .hasMessageContaining(BILL_NOT_FOUND_BY_ID, billId);
    }

    @Test
    void itShouldGetBillsByAccountId() {
        // given
        Bill bill = getBill();
        List<Bill> bills = new ArrayList<>();
        bills.add(bill);
        Long account = bill.getAccount();
        when(billRepository.findByAccountId(account)).thenReturn(bills);

        // when
        List<Bill> expected = underTest.getBillsByAccountId(account);

        // then
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(billRepository).findByAccountId(captor.capture());
        Long value= captor.getValue();
        assertThat(value).isEqualTo(bill.getAccount());
        assertThat(expected).isEqualTo(bills);
    }

    @Test
    void itShouldMarkBillAsDefault() {
        // given
        Bill bill1 = getBill();
        Bill bill2 = getBill();
        bill2.setDefault(false);

        List<Bill> bills = new ArrayList<>();
        bills.add(bill1);
        bills.add(bill2);

        Long account = bill1.getAccount();
        Long billId = bill2.getBillId();
        when(billRepository.findById(billId)).thenReturn(Optional.of(bill2));
        when(billRepository.findByAccountId(account)).thenReturn(bills);
        when(billRepository.save(bill2)).thenReturn(bill2);

        // when
        Bill expected = underTest.makeBillDefault(billId);

        // then
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(billRepository).findByAccountId(captor.capture());
        Long value= captor.getValue();
        assertThat(value).isEqualTo(bill2.getBillId());
        assertThat(expected).isEqualTo(bill2);
        assertThat(bill1.isDefault()).isFalse();
        assertThat(bill2.isDefault()).isTrue();
    }

    @Test
    void itShouldNotMarkBillAsDefault_whenBillDoesNotExistById() {
        // given
        Bill bill = getBill();
        bill.setDefault(false);

        Long billId = bill.getBillId();
        when(billRepository.findById(billId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.makeBillDefault(billId))
                .isInstanceOf(BillNotFoundException.class)
                .hasMessageContaining(BILL_NOT_FOUND_BY_ID, billId);
        verifyNoMoreInteractions(billRepository);
        verifyNoMoreInteractions(accountServiceClient);
    }

    private Bill getBill() {
        return Bill.builder()
                .billId(1L)
                .amount(new BigDecimal(100))
                .account(1L)
                .creationDate(OffsetDateTime.of(2022, 7, 9, 22, 10, 30, 10, ZoneOffset.of("Z")))
                .isDefault(true)
                .overdraftEnabled(true)
                .build();
    }
}