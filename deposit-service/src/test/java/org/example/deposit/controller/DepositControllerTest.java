package org.example.deposit.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.deposit.DepositApplication;
import org.example.deposit.config.SpringBootH2TestConfig;
import org.example.deposit.controller.dto.DepositRequestDTO;
import org.example.deposit.controller.dto.DepositResponseDTO;
import org.example.deposit.entity.Deposit;
import org.example.deposit.repository.DepositRepository;
import org.example.deposit.rest.AccountResponseDTO;
import org.example.deposit.rest.AccountServiceClient;
import org.example.deposit.rest.BillResponseDTO;
import org.example.deposit.rest.BillServiceClient;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = {DepositApplication.class, SpringBootH2TestConfig.class}
)
@ExtendWith(MockitoExtension.class)
class DepositControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private DepositRepository depositRepository;

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
    void itShouldCreateDeposit() throws Exception {
        // given
        when(billServiceClient.getBillById(anyLong())).thenReturn(createBillResponseDTO());
        AccountResponseDTO accountResponseDTO = createAccountResponseDTO();
        when(accountServiceClient.getAccountById(anyLong())).thenReturn(accountResponseDTO);
        DepositRequestDTO requestDTO = DepositRequestDTO.builder()
                .accountId(null).billId(1L).amount(BigDecimal.valueOf(100)).build();
        // when
        ObjectMapper mapper = new ObjectMapper();
        MvcResult mvcResult = mockMvc
                .perform(post("/deposits")
                        .content(mapper.writeValueAsString(requestDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        // then
        String body = mvcResult.getResponse().getContentAsString();
        DepositResponseDTO depositResponseDTO = mapper.readValue(body, DepositResponseDTO.class);
        List<Deposit> expected = depositRepository.findByEmail(accountResponseDTO.getEmail());
        assertThat(expected.size()).isEqualTo(1);
        assertThat(expected.get(0).getEmail()).isEqualTo(depositResponseDTO.getEmail());
    }

    private AccountResponseDTO createAccountResponseDTO() {
        return AccountResponseDTO.builder()
                .accountId(1L)
                .name("dmitry")
                .email("dmitry@gmail.com")
                .phone("+79520009939")
                .creationDate(OffsetDateTime.now())
                .bills(List.of(1L))
                .build();
    }

    private BillResponseDTO createBillResponseDTO() {
        return BillResponseDTO.builder()
                .account(1L)
                .amount(BigDecimal.valueOf(1000))
                .billId(1L)
                .creationDate(OffsetDateTime.now())
                .isDefault(true)
                .overdraftEnabled(true)
                .build();
    }

}