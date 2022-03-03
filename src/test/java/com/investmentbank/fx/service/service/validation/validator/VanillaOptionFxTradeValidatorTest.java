package com.investmentbank.fx.service.service.validation.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.google.common.collect.ImmutableList;
import com.investmentbank.fx.service.model.result.Status;
import com.investmentbank.fx.service.model.result.ValidationResult;
import com.investmentbank.fx.service.model.trade.FxTrade;
import com.investmentbank.fx.service.model.trade.VanillaOptionFxTrade;
import com.investmentbank.fx.service.service.TestData;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class VanillaOptionFxTradeValidatorTest {

  private VanillaOptionFxFxTradeValidator vanillaOptionTradeValidator;

  @Mock
  private CommonFxTradeValidator commonFxTradeValidatorMock;

  @BeforeEach
  public void setUp() {
    vanillaOptionTradeValidator = new VanillaOptionFxFxTradeValidator(commonFxTradeValidatorMock);
  }

  @Test
  public void should_validate_american_option_successfully() {
    //given
    FxTrade testTrade = VanillaOptionFxTrade.builder()
        .style("AMERICAN")
        .tradeDate(LocalDate.of(2020, 3, 2))
        .excerciseStartDate(LocalDate.of(2020, 3, 3))
        .expiryDate(LocalDate.of(2020, 4, 2))
        .premiumDate(LocalDate.of(2020, 4, 2))
        .deliveryDate(LocalDate.of(2020, 4, 4))
        .valueDate(LocalDate.of(2020, 3, 2))
        .build();
    given(commonFxTradeValidatorMock.validate(testTrade)).willReturn(TestData.VALIDATION_RESULT_OK);

    //when
    ValidationResult result = vanillaOptionTradeValidator.validate(testTrade);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(Status.OK);
    assertThat(result.getMessage()).isEmpty();
  }

  @Test
  public void should_validate_european_option_successfully() {
    //given
    FxTrade testTrade = VanillaOptionFxTrade.builder()
        .style("EUROPEAN")
        .tradeDate(LocalDate.of(2020, 3, 2))
        .expiryDate(LocalDate.of(2020, 4, 2))
        .premiumDate(LocalDate.of(2020, 4, 2))
        .deliveryDate(LocalDate.of(2020, 4, 4))
        .valueDate(LocalDate.of(2020, 3, 2))
        .build();
    given(commonFxTradeValidatorMock.validate(testTrade)).willReturn(TestData.VALIDATION_RESULT_OK);

    //when
    ValidationResult result = vanillaOptionTradeValidator.validate(testTrade);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(Status.OK);
    assertThat(result.getMessage()).isEmpty();
  }

  @Test
  public void should_detect_violated_common_validation_rule() {
    //given
    FxTrade testTrade = VanillaOptionFxTrade.builder()
        .style("EUROPEAN")
        .tradeDate(LocalDate.of(2020, 3, 2))
        .expiryDate(LocalDate.of(2020, 4, 2))
        .premiumDate(LocalDate.of(2020, 4, 2))
        .deliveryDate(LocalDate.of(2020, 4, 4))
        .valueDate(LocalDate.of(2020, 3, 2))
        .build();
    given(commonFxTradeValidatorMock.validate(testTrade)).willReturn(ValidationResult.builder()
        .message(ImmutableList.of("Sample error"))
        .index(0)
        .status(Status.INVALID)
        .build());

    //when
    ValidationResult result = vanillaOptionTradeValidator.validate(testTrade);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(Status.INVALID);
    assertThat(result.getMessage()).containsOnly("Sample error");
  }

  @Test
  public void should_detect_invalid_style() {
    //given
    FxTrade testTrade = VanillaOptionFxTrade.builder()
        .style("BERMUDA")
        .tradeDate(LocalDate.of(2020, 3, 2))
        .expiryDate(LocalDate.of(2020, 4, 2))
        .premiumDate(LocalDate.of(2020, 4, 2))
        .deliveryDate(LocalDate.of(2020, 4, 4))
        .valueDate(LocalDate.of(2020, 3, 2))
        .build();
    given(commonFxTradeValidatorMock.validate(testTrade)).willReturn(TestData.VALIDATION_RESULT_OK);

    //when
    ValidationResult result = vanillaOptionTradeValidator.validate(testTrade);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(Status.INVALID);
    assertThat(result.getMessage()).containsOnly("Invalid option style BERMUDA");
  }

  @Test
  public void should_detect_american_option_trade_date_after_exercise_date() {
    //given
    final LocalDate testTradeDate = LocalDate.of(2020, 3, 5);
    final LocalDate testExcerciseDate = LocalDate.of(2020, 3, 2);
    FxTrade testTrade = VanillaOptionFxTrade.builder()
        .style("AMERICAN")
        .tradeDate(testTradeDate)
        .expiryDate(LocalDate.of(2020, 4, 2))
        .premiumDate(LocalDate.of(2020, 4, 2))
        .deliveryDate(LocalDate.of(2020, 4, 4))
        .excerciseStartDate(testExcerciseDate)
        .valueDate(LocalDate.of(2020, 3, 2))
        .build();
    given(commonFxTradeValidatorMock.validate(testTrade)).willReturn(TestData.VALIDATION_RESULT_OK);

    //when
    ValidationResult result = vanillaOptionTradeValidator.validate(testTrade);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(Status.INVALID);
    assertThat(result.getMessage()).containsOnly(
        "exerciseStartDate 2020-03-02 must be after tradeDate 2020-03-05");
  }

  @Test
  public void should_detect_american_option_trade_date_after_expiry_date() {
    //given
    final LocalDate testTradeDate = LocalDate.of(2020, 3, 4);
    final LocalDate testExerciseDate = LocalDate.of(2020, 3, 5);
    final LocalDate testExpiryDate = LocalDate.of(2020, 3, 3);
    FxTrade testTrade = VanillaOptionFxTrade.builder()
        .style("AMERICAN")
        .tradeDate(testTradeDate)
        .expiryDate(testExpiryDate)
        .premiumDate(LocalDate.of(2020, 4, 2))
        .deliveryDate(LocalDate.of(2020, 4, 4))
        .excerciseStartDate(testExerciseDate)
        .valueDate(LocalDate.of(2020, 3, 10))
        .build();
    given(commonFxTradeValidatorMock.validate(testTrade)).willReturn(TestData.VALIDATION_RESULT_OK);

    //when
    ValidationResult result = vanillaOptionTradeValidator.validate(testTrade);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(Status.INVALID);
    assertThat(result.getMessage()).containsOnly(
        "exerciseStartDate 2020-03-05 must be before expiryDate 2020-03-03");
  }

  @Test
  public void should_detect_american_option_expiry_date_after_delivery_date() {
    //given
    final LocalDate deliveryDate = LocalDate.of(2020, 4, 4);
    final LocalDate expiryDate = LocalDate.of(2020, 4, 5);
    FxTrade testTrade = VanillaOptionFxTrade.builder()
        .style("AMERICAN")
        .tradeDate(LocalDate.of(2020, 3, 2))
        .expiryDate(expiryDate)
        .premiumDate(LocalDate.of(2020, 4, 2))
        .deliveryDate(deliveryDate)
        .excerciseStartDate(LocalDate.of(2020, 3, 3))
        .valueDate(LocalDate.of(2020, 3, 10))
        .build();
    given(commonFxTradeValidatorMock.validate(testTrade)).willReturn(TestData.VALIDATION_RESULT_OK);

    //when
    ValidationResult result = vanillaOptionTradeValidator.validate(testTrade);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(Status.INVALID);
    assertThat(result.getMessage()).containsOnly(
        "expiryDate 2020-04-05 must be before delivery date 2020-04-04");
  }

  @Test
  public void should_detect_american_option_premium_date_after_delivery_date() {
    //given
    final LocalDate deliveryDate = LocalDate.of(2020, 4, 4);
    final LocalDate premiumDate = LocalDate.of(2020, 4, 5);
    FxTrade testTrade = VanillaOptionFxTrade.builder()
        .style("AMERICAN")
        .tradeDate(LocalDate.of(2020, 3, 2))
        .expiryDate(LocalDate.of(2020, 4, 2))
        .premiumDate(premiumDate)
        .deliveryDate(deliveryDate)
        .excerciseStartDate(LocalDate.of(2020, 3, 3))
        .valueDate(LocalDate.of(2020, 3, 10))
        .build();
    given(commonFxTradeValidatorMock.validate(testTrade)).willReturn(TestData.VALIDATION_RESULT_OK);

    //when
    ValidationResult result = vanillaOptionTradeValidator.validate(testTrade);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(Status.INVALID);
    assertThat(result.getMessage()).containsOnly(
        "premiumDate 2020-04-05 must be before delivery date 2020-04-04");
  }

}
