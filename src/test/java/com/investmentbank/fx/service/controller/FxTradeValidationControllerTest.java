package com.investmentbank.fx.service.controller;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.ImmutableList;
import com.investmentbank.fx.service.model.result.Status;
import com.investmentbank.fx.service.model.result.ValidationResult;
import com.investmentbank.fx.service.model.result.ValidationResultList;
import com.investmentbank.fx.service.model.trade.FxTrade;
import com.investmentbank.fx.service.model.trade.FxTradeContainer;
import com.investmentbank.fx.service.model.trade.SpotFxTrade;
import com.investmentbank.fx.service.service.validation.ValidationService;
import java.io.IOException;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class FxTradeValidationControllerTest {

  @Autowired
  private FxTradeValidationController controller;

  @MockBean
  private ValidationService validationServiceMock;

  private FxTrade testFxTrade;

  @BeforeEach
  public void setUp() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    testFxTrade = objectMapper.readValue(
        getClass().getClassLoader().getResourceAsStream("spotFxTrade.json"), SpotFxTrade.class);
  }

  @Test
  public void should_validate_single_trade() {
    // given
    ValidationResult validationResult = ValidationResult.builder()
        .status(Status.OK)
        .message(Collections.emptyList())
        .build();
    given(validationServiceMock.validateFxTrade(any(SpotFxTrade.class))).willReturn(
        validationResult);
    // when
    ResponseEntity<ValidationResult> response = controller.validateFxTrade(testFxTrade);

    // then
    verify(validationServiceMock).validateFxTrade(testFxTrade);
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull().isEqualTo(validationResult);
  }

  @Test
  public void should_validate_bulk_trade_request() {
    // given
    ValidationResultList validationResult = ValidationResultList.builder()
        .status(Status.OK)
        .results(ImmutableList.of(ValidationResult.builder()
            .status(Status.OK)
            .message(Collections.emptyList())
            .build()))
        .build();
    given(validationServiceMock.validateFxTrades(anyList())).willReturn(validationResult);
    FxTradeContainer fxTradeContainer = new FxTradeContainer();
    fxTradeContainer.add(testFxTrade);
    fxTradeContainer.add(testFxTrade);

    // when
    ResponseEntity<ValidationResultList> response = controller.bulkValidateFxTrade(
        fxTradeContainer);

    // then
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull().isEqualTo(validationResult);
  }
}
