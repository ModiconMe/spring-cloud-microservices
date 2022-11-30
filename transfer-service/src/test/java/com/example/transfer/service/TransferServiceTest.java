package com.example.transfer.service;

import com.example.transfer.controller.dto.TransferRequestDTO;
import com.example.transfer.controller.dto.TransferResponseDTO;
import com.example.transfer.entity.Transfer;
import com.example.transfer.repository.TransferRepository;
import com.example.transfer.rest.AccountResponseDTO;
import com.example.transfer.rest.AccountServiceClient;
import com.example.transfer.rest.BillResponseDTO;
import com.example.transfer.rest.BillServiceClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @InjectMocks
    private TransferServiceImpl transferService;

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private AccountServiceClient accountServiceClient;

    @Mock
    private BillServiceClient billServiceClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Test
    void itShouldCreateTransferWithToBill() {
        // given
        Transfer transfer = Transfer.builder()
                .transferId(1L)
                .toBillId(3L)
                .toAccountId(null)
                .fromBillId(1L)
                .fromAccountId(1L)
                .amount(new BigDecimal(50))
                .build();

        AccountResponseDTO fromAccount = getAccountResponseDTO(1L, "dmitry", "dmitry@gmail.com", "+79520009939", List.of(1L, 2L));
        BillResponseDTO fromBill1 = getBuild(1L, 1L, 100, true, true);
        BillResponseDTO fromBill2 = getBuild(2L, 1L, 1000, false, false);

        AccountResponseDTO toAccount = getAccountResponseDTO(2L, "dmitry1", "dmitry1@gmail.com", "+79520009938", List.of(3L));
        BillResponseDTO toBill = getBuild(3L, 2L, 2000, true, false);

        when(accountServiceClient.getAccountById(transfer.getFromAccountId())).thenReturn(fromAccount);
        when(billServiceClient.getBillById(transfer.getFromBillId())).thenReturn(fromBill1);
        when(billServiceClient.getBillById(transfer.getToBillId())).thenReturn(toBill);

        // when
        TransferResponseDTO expected = transferService.transfer(transfer);

        // then
        verify(billServiceClient, times(2)).update(anyLong(), any());
        verify(transferRepository, times(1)).save(any());
        assertThat(expected.getAmount()).isEqualTo(BigDecimal.valueOf(50));
    }

    @Test
    void itShouldCreateTransferWithToAccount() {
        // given
        Transfer transfer = Transfer.builder()
                .transferId(1L)
                .toBillId(null)
                .toAccountId(2L)
                .fromBillId(1L)
                .fromAccountId(1L)
                .amount(new BigDecimal(50))
                .build();

        AccountResponseDTO fromAccount = getAccountResponseDTO(1L, "dmitry", "dmitry@gmail.com", "+79520009939", List.of(1L, 2L));
        BillResponseDTO fromBill1 = getBuild(1L, 1L, 100, true, true);
        BillResponseDTO fromBill2 = getBuild(2L, 1L, 1000, false, false);

        AccountResponseDTO toAccount = getAccountResponseDTO(2L, "dmitry1", "dmitry1@gmail.com", "+79520009938", List.of(3L));
        BillResponseDTO toBill = getBuild(3L, 2L, 2000, true, false);
        List<BillResponseDTO> bills = new ArrayList<>();
        bills.add(toBill);

        when(accountServiceClient.getAccountById(transfer.getFromAccountId())).thenReturn(fromAccount);
        when(billServiceClient.getBillById(transfer.getFromBillId())).thenReturn(fromBill1);
        when(billServiceClient.getBillsByAccountId(transfer.getToAccountId())).thenReturn(bills);
        when(billServiceClient.getBillById(toBill.getBillId())).thenReturn(toBill);

        // when
        TransferResponseDTO expected = transferService.transfer(transfer);

        // then
        verify(billServiceClient, times(2)).update(anyLong(), any());
        verify(transferRepository, times(1)).save(any());
        assertThat(expected.getAmount()).isEqualTo(BigDecimal.valueOf(50));
    }

    private static BillResponseDTO getBuild(long billId, long billId1, int val, boolean isDefault, boolean isDefault1) {
        return BillResponseDTO.builder()
                .billId(billId)
                .amount(new BigDecimal(val))
                .isDefault(isDefault)
                .overdraftEnabled(isDefault1)
                .account(billId1)
                .build();
    }

    private static AccountResponseDTO getAccountResponseDTO(long accountId, String dmitry, String email, String phone, List<Long> bills) {
        AccountResponseDTO fromAccount = AccountResponseDTO.builder()
                .accountId(accountId)
                .name(dmitry)
                .email(email)
                .phone(phone)
                .bills(bills)
                .build();
        return fromAccount;
    }
}