package org.example.bill.controller.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BillRequestDTO {
    @NotNull(message = "amount should be not null")
    private BigDecimal amount;

    private OffsetDateTime creationDate;

    private boolean overdraftEnabled;

    @NotNull(message = "account should be not null")
    private Long account;
}
