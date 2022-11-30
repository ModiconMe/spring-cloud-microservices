package org.example.bill.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class AccountRequestDTO {
    private String name;
    private String email;
    private String phone;
    private List<Long> bills;
}
