package com.example.transfer.controller;

import com.example.transfer.TransferApplication;
import com.example.transfer.config.SpringBootH2TestConfig;
import com.example.transfer.controller.dto.TransferResponseDTO;
import com.example.transfer.entity.Transfer;
import com.example.transfer.repository.TransferRepository;
import com.example.transfer.rest.AccountResponseDTO;
import com.example.transfer.rest.AccountServiceClient;
import com.example.transfer.rest.BillResponseDTO;
import com.example.transfer.rest.BillServiceClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = {TransferApplication.class, SpringBootH2TestConfig.class}
)
@ExtendWith(MockitoExtension.class)
class TransferControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private TransferRepository transferRepository;

    @MockBean
    private BillServiceClient billServiceClient;

    @MockBean
    private AccountServiceClient accountServiceClient;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void itShouldTransfer() throws Exception {
        // given
        AccountResponseDTO fromAccount = getAccountResponseDTO(1L, "dmitry", "dmitry@gmail.com", "+79520009939", List.of(1L, 2L));
        AccountResponseDTO toAccount = getAccountResponseDTO(2L, "dmitry1", "dmitry1@gmail.com", "+79520009938", List.of(3L, 4L));
        BillResponseDTO fromBill1 = getBillResponseDTO(1L, 1L, 100, true, false);
        BillResponseDTO fromBill2 = getBillResponseDTO(2L, 1L, 200, false, false);
        BillResponseDTO toBill1 = getBillResponseDTO(3L, 2L, 150, true, false);
        BillResponseDTO toBill2 = getBillResponseDTO(4L, 2L, 50, false, false);

        Transfer transfer = Transfer.builder()
                .fromAccountId(fromAccount.getAccountId())
                .fromBillId(fromBill1.getBillId())
                .toAccountId(toAccount.getAccountId())
                .toBillId(toBill1.getBillId())
                .amount(BigDecimal.valueOf(100))
                .build();

        when(accountServiceClient.getAccountById(transfer.getFromAccountId())).thenReturn(fromAccount);
        when(billServiceClient.getBillById(transfer.getFromBillId())).thenReturn(fromBill1);
        when(billServiceClient.getBillById(transfer.getToBillId())).thenReturn(toBill1);

        // when
        ResultActions perform = mockMvc.perform(post("/transfers")
                .content(Objects.requireNonNull(objectToJson(transfer)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        MvcResult mvcResult = perform.andExpect(status().isOk()).andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        TransferResponseDTO transferResponseDTO = jsonToObject(body);
        Optional<Transfer> expected = transferRepository.findById(1L);
        assertThat(expected.isPresent()).isTrue();
        assertThat(expected.get().getAmount()).isEqualTo(new BigDecimal("100.00"));
    }

    @Test
    void itShouldNotTransfer_whenRequestIsNotValid() throws Exception {
        // given
        AccountResponseDTO fromAccount = getAccountResponseDTO(1L, "dmitry", "dmitry@gmail.com", "+79520009939", List.of(1L, 2L));
        AccountResponseDTO toAccount = getAccountResponseDTO(2L, "dmitry1", "dmitry1@gmail.com", "+79520009938", List.of(3L, 4L));
        BillResponseDTO fromBill1 = getBillResponseDTO(1L, 1L, 100, true, false);
        BillResponseDTO fromBill2 = getBillResponseDTO(2L, 1L, 200, false, false);
        BillResponseDTO toBill1 = getBillResponseDTO(3L, 2L, 150, true, false);
        BillResponseDTO toBill2 = getBillResponseDTO(4L, 2L, 50, false, false);

        Transfer transfer = Transfer.builder()
                .fromAccountId(fromAccount.getAccountId())
                .fromBillId(fromBill1.getBillId())
                .toAccountId(null)
                .toBillId(null)
                .amount(BigDecimal.valueOf(100))
                .build();
        // when
        ResultActions perform = mockMvc.perform(post("/transfers")
                .content(Objects.requireNonNull(objectToJson(transfer)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        perform.andExpect(status().isBadRequest()).andReturn();
    }


    private static BillResponseDTO getBillResponseDTO(long billId, long accountId, int amount, boolean isDefault, boolean overdraftEnabled) {
        return BillResponseDTO.builder()
                .billId(billId)
                .amount(new BigDecimal(amount))
                .isDefault(isDefault)
                .overdraftEnabled(overdraftEnabled)
                .account(accountId)
                .build();
    }

    private static AccountResponseDTO getAccountResponseDTO(long accountId, String name, String email, String phone, List<Long> bills) {
        return AccountResponseDTO.builder()
                .accountId(accountId)
                .name(name)
                .email(email)
                .phone(phone)
                .bills(bills)
                .build();
    }

    private String objectToJson(Object object) {
        try {
            ObjectMapper mapper1 = new ObjectMapper();
            mapper1.registerModule(new JavaTimeModule());
            return mapper1.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private TransferResponseDTO jsonToObject(String json) {
        try {
            ObjectMapper mapper1 = new ObjectMapper();
            mapper1.registerModule(new JavaTimeModule());
            return mapper1.readValue(json, TransferResponseDTO.class);
        } catch (JsonProcessingException e) {
            fail("Failed to convert json to object");
            return null;
        }
    }
}