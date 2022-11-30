package org.example.account.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.account.AccountApplication;
import org.example.account.config.SpringBootH2TestConfig;
import org.example.account.controller.dto.AccountMapper;
import org.example.account.controller.dto.AccountRequestDTO;
import org.example.account.controller.dto.AccountResponseDTO;
import org.example.account.entity.Account;
import org.example.account.repository.AccountRepository;
import org.example.account.rest.BillServiceClient;
import org.example.account.service.AccountService;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest(classes = {AccountApplication.class, SpringBootH2TestConfig.class})
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccountControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountMapper mapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private BillServiceClient billServiceClient;

    @BeforeEach
    void setUp() {
        mockMvc = webAppContextSetup(context).build();
    }

    @Test
    @Order(1)
    void itShouldNotGetAccount_whenAccountDoesNotExistById() throws Exception {
        // given
        // when
        ResultActions perform = mockMvc
                .perform(get("/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        perform.andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @Order(2)
    void itShouldNotUpdateAccount_whenAccountDoesNotExist() throws Exception {
        // given
        AccountRequestDTO accountRequestDTO = createAccountRequest();

        // when
        String json = objectToJson(accountRequestDTO);
        ResultActions perform = mockMvc
                .perform(put("/accounts/1")
                        .content(Objects.requireNonNull(json))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        MvcResult mvcResult = perform.andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @Order(3)
    void itShouldCreateAccount() throws Exception {
        // given
        AccountRequestDTO accountRequestDTO = createAccountRequest();

        // when
        String json = objectToJson(accountRequestDTO);
        ResultActions perform = mockMvc
                .perform(post("/accounts")
                        .content(Objects.requireNonNull(json))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        MvcResult mvcResult = perform.andExpect(status().isOk()).andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        Optional<Account> expected = accountRepository.findByEmail(accountRequestDTO.getEmail());
        assertThat(expected.isPresent()).isTrue();
        assertThat(expected.get().getAccountId()).isEqualTo(Integer.parseInt(body));
    }

    @Test
    @Order(4)
    void itShouldNotCreateAccount_whenNameIsEmpty() throws Exception {
        // given
        AccountRequestDTO accountRequestDTO = createAccountRequest();
        accountRequestDTO.setPhone("+89520009939");

        // when
        String json = objectToJson(accountRequestDTO);
        ResultActions perform = mockMvc
                .perform(post("/accounts")
                        .content(Objects.requireNonNull(json))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        MvcResult mvcResult = perform.andExpect(status().isBadRequest()).andReturn();
        System.out.println("--------" + mvcResult.getResponse().getContentAsString());
    }

    @Test
    @Order(5)
    void itShouldNotCreateAccount_whenEmailIsAlreadyExist() throws Exception {
        // given
        AccountRequestDTO accountRequestDTO = createAccountRequest();

        // when
        String json = objectToJson(accountRequestDTO);
        ResultActions perform = mockMvc
                .perform(post("/accounts")
                        .content(Objects.requireNonNull(json))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        perform.andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @Order(6)
    void itShouldNotCreateAccount_whenPhoneIsAlreadyExist() throws Exception {
        // given
        AccountRequestDTO accountRequestDTO = createAccountRequest();

        // when
        String json = objectToJson(accountRequestDTO);
        ResultActions perform = mockMvc
                .perform(post("/accounts")
                        .content(Objects.requireNonNull(json))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));
        // then
        perform.andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @Order(7)
    void itShouldGetAccount() throws Exception {
        // given
        AccountRequestDTO accountRequestDTO = createAccountRequest();

        // when
        String json = objectToJson(accountRequestDTO);

        ResultActions perform = mockMvc
                .perform(get("/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        perform.andExpect(status().isOk()).andReturn();
    }

    @Test
    @Order(8)
    void itShouldNotUpdateAccount_whenAccountDoesNotExistById() throws Exception {
        // given
        AccountRequestDTO accountRequestDTO = createAccountRequest();
        accountRequestDTO.setEmail("dmitry@gmail.com");

        // when
        String json = objectToJson(accountRequestDTO);
        ResultActions perform = mockMvc
                .perform(put("/accounts/777")
                        .content(Objects.requireNonNull(json))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        perform.andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @Order(9)
    void itShouldNotUpdateAccount_whenAccountEmailIsAlreadyExist() throws Exception {
        // given
        AccountRequestDTO accountRequestDTO1 = createAccountRequest();
        AccountRequestDTO accountRequestDTO2 = createAccountRequest();
        String email = "dmitry1@gmail.com";
        accountRequestDTO2.setEmail(email);
        accountRequestDTO2.setPhone("+79520009938");
        accountRequestDTO1.setEmail(email);

        // save second account
        String json1 = objectToJson(accountRequestDTO2);
        mockMvc
                .perform(post("/accounts")
                        .content(Objects.requireNonNull(json1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));


        // when
        String json2 = objectToJson(accountRequestDTO1);
        ResultActions perform = mockMvc
                .perform(put("/accounts/1")
                        .content(Objects.requireNonNull(json2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        perform.andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @Order(10)
    void itShouldNotUpdateAccount_whenAccountPhoneNumberIsAlreadyExist() throws Exception {
        // given
        AccountRequestDTO accountRequestDTO = createAccountRequest();
        // set existing phone number (prev test)
        accountRequestDTO.setPhone("+79520009938");

        // when
        String json = objectToJson(accountRequestDTO);
        ResultActions perform = mockMvc
                .perform(put("/accounts/1")
                        .content(Objects.requireNonNull(json))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        perform.andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @Order(11)
    void itShouldUpdateAccount() throws Exception {
        // given
        AccountRequestDTO accountRequestDTO = createAccountRequest();
        accountRequestDTO.setEmail("dmitry2@gmail.com");
        accountRequestDTO.setPhone("+79520009933");

        // when
        String json = objectToJson(accountRequestDTO);
        ResultActions perform = mockMvc
                .perform(put("/accounts/1")
                        .content(Objects.requireNonNull(json))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        MvcResult mvcResult = perform.andExpect(status().isOk()).andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        Optional<Account> expected = accountRepository.findByEmail(accountRequestDTO.getEmail());
        assertThat(expected.isPresent()).isTrue();
        assertThat(expected.get().getEmail()).isEqualTo(Objects.requireNonNull(jsonToObject(body)).getEmail());
        assertThat(expected.get().getPhone()).isEqualTo(Objects.requireNonNull(jsonToObject(body)).getPhone());
    }

    @Test
    @Order(12)
    void itShouldAddBillToAccount() throws Exception {
        // given
        // when
        ResultActions perform = mockMvc
                .perform(put("/accounts/add-bill/1/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        MvcResult mvcResult = perform.andExpect(status().isOk()).andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        assertThat(1).isEqualTo(Objects.requireNonNull(jsonToObject(body)).getBills().size());
    }

    @Test
    @Order(13)
    void itShouldNotAddBillToAccount_whenAccountDoesNotExistById() throws Exception {
        // given
        // when
        ResultActions perform = mockMvc
                .perform(put("/accounts/add-bill/777/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        perform.andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @Order(14)
    void itShouldRemoveToAccount_whenAccountDoesNotExist() throws Exception {
        // given
        // when
        ResultActions perform = mockMvc
                .perform(put("/accounts/remove-bill/777/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        perform.andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @Order(15)
    void itShouldRemoveToAccount() throws Exception {
        // given
        // when
        ResultActions perform = mockMvc
                .perform(put("/accounts/remove-bill/1/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        MvcResult mvcResult = perform.andExpect(status().isOk()).andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        assertThat(0).isEqualTo(Objects.requireNonNull(jsonToObject(body)).getBills().size());
    }

    private AccountRequestDTO createAccountRequest() {
        AccountRequestDTO accountRequestDTO = AccountRequestDTO
                .builder()
                .name("dmitry")
                .email("dmitry@gmail.com")
                .phone("+79520009939")
                .dateOfBirth(LocalDate.of(1999, 7, 9))
                .build();
        accountRequestDTO.setBills(new ArrayList<>());
        return accountRequestDTO;
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

    private AccountResponseDTO jsonToObject(String json) {
        try {
            ObjectMapper mapper1 = new ObjectMapper();
            mapper1.registerModule(new JavaTimeModule());
            return mapper1.readValue(json, AccountResponseDTO.class);
        } catch (JsonProcessingException e) {
            fail("Failed to convert json to object");
            return null;
        }
    }
}