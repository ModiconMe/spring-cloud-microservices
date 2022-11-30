package com.example.transfer.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity(name = "Transfer")
@Table(name = "transfer")
@Getter
@Setter
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class Transfer {

    @Id
    @SequenceGenerator(name = "transfer_sequence", sequenceName = "transfer_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transfer_sequence")
    @Column(name = "id", updatable = false)
    private Long transferId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "from_bill_id", nullable = false)
    private Long fromBillId;

    @Column(name = "from_account_id", nullable = false)
    private Long fromAccountId;

    @Column(name = "to_bill_id", nullable = false)
    private Long toBillId;

    @Column(name = "to_account_id", nullable = false)
    private Long toAccountId;

    @Column(name = "creation_date", nullable = false)
    private OffsetDateTime creationDate;

    public Transfer(BigDecimal amount, Long fromBillId, Long fromAccountId, Long toBillId, Long toAccountId, OffsetDateTime creationDate) {
        this.amount = amount;
        this.fromBillId = fromBillId;
        this.fromAccountId = fromAccountId;
        this.toBillId = toBillId;
        this.toAccountId = toAccountId;
        this.creationDate = creationDate;
    }

}
