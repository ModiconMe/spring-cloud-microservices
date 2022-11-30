package org.example.bill.controller;

import org.example.bill.controller.dto.BillMapper;
import org.example.bill.controller.dto.BillRequestDTO;
import org.example.bill.controller.dto.BillResponseDTO;
import org.example.bill.service.BillService;
import org.example.bill.service.BillServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/bills")
public class BillController {

    private final BillService billService;
    private final BillMapper mapper;

    @Autowired
    public BillController(BillService billService, BillMapper mapper) {
        this.billService = billService;
        this.mapper = mapper;
    }

    /**
     * CREATE bill.
     *
     * @param billRequestDTO request for creating bill
     * @return bill id
     */
    @PostMapping("/")
    public Long createBill(@RequestBody @Valid BillRequestDTO billRequestDTO) {
        return billService.createBill(mapper.mapFromRequest(billRequestDTO));
    }

    /**
     * READ bill by id.
     *
     * @param billId bill id
     * @return bill
     */
    @GetMapping("/{billId}")
    public BillResponseDTO getBill(@PathVariable("billId") Long billId) {
        return mapper.mapToResponse(billService.getBillById(billId));
    }

    /**
     * UPDATE bill by id.
     *
     * @param billId         bill id
     * @param billRequestDTO request for update bill
     * @return dto updated bill
     */
    @PutMapping("/{billId}")
    public BillResponseDTO updateBill(
            @PathVariable("billId") Long billId,
            @RequestBody @Valid BillRequestDTO billRequestDTO
    ) {
        return mapper.mapToResponse(billService.updateBill(
                billId,
                mapper.mapFromRequest(billRequestDTO)
        ));
    }

    /**
     * DELETE bill by id.
     *
     * @param billId bill id
     * @return dto deleted bill
     */
    @DeleteMapping("/{billId}")
    public BillResponseDTO deleteBill(@PathVariable("billId") Long billId) {
        return mapper.mapToResponse(billService.deleteBill(billId));
    }

    /**
     * Delete all accounts bill by request from account-service when account is deleted.
     *
     * @param accountId account id
     * @return list of deleted account bills
     */
    @DeleteMapping("/account/{accountId}")
    public List<BillResponseDTO> deleteBillsByAccountId(@PathVariable("accountId") Long accountId) {
        return billService.deleteBillsByAccountId(accountId)
                .stream()
                .map(mapper::mapToResponse)
                .toList();
    }

    /**
     * Mark bill as accounts default.
     *
     * @param billId bill id
     * @return dto updated bill
     */
    @PutMapping("/default/{billId}")
    public BillResponseDTO makeBillDefault(@PathVariable("billId") Long billId) {
        return mapper.mapToResponse(billService.makeBillDefault(billId));
    }

    /**
     * Obtain accounts bills by account id.
     *
     * @param accountId account id
     * @return list of accounts bills
     */
    @GetMapping("/account/{accountId}")
    public List<BillResponseDTO> getBillsByAccountId(@PathVariable("accountId") Long accountId) {
        return billService.getBillsByAccountId(accountId)
                .stream()
                .map(mapper::mapToResponse)
                .toList();
    }

}
