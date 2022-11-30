package com.example.transfer.controller.dto;

import com.example.transfer.entity.Transfer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferMapper {

    private final ModelMapper mapper;

    @Autowired
    public TransferMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public Transfer mapFromRequest(TransferRequestDTO transferRequestDTO) {
        return mapper.map(transferRequestDTO, Transfer.class);
    }

    public TransferResponseDTO mapToResponse(Transfer transfer) {
        return mapper.map(transfer, TransferResponseDTO.class);
    }

}
