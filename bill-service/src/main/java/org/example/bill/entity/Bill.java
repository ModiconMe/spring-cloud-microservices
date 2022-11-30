package org.example.bill.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity(name = "Bill")
@Table(name = "bill")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
@AllArgsConstructor
public class Bill {

    @Id
    @SequenceGenerator(name = "bill_sequence", sequenceName = "bill_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bill_sequence")
    @Column(name = "id", updatable = false)
    private Long billId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @Column(name = "creationDate", nullable = false)
    private OffsetDateTime creationDate;

    @Column(name = "overdraft_enabled", nullable = false)
    private boolean overdraftEnabled;

    @Column(name = "account_id", nullable = false)
    private Long account;

}
