package org.example.deposit.rest;

import lombok.*;

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
