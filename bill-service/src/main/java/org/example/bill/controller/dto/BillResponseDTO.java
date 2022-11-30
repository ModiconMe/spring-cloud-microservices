package org.example.bill.controller.dto;

import lombok.*;
import org.example.bill.entity.Bill;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BillResponseDTO {
    private Long billId;
    private BigDecimal amount;
    private boolean isDefault;
    private OffsetDateTime creationDate;
    private boolean overdraftEnabled;
    private Long account;
}
