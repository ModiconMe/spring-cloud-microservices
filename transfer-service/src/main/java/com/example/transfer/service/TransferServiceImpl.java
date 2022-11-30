package com.example.transfer.service;

import com.example.transfer.controller.dto.TransferRequestDTO;
import com.example.transfer.controller.dto.TransferResponseDTO;
import com.example.transfer.entity.Transfer;
import com.example.transfer.exception.TransferServiceException;
import com.example.transfer.repository.TransferRepository;
import com.example.transfer.rest.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
public class TransferServiceImpl implements TransferService {

    private static final String TOPIC_EXCHANGE_DEPOSIT = "js.deposit.notify.exchange";
    private static final String ROUTING_KEY_DEPOSIT = "js.key.deposit";

    private final TransferRepository transferRepository;

    private final AccountServiceClient accountServiceClient;

    private final BillServiceClient billServiceClient;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public TransferServiceImpl(
            TransferRepository transferRepository,
            AccountServiceClient accountServiceClient,
            BillServiceClient billServiceClient,
            RabbitTemplate rabbitTemplate
    ) {
        this.transferRepository = transferRepository;
        this.accountServiceClient = accountServiceClient;
        this.billServiceClient = billServiceClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Make transfer between two accounts.
     *
     * @param transfer transfer request info
     * @return transfer response
     */
    public TransferResponseDTO transfer(Transfer transfer) {

        Long toAccountId = transfer.getToAccountId();
        Long toBillId = transfer.getToBillId();
        Long fromAccountId = transfer.getFromAccountId();
        Long fromBillId = transfer.getFromBillId();

        if (toAccountId == null && toBillId == null) {
            throw new TransferServiceException("Obtain account is null and bill is null");
        }

        BigDecimal amount = transfer.getAmount();

        // send request to account and bill service
        AccountResponseDTO fromAccount = accountServiceClient.getAccountById(fromAccountId);
        BillResponseDTO fromBill = billServiceClient.getBillById(fromBillId);

        // if bill id is not null than send transfer to that bill
        if (toBillId != null) {
            transferMoneyBetweenBills(
                    toBillId,
                    amount,
                    fromBill
            );

            transferRepository.save(new Transfer(
                    amount,
                    fromBillId,
                    fromAccountId,
                    toBillId,
                    toAccountId,
                    OffsetDateTime.now())
            );
            return createResponse(fromBill.getBillId(), toBillId, amount);
        }

        // else if bill id is null than send to account default bill
        BillResponseDTO defaultBill = getDefaultBill(toAccountId);
        transferMoneyBetweenBills(
                defaultBill.getBillId(),
                amount,
                fromBill
        );

        transferRepository.save(new Transfer(
                amount,
                defaultBill.getBillId(),
                fromAccountId,
                toBillId,
                toAccountId,
                OffsetDateTime.now())
        );
        return createResponse(defaultBill.getBillId(), toBillId, amount);
    }

    private void transferMoneyBetweenBills(Long toBillId, BigDecimal amount, BillResponseDTO fromBill) {
        // take money from
        if (fromBill.getAmount().compareTo(amount) < 0)
            throw new TransferServiceException("Bill with id " + fromBill.getBillId() + " have not enough money");

        BillRequestDTO fromBillRequestDTO = createBillRequestDTO(amount.negate(), fromBill);
        billServiceClient.update(fromBill.getBillId(), fromBillRequestDTO);

        // send money to
        BillResponseDTO toBillResponseDTO = billServiceClient.getBillById(toBillId);
        BillRequestDTO toBillRequestDTO = createBillRequestDTO(amount, toBillResponseDTO);
        billServiceClient.update(toBillId, toBillRequestDTO);
    }

    /**
     * Create message that send to RabbitMQ.
     * @param fromBill bill from
     * @param toBill bill to
     * @param amount sum of transfer
     * @return transfer
     */
    private TransferResponseDTO createResponse(Long fromBill, Long toBill, BigDecimal amount) {
        TransferResponseDTO transferResponseDTO = new TransferResponseDTO(fromBill, toBill, amount);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            rabbitTemplate.convertAndSend(TOPIC_EXCHANGE_DEPOSIT, ROUTING_KEY_DEPOSIT, objectMapper.writeValueAsString(transferResponseDTO));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new TransferServiceException("Cant send message to RabbitMQ");
        }

        return transferResponseDTO;
    }

    /**
     * Create bill request for sending to bill-service and change bill balance.
     * @param amount sum of transfer
     * @param billResponseDTO bill balance to change
     * @return updated bill
     */
    private static BillRequestDTO createBillRequestDTO(BigDecimal amount, BillResponseDTO billResponseDTO) {
        return BillRequestDTO.builder()
                .account(billResponseDTO.getAccount())
                .isDefault(billResponseDTO.isDefault())
                .creationDate(billResponseDTO.getCreationDate())
                .overdraftEnabled(billResponseDTO.isOverdraftEnabled())
                .amount(billResponseDTO.getAmount().add(amount))
                .build();
    }

    /**
     * Get default bill for account.
     * @param accountId account id
     * @return bill
     */
    private BillResponseDTO getDefaultBill(Long accountId) {
        return billServiceClient.getBillsByAccountId(accountId)
                .stream()
                .filter(BillResponseDTO::isDefault)
                .findAny()
                .orElseThrow(() -> new TransferServiceException("Unable to find default bill for account with id " + accountId));
    }
}
