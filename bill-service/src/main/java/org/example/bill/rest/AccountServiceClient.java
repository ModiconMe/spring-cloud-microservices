package org.example.bill.rest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "account-service")
@Service
public interface AccountServiceClient {

    @RequestMapping(value = "/accounts/add-bill/{accountId}/{billId}", method = RequestMethod.PUT)
    void addBillToAccount(@PathVariable("accountId") Long accountId, @PathVariable("billId") Long billId);

    @RequestMapping(value = "remove-bill/{accountId}/{billId}", method = RequestMethod.PUT)
    void removeBillFromAccount(@PathVariable("accountId") Long accountId, @PathVariable("billId") Long billId);
}
