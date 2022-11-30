package org.example.account.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.example.account.utils.validation.Phone;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class AccountRequestDTO {
    @NotEmpty(message = "name should be not empty")
    @Size(min = 2, max = 100, message = "Name should be between 2 and 100 character")
    private String name;

    @NotEmpty(message = "email should be not empty")
    @Email
    private String email;

    @NotEmpty(message = "phone number should be not empty")
    @Phone
    private String phone;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private List<Long> bills;

    public void setBills(List<Long> bills) {
        this.bills = bills;
    }
}
