package org.example.deposit.rest;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Builder
public class BillRequestDTO {
    private BigDecimal amount;
    private boolean isDefault;
    private OffsetDateTime creationDate;
    private boolean overdraftEnabled;
    private Long account;
}
