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
    @Temporal(TemporalType.TIMESTAMP)
    private OffsetDateTime creationDate;

    @Column(name = "overdraft_enabled", nullable = false)
    private boolean overdraftEnabled;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "bill_account_id_fk"), nullable = false)
    private Account account;

    public Bill(BigDecimal amount, boolean isDefault, OffsetDateTime creationDate, boolean overdraftEnabled, Account account) {
        this.amount = amount;
        this.isDefault = isDefault;
        this.creationDate = creationDate;
        this.overdraftEnabled = overdraftEnabled;
        this.account = account;
    }

    public Bill(BigDecimal amount, boolean isDefault, boolean overdraftEnabled, Account account) {
        this.amount = amount;
        this.isDefault = isDefault;
        this.overdraftEnabled = overdraftEnabled;
        this.account = account;
    }
}
