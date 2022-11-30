package com.example.transfer.service;

import com.example.transfer.controller.dto.TransferRequestDTO;
import com.example.transfer.controller.dto.TransferResponseDTO;
import com.example.transfer.entity.Transfer;

public interface TransferService {

    TransferResponseDTO transfer(Transfer transfer);

}
