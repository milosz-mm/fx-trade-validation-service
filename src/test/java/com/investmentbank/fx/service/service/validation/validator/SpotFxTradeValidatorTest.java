package com.investmentbank.fx.service.service.validation.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.google.common.collect.ImmutableList;
import com.investmentbank.fx.service.model.result.Status;
import com.investmentbank.fx.service.model.result.ValidationResult;
import com.investmentbank.fx.service.model.trade.FxTrade;
import com.investmentbank.fx.service.model.trade.SpotFxTrade;
import java.time.LocalDate;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SpotFxTradeValidatorTest {

  private SpotFxTradeValidator spotFxTradeValidator;

  @Mock
  private CommonFxTradeValidator commonFxTradeValidatorMock;

  @BeforeEach
  public void setUp() {
    spotFxTradeValidator = new SpotFxTradeValidator(commonFxTradeValidatorMock);
  }

  @Test
  public void should_validate_successfully() {
    //given
    LocalDate testValueDate = LocalDate.of(2022, 2, 2);
    FxTrade testTrade = SpotFxTrade.builder()
        .tradeDate(LocalDate.of(2022, 2, 1))
        .valueDate(testValueDate)
        .ccyPair("USDPLN")
        .customer("YODA1")
        .build();
    given(commonFxTradeValidatorMock.validate(testTrade)).willReturn(ValidationResult.builder()
        .message(Collections.emptyList())
        .index(0)
        .status(Status.OK)
        .build());

    //when
    ValidationResult result = spotFxTradeValidator.validate(testTrade);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(Status.OK);
    assertThat(result.getMessage()).isEmpty();
  }

  @Test
  public void should_detect_invalid_trade() {
    //given
    LocalDate testValueDate = LocalDate.of(2022, 2, 2);
    FxTrade testTrade = SpotFxTrade.builder()
        .tradeDate(LocalDate.of(2022, 2, 1))
        .valueDate(testValueDate)
        .ccyPair("USDPLN")
        .customer("YODA1")
        .build();
    given(commonFxTradeValidatorMock.validate(testTrade)).willReturn(ValidationResult.builder()
        .message(ImmutableList.of("Sample error"))
        .index(0)
        .status(Status.INVALID)
        .build());

    //when
    ValidationResult result = spotFxTradeValidator.validate(testTrade);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(Status.INVALID);
    assertThat(result.getMessage()).containsOnly("Sample error");
  }

}
