package org.example.bill.controller.dto;

import org.example.bill.entity.Bill;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * �������������� �������� -> ��������, �������� -> ������.
 */
@Service
public class BillMapper {

    private final ModelMapper mapper;

    @Autowired
    public BillMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public Bill mapFromRequest(BillRequestDTO billRequestDTO) {
        return mapper.map(billRequestDTO, Bill.class);
    }

    public BillResponseDTO mapToResponse(Bill bill) {
        return mapper.map(bill, BillResponseDTO.class);
    }

}
