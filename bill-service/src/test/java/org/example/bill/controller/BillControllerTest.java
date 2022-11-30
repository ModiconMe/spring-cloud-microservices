package org.example.bill.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.bill.BillApplication;
import org.example.bill.config.SpringBootH2TestConfig;
import org.example.bill.controller.dto.BillMapper;
import org.example.bill.controller.dto.BillRequestDTO;
import org.example.bill.controller.dto.BillResponseDTO;
import org.example.bill.entity.Bill;
import org.example.bill.repository.BillRepository;
import org.example.bill.rest.AccountServiceClient;
import org.example.bill.service.BillService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest(classes = {BillApplication.class, SpringBootH2TestConfig.class})
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BillControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private BillService billService;

    @Autowired
    private BillMapper mapper;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private AccountServiceClient accountServiceClient;

    @BeforeEach
    void setUp() {
        mockMvc = webAppContextSetup(context).build();
    }

    @Test
    @Order(1)
    void itShouldNotGetBill_whenBillDoesNotExist() throws Exception {
        // given
        // when
        ResultActions perform = mockMvc
                .perform(get("/bills/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        perform.andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @Order(2)
    void itShouldCreateBill() throws Exception {
        // given
        BillRequestDTO billRequestDTO = createBillRequest();

        // when
        String json = objectToJson(billRequestDTO);
        ResultActions perform = mockMvc
                .perform(post("/bills/")
                        .content(Objects.requireNonNull(json))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        MvcResult mvcResult = perform.andExpect(status().isOk()).andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        Optional<Bill> expected = billRepository.findById(1L);
        assertThat(expected.isPresent()).isTrue();
        assertThat(expected.get().getBillId()).isEqualTo(Integer.parseInt(body));
    }

    @Test
    @Order(3)
    void itShouldGetBill() throws Exception {
        // given
        BillRequestDTO billRequestDTO = createBillRequest();

        // when
        String json = objectToJson(billRequestDTO);
        ResultActions perform = mockMvc
                .perform(get("/bills/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        MvcResult mvcResult = perform.andExpect(status().isOk()).andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        assertThat(billRequestDTO.getAmount()).isEqualTo(Objects.requireNonNull(jsonToObject(body)).getAmount());
    }

    @Test
    @Order(4)
    void itShouldUpdateBill() throws Exception {
        // given
        BillRequestDTO billRequestDTO = createBillRequest();
        billRequestDTO.setAmount(new BigDecimal("200.00"));

        // when
        String json = objectToJson(billRequestDTO);
        ResultActions perform = mockMvc
                .perform(put("/bills/1")
                        .content(Objects.requireNonNull(json))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        MvcResult mvcResult = perform.andExpect(status().isOk()).andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        assertThat(billRequestDTO.getAmount()).isEqualTo(Objects.requireNonNull(jsonToObject(body)).getAmount());
    }

    @Test
    @Order(5)
    void itShouldNotUpdateBill_whenBillDoesNotExist() throws Exception {
        // given
        BillRequestDTO billRequestDTO = createBillRequest();
        billRequestDTO.setAmount(new BigDecimal("200.00"));

        // when
        String json = objectToJson(billRequestDTO);
        ResultActions perform = mockMvc
                .perform(put("/bills/777")
                        .content(Objects.requireNonNull(json))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        perform.andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @Order(6)
    void itShouldMakeBillDefault() throws Exception {
        // given
        // when
        ResultActions perform = mockMvc
                .perform(put("/bills/default/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        MvcResult mvcResult = perform.andExpect(status().isOk()).andReturn();
        Optional<Bill> optionalBill = billRepository.findById(1L);
        assertThat(optionalBill.get().isDefault()).isTrue();
    }

    @Test
    @Order(7)
    void itShouldNotMakeBillDefault_whenBillDoesNotExist() throws Exception {
        // given
        // when
        ResultActions perform = mockMvc
                .perform(put("/bills/default/777")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        MvcResult mvcResult = perform.andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @Order(8)
    void isShouldGetBillsByAccountId() throws Exception {
        // given
        // when
        ResultActions perform = mockMvc
                .perform(get("/bills/account/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        MvcResult mvcResult = perform.andExpect(status().isOk()).andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        assertThat(1).isEqualTo(Objects.requireNonNull(jsonToListObject(body)).size());
    }

    @Test
    @Order(9)
    void isShouldNotGetBillsByAccountId_whenAccountDoesNotExist() throws Exception {
        // given
        // when
        ResultActions perform = mockMvc
                .perform(get("/bills/account/777")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        MvcResult mvcResult = perform.andExpect(status().isOk()).andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        assertThat(0).isEqualTo(Objects.requireNonNull(jsonToListObject(body)).size());
    }

    @Test
    @Order(10)
    void itShouldDeleteBill() throws Exception {
        // given
        // when
        ResultActions perform = mockMvc
                .perform(delete("/bills/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        perform.andExpect(status().isOk()).andReturn();
    }

    @Test
    @Order(11)
    void itShouldNotDeleteBill() throws Exception {
        // given
        // when
        ResultActions perform = mockMvc
                .perform(delete("/bills/777")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        perform.andExpect(status().isBadRequest()).andReturn();
    }

    private BillRequestDTO createBillRequest() {
        return BillRequestDTO.builder()
                .amount(new BigDecimal("100.00"))
                .account(1L)
                .creationDate(OffsetDateTime.of(2022, 7, 9, 22, 10, 30, 10, ZoneOffset.of("Z")))
                .overdraftEnabled(false)
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

    private BillResponseDTO jsonToObject(String json) {
        try {
            ObjectMapper mapper1 = new ObjectMapper();
            mapper1.registerModule(new JavaTimeModule());
            return mapper1.readValue(json, BillResponseDTO.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<BillResponseDTO> jsonToListObject(String json) {
        try {
            ObjectMapper mapper1 = new ObjectMapper();
            mapper1.registerModule(new JavaTimeModule());
            return mapper1.readValue(json, List.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}