package org.example.account.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.account.entity.Account;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class AccountResponseDTO {
    private Long accountId;
    private String name;
    private String email;
    private String phone;
    private OffsetDateTime creationDate;
    private List<Long> bills;

    public AccountResponseDTO(Account account) {
        accountId = account.getAccountId();
        name = account.getName();
        email = account.getEmail();
        phone = account.getPhone();
        creationDate = account.getCreationDate();
        bills = account.getBills();
    }
}
