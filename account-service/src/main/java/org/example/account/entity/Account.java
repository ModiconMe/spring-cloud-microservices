package org.example.account.entity;

import lombok.*;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;

@Entity(name = "Account")
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor
@ToString
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

    @Column(name = "creationDate", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private OffsetDateTime creationDate;

//    @OneToMany(
//            mappedBy = "account",
//            orphanRemoval = true,
//            fetch = FetchType.EAGER,
//            cascade = {
//                    CascadeType.PERSIST,
//                    CascadeType.REMOVE
//            }
//    )
//    private List<Bill> bills;

    @ElementCollection
    private List<Long> bills;

    public Account(String name, String email, String phone, OffsetDateTime creationDate, List<Long> bills) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.creationDate = creationDate;
        this.bills = bills;
    }
}
