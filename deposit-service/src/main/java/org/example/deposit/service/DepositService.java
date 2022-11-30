package org.example.deposit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.deposit.controller.dto.DepositResponseDTO;
import org.example.deposit.entity.Deposit;
import org.example.deposit.exception.DepositServiceException;
import org.example.deposit.repository.DepositRepository;
import org.example.deposit.rest.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
public class DepositService {

    private static final String TOPIC_EXCHANGE_DEPOSIT = "js.deposit.notify.exchange";
    private static final String ROUTING_KEY_DEPOSIT = "js.key.deposit";

    private final DepositRepository depositRepository;

    private final AccountServiceClient accountServiceClient;

    private final BillServiceClient billServiceClient;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public DepositService(
            DepositRepository depositRepository,
            AccountServiceClient accountServiceClient,
            BillServiceClient billServiceClient,
            RabbitTemplate rabbitTemplate
    ) {
        this.depositRepository = depositRepository;
        this.accountServiceClient = accountServiceClient;
        this.billServiceClient = billServiceClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    public DepositResponseDTO deposit(Long accountId, Long billId, BigDecimal amount) {
        if (accountId == null && billId == null)
            throw new DepositServiceException("Account is null and bill is null");

        if (billId != null) {
            BillResponseDTO billResponseDTO = billServiceClient.getBillById(billId);
            BillRequestDTO billRequestDTO = createBillRequestDTO(amount, billResponseDTO);
            billServiceClient.update(billId, billRequestDTO);
            AccountResponseDTO accountResponseDTO = accountServiceClient.getAccountById(billResponseDTO.getAccount());
            depositRepository.save(new Deposit(amount, billId, OffsetDateTime.now(), accountResponseDTO.getEmail()));
            return createResponse(amount, accountResponseDTO);
        }

        BillResponseDTO defaultBill = getDefaultBill(accountId);
        BillRequestDTO billRequestDTO = createBillRequestDTO(amount, defaultBill);
        billServiceClient.update(defaultBill.getBillId(), billRequestDTO);
        AccountResponseDTO account = accountServiceClient.getAccountById(accountId);
        depositRepository.save(new Deposit(amount, defaultBill.getBillId(), OffsetDateTime.now(), account.getEmail()));
        return createResponse(amount, account);
    }

    private DepositResponseDTO createResponse(BigDecimal amount, AccountResponseDTO accountResponseDTO) {
        DepositResponseDTO depositResponseDTO = new DepositResponseDTO(amount, accountResponseDTO.getEmail());

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            rabbitTemplate.convertAndSend(TOPIC_EXCHANGE_DEPOSIT, ROUTING_KEY_DEPOSIT, objectMapper.writeValueAsString(depositResponseDTO));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new DepositServiceException("Cant send message to RabbitMQ");
        }

        return depositResponseDTO;
    }

    private static BillRequestDTO createBillRequestDTO(BigDecimal amount, BillResponseDTO billResponseDTO) {
        return BillRequestDTO.builder()
                .account(billResponseDTO.getAccount())
                .isDefault(billResponseDTO.isDefault())
                .creationDate(billResponseDTO.getCreationDate())
                .overdraftEnabled(billResponseDTO.isOverdraftEnabled())
                .amount(billResponseDTO.getAmount().add(amount))
                .build();
    }

    private BillResponseDTO getDefaultBill(Long accountId) {
        return billServiceClient.getBillsByAccountId(accountId)
                .stream()
                .filter(BillResponseDTO::isDefault)
                .findAny()
                .orElseThrow(() -> new DepositServiceException("Unable to find default bill for account with id " + accountId));
    }
}
