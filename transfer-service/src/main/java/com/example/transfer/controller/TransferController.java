package com.example.transfer.controller;

import com.example.transfer.controller.dto.TransferMapper;
import com.example.transfer.controller.dto.TransferRequestDTO;
import com.example.transfer.controller.dto.TransferResponseDTO;
import com.example.transfer.service.TransferServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController()
public class TransferController {

    private final TransferServiceImpl transferService;
    private final TransferMapper mapper;

    @Autowired
    public TransferController(TransferServiceImpl transferService, TransferMapper mapper) {
        this.transferService = transferService;
        this.mapper = mapper;
    }

    @PostMapping("/transfers")
    public TransferResponseDTO transfer(@RequestBody @Valid TransferRequestDTO transferRequestDTO) {
        return transferService.transfer(mapper.mapFromRequest(transferRequestDTO));
    }

}
