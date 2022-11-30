package org.example.account.rest;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Builder
public class BillRequestDTO {
    private BigDecimal amount;
    private OffsetDateTime creationDate;
    private boolean overdraftEnabled;
    private Long account;
}
