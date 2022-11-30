package org.example.deposit.service;

import org.example.deposit.controller.dto.DepositResponseDTO;
import org.example.deposit.exception.DepositServiceException;
import org.example.deposit.repository.DepositRepository;
import org.example.deposit.rest.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class DepositServiceTest {

    @InjectMocks
    private DepositService depositService;

    @Mock
    private DepositRepository depositRepository;

    @Mock
    private AccountServiceClient accountServiceClient;

    @Mock
    private BillServiceClient billServiceClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Test
    void itShouldCreateDepositWhenBillId() {
        // given
        BillResponseDTO billResponseDTO = createBillResponseDTO();
        when(billServiceClient.getBillById(anyLong())).thenReturn(billResponseDTO);


        AccountResponseDTO accountResponseDTO = createAccountResponseDTO();
        when(accountServiceClient.getAccountById(anyLong())).thenReturn(accountResponseDTO);

        // when
        BigDecimal amount = BigDecimal.valueOf(100);
        Long billId = 1L;
        Long accountId = null;
        String email = "dmitry@gmail.com";
        DepositResponseDTO deposit = depositService.deposit(accountId, billId, amount);

        // then
        verify(billServiceClient, times(1)).update(anyLong(), any());
        verify(depositRepository, times(1)).save(any());
        assertThat(deposit.getAmount()).isEqualTo(amount);
        assertThat(deposit.getEmail()).isEqualTo(email);
    }

    @ParameterizedTest
    @MethodSource(value = "createSeveralValidAccountResponseDTO")
    void itShouldCreateDeposit_WhenAccountId(AccountResponseDTO accountResponseDTO, List<BillResponseDTO> billResponseDTOS) {
        // given
        when(billServiceClient.getBillsByAccountId(anyLong())).thenReturn(billResponseDTOS);

        when(accountServiceClient.getAccountById(anyLong())).thenReturn(accountResponseDTO);

        // when
        BigDecimal amount = BigDecimal.valueOf(100);
        Long billId = null;
        Long accountId = 1L;
        String email = accountResponseDTO.getEmail();
        DepositResponseDTO deposit = depositService.deposit(accountId, billId, amount);

        // then
        verify(billServiceClient, times(1)).update(anyLong(), any());
        verify(depositRepository, times(1)).save(any());
        assertThat(deposit.getAmount()).isEqualTo(amount);
        assertThat(deposit.getEmail()).isEqualTo(email);
    }

    @ParameterizedTest
    @MethodSource(value = "createSeveralNonValidAccountResponseDTO")
    void itShouldNotCreateDeposit_WhenAccountIsNotValid(AccountResponseDTO accountResponseDTO, List<BillResponseDTO> billResponseDTOS) {
        // given
        when(billServiceClient.getBillsByAccountId(anyLong())).thenReturn(billResponseDTOS);

        // when
        BigDecimal amount = BigDecimal.valueOf(100);
        Long billId = null;
        Long accountId = 1L;

        // then
        assertThatThrownBy(() -> depositService.deposit(accountId, billId, amount))
                .isInstanceOf(DepositServiceException.class)
                .hasMessageContaining("Unable to find default bill for account with id " + accountId);
        verify(billServiceClient, times(0)).update(anyLong(), any());
        verify(depositRepository, times(0)).save(any());
    }

    private AccountResponseDTO createAccountResponseDTO() {
        return AccountResponseDTO.builder()
                .accountId(1L)
                .name("dmitry")
                .email("dmitry@gmail.com")
                .phone("+79520009939")
                .creationDate(OffsetDateTime.now())
                .bills(List.of(1L))
                .build();
    }

    private BillResponseDTO createBillResponseDTO() {
        return BillResponseDTO.builder()
                .account(1L)
                .amount(BigDecimal.valueOf(1000))
                .billId(1L)
                .creationDate(OffsetDateTime.now())
                .isDefault(true)
                .overdraftEnabled(true)
                .build();
    }

    private static Stream<Arguments> createSeveralValidAccountResponseDTO() {
        return Stream.of(
                Arguments.of(
                        AccountResponseDTO.builder()
                                .accountId(1L)
                                .name("dmitry")
                                .email("dmitry@gmail.com")
                                .phone("+79520009939")
                                .creationDate(OffsetDateTime.now())
                                .bills(List.of(1L))
                                .build(),
                        List.of(BillResponseDTO.builder()
                                .account(1L)
                                .amount(BigDecimal.valueOf(1000))
                                .billId(1L)
                                .creationDate(OffsetDateTime.now())
                                .isDefault(true)
                                .overdraftEnabled(true)
                                .build())),
                Arguments.of(
                        AccountResponseDTO.builder()
                                .accountId(2L)
                                .name("dmitry1")
                                .email("dmitry1@gmail.com")
                                .phone("+79520009938")
                                .creationDate(OffsetDateTime.now())
                                .bills(List.of(2L, 3L))
                                .build(),
                        List.of(BillResponseDTO.builder()
                                        .account(2L)
                                        .amount(BigDecimal.valueOf(1000))
                                        .billId(2L)
                                        .creationDate(OffsetDateTime.now())
                                        .isDefault(true)
                                        .overdraftEnabled(true)
                                        .build(),
                                BillResponseDTO.builder()
                                        .account(2L)
                                        .amount(BigDecimal.valueOf(1000))
                                        .billId(3L)
                                        .creationDate(OffsetDateTime.now())
                                        .isDefault(false)
                                        .overdraftEnabled(true)
                                        .build())));
    }

    private static Stream<Arguments> createSeveralNonValidAccountResponseDTO() {
        return Stream.of(
                Arguments.of(
                        AccountResponseDTO.builder()
                                .accountId(1L)
                                .name("dmitry")
                                .email("dmitry@gmail.com")
                                .phone("+79520009939")
                                .creationDate(OffsetDateTime.now())
                                .bills(List.of(1L))
                                .build(),
                        List.of(BillResponseDTO.builder()
                                .account(1L)
                                .amount(BigDecimal.valueOf(1000))
                                .billId(1L)
                                .creationDate(OffsetDateTime.now())
                                .isDefault(false)
                                .overdraftEnabled(true)
                                .build())));
    }

}