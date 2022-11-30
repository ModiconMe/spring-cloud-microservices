package org.example.deposit.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class DepositRequestDTO {

    @NotNull(message = "account id should be not null")
    private Long accountId;

    @NotNull(message = "bill id should be not null")
    private Long billId;

    @NotNull(message = "amount should be not null")
    @Positive(message = "amount should be positive")
    private BigDecimal amount;
}
