package com.example.transfer.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BillResponseDTO {
    private Long billId;
    private BigDecimal amount;
    private boolean isDefault;
    private OffsetDateTime creationDate;
    private boolean overdraftEnabled;
    private Long account;
}
