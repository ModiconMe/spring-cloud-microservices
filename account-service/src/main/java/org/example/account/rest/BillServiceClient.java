package org.example.account.rest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "bill-service")
public interface BillServiceClient {

    @RequestMapping(value = "bills/account/{accountId}", method = RequestMethod.DELETE)
    List<BillResponseDTO> deleteBillByAccountId(@PathVariable("accountId") Long accountId);

}
