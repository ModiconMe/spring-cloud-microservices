package org.example.account.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Account")
@Table(
        name = "account",
        uniqueConstraints = {
                @UniqueConstraint(name = "account_email_unique", columnNames = "email"),
                @UniqueConstraint(name = "account_phone_unique", columnNames = "phone")
        }
)
@Getter
@Setter
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class Account {

    @Id
    @SequenceGenerator(name = "account_sequence", sequenceName = "account_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_sequence")
    @Column(name = "id", updatable = false)
    private Long accountId;

    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(name = "email", nullable = false, columnDefinition = "TEXT")
    private String email;

    @Column(name = "phone", nullable = false, columnDefinition = "TEXT")
    private String phone;

    @Column(name = "date_of_birth", nullable = false, columnDefinition = "DATE")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateOfBirth;

    @Column(name = "creationDate", nullable = false, columnDefinition = "DATE")
    private OffsetDateTime creationDate;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Long> bills;

    public void addBill(Long billId) {
        if (bills == null)
            bills = new ArrayList<>();
        bills.add(billId);
    }

    public void removeBill(Long billId) {
        if (bills == null)
            bills = new ArrayList<>();
        bills.remove(billId);
    }
}
