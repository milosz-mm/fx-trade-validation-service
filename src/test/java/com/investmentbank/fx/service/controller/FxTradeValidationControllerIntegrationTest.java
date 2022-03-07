package com.investmentbank.fx.service.controller;


import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.investmentbank.fx.service.model.result.Status;
import com.investmentbank.fx.service.model.result.ValidationResult;
import com.investmentbank.fx.service.model.result.ValidationResultList;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class FxTradeValidationControllerIntegrationTest {

  private static final LocalDate TODAY = LocalDate.of(2020, 9, 10);

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  private ObjectMapper objectMapper;

  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
  }

  @Test
  public void should_validate_single_trade() throws Exception {
    byte[] testTradePayload = Files.readAllBytes(
        Paths.get(getClass().getClassLoader().getResource("spotFxTrade.json").toURI()));
    MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/v1/validate")
            .content(new String(testTradePayload)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    MockHttpServletResponse response = mvcResult.getResponse();
    assertThat(response.getContentType()).isEqualTo("application/json");
    ValidationResult validationResult = objectMapper.readValue(response.getContentAsString(),
        ValidationResult.class);
    verifyValidationResult(validationResult, Status.INVALID,
        "valueDate 2020-08-15 is not a business day");
  }

  @Test
  public void should_validate_multiple_trades() throws Exception {
    // given
    byte[] testTradePayload = Files.readAllBytes(Paths.get(
        requireNonNull(getClass().getClassLoader().getResource("testTrades.json")).toURI()));

    // when
    MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/v1/bulk-validate")
            .content(new String(testTradePayload)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    // then
    MockHttpServletResponse response = mvcResult.getResponse();
    assertThat(response.getContentType()).isEqualTo("application/json");
    ValidationResultList validationResult = objectMapper.readValue(response.getContentAsString(),
        ValidationResultList.class);
    int index = 0;
    verifyValidationResultList(validationResult, Status.INVALID,
        ValidationResult.builder().status(Status.INVALID)
            .message(List.of("valueDate 2020-08-15 is not a business day")).index(index++).build(),
        ValidationResult.builder().status(Status.INVALID)
            .message(List.of("valueDate 2020-08-22 is not a business day")).index(index++).build(),
        ValidationResult.builder().status(Status.INVALID)
            .message(List.of("valueDate 2020-08-22 is not a business day")).index(index++).build(),
        ValidationResult.builder().status(Status.OK).message(Collections.emptyList()).index(index++)
            .build(),
        ValidationResult.builder().status(Status.INVALID)
            .message(List.of("valueDate 2020-08-08 is before tradeDate 2020-08-11",
                "valueDate 2020-08-08 is not a business day")).index(index++).build(),
        ValidationResult.builder().status(Status.INVALID)
            .message(List.of("valueDate 2020-08-08 is before tradeDate 2020-08-11",
                "valueDate 2020-08-08 is not a business day", "customer PLUT02 is invalid"))
            .index(index++).build(),
        ValidationResult.builder().status(Status.INVALID)
            .message(List.of("valueDate 2020-08-22 is not a business day",
                "customer PLUTO3 is invalid")).index(index++).build(),
        ValidationResult.builder().status(Status.INVALID).message(List.of("valueDate is required"))
            .index(index++).build(),
        ValidationResult.builder().status(Status.INVALID).message(List.of("valueDate is required"))
            .index(index++).build(),
        ValidationResult.builder().status(Status.INVALID)
            .message(List.of("expiryDate 2020-08-25 must be before delivery date 2020-08-22",
                "valueDate is required")).index(index++).build(),
        ValidationResult.builder().status(Status.INVALID).message(List.of("valueDate is required"))
            .index(index++).build(),
        ValidationResult.builder().status(Status.INVALID).message(List.of("valueDate is required"))
            .index(index++).build(),
        ValidationResult.builder().status(Status.INVALID)
            .message(List.of("expiryDate 2020-08-25 must be before delivery date 2020-08-22",
                "valueDate is required")).index(index++).build(),
        ValidationResult.builder().status(Status.INVALID)
            .message(List.of("exerciseStartDate 2020-08-10 must be after tradeDate 2020-08-11",
                "valueDate is required")).index(index++).build(),
        ValidationResult.builder().status(Status.INVALID)
            .message(List.of("exerciseStartDate 2020-08-10 must be after tradeDate 2020-08-11",
                "valueDate is required", "customer PLUTO3 is invalid")).index(index).build()
    );
  }

  private void verifyValidationResult(ValidationResult validationResult, Status expectedStatus,
      String... expectedMessages) {
    assertThat(validationResult).isNotNull();
    assertThat(validationResult.getStatus()).isEqualTo(expectedStatus);
    assertThat(validationResult.getMessage()).containsOnly(expectedMessages);
  }

  private void verifyValidationResultList(ValidationResultList validationResult,
      Status expectedStatus, ValidationResult... expectedValidationResults) {
    assertThat(validationResult).isNotNull();
    assertThat(validationResult.getStatus()).isEqualTo(expectedStatus);
    assertThat(validationResult.getResults()).containsOnly(expectedValidationResults);
  }


}
