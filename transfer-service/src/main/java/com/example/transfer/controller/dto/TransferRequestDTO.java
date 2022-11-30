package com.example.transfer.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class TransferRequestDTO {

    @NotNull(message = "sender account id should be not null")
    private Long fromAccountId;

    @NotNull(message = "sender bill id should be not null")
    private Long fromBillId;

    @NotNull(message = "obtain account id should be not null")
    private Long toAccountId;

    private Long toBillId;

    @NotNull(message = "amount should be not null")
    @Positive(message = "amount should be positive")
    private BigDecimal amount;

}
