package org.example.account.controller.dto;

import org.example.account.entity.Account;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountMapper {

    private final ModelMapper mapper;

    @Autowired
    public AccountMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public Account mapFromRequest(AccountRequestDTO accountRequestDTO) {
        return mapper.map(accountRequestDTO, Account.class);
    }

    public AccountResponseDTO mapToResponse(Account account) {
        return mapper.map(account, AccountResponseDTO.class);
    }

}
