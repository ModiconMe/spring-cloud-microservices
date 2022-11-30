package org.example.deposit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity(name = "Deposit")
@Table(name = "deposit")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Deposit {

    @Id
    @SequenceGenerator(name = "deposit_sequence", sequenceName = "deposit_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "deposit_sequence")
    @Column(name = "id", updatable = false)
    private Long depositId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "bill_id", nullable = false)
    private Long billId;

    @Column(name = "creation_date", nullable = false)
    private OffsetDateTime creationDate;

    @Column(name = "email", nullable = false)
    private String email;

    public Deposit(BigDecimal amount, Long billId, OffsetDateTime creationDate, String email) {
        this.amount = amount;
        this.billId = billId;
        this.creationDate = creationDate;
        this.email = email;
    }
}
