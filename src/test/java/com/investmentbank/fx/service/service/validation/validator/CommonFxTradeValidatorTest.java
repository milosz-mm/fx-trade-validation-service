package com.investmentbank.fx.service.service.validation.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.investmentbank.fx.service.model.result.Status;
import com.investmentbank.fx.service.model.result.ValidationResult;
import com.investmentbank.fx.service.model.trade.FxTrade;
import com.investmentbank.fx.service.model.trade.SpotFxTrade;
import com.investmentbank.fx.service.service.businessday.BusinessDayService;
import com.investmentbank.fx.service.service.counterparty.CounterpartyService;
import com.investmentbank.fx.service.service.currency.CurrencyService;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommonFxTradeValidatorTest {

  private CommonFxTradeValidator commonFxTradeValidator;

  @Mock
  private BusinessDayService businessDayServiceMock;

  @Mock
  private CurrencyService currencyServiceMock;

  @Mock
  private CounterpartyService counterpartyServiceMock;

  @BeforeEach
  public void setUp() {
    commonFxTradeValidator = new CommonFxTradeValidator(businessDayServiceMock,
        currencyServiceMock, counterpartyServiceMock);
  }

  @Test
  public void should_detect_invalid_ccy_pair() {
    //given
    LocalDate testValueDate = LocalDate.of(2022, 2, 2);
    String testCcyPair = "USDPL";
    given(businessDayServiceMock.isBusinessDay(testValueDate)).willReturn(true);
    given(currencyServiceMock.isValidCurrency("USD")).willReturn(true);
    given(currencyServiceMock.isValidCurrency("PL")).willReturn(false);
    given(counterpartyServiceMock.isValidCounterparty("YODA1")).willReturn(true);
    FxTrade testTrade = SpotFxTrade.builder()
        .tradeDate(LocalDate.of(2022, 2, 1))
        .valueDate(testValueDate)
        .ccyPair(testCcyPair)
        .customer("YODA1")
        .build();
    //when
    ValidationResult result = commonFxTradeValidator.validate(testTrade);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(Status.INVALID);
    assertThat(result.getMessage()).containsOnly(
        "ccyPair USDPL is not valid ISO 4217 currency pair");
  }

  @Test
  public void should_detect_non_business_day_value_date() {
    //given
    LocalDate testValueDate = LocalDate.of(2022, 2, 2);
    String testCcyPair = "USDPLN";
    given(businessDayServiceMock.isBusinessDay(testValueDate)).willReturn(false);
    given(currencyServiceMock.isValidCurrency("USD")).willReturn(true);
    given(currencyServiceMock.isValidCurrency("PLN")).willReturn(true);
    given(counterpartyServiceMock.isValidCounterparty("YODA1")).willReturn(true);
    FxTrade testTrade = SpotFxTrade.builder()
        .tradeDate(LocalDate.of(2022, 2, 1))
        .valueDate(testValueDate)
        .ccyPair(testCcyPair)
        .customer("YODA1")
        .build();
    //when
    ValidationResult result = commonFxTradeValidator.validate(testTrade);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(Status.INVALID);
    assertThat(result.getMessage()).containsOnly("valueDate 2022-02-02 is not a business day");
  }

  @Test
  public void should_detect_value_date_before_trade_date() {
    //given
    LocalDate testValueDate = LocalDate.of(2022, 1, 2);
    String testCcyPair = "USDPLN";
    given(businessDayServiceMock.isBusinessDay(testValueDate)).willReturn(true);
    given(currencyServiceMock.isValidCurrency("USD")).willReturn(true);
    given(currencyServiceMock.isValidCurrency("PLN")).willReturn(true);
    given(counterpartyServiceMock.isValidCounterparty("YODA1")).willReturn(true);
    FxTrade testTrade = SpotFxTrade.builder()
        .tradeDate(LocalDate.of(2022, 2, 1))
        .valueDate(testValueDate)
        .customer("YODA1")
        .ccyPair(testCcyPair)
        .build();
    //when
    ValidationResult result = commonFxTradeValidator.validate(testTrade);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(Status.INVALID);
    assertThat(result.getMessage()).containsOnly(
        "valueDate 2022-01-02 is before tradeDate 2022-02-01");
  }

  @Test
  public void should_detect_invalid_cpty() {
    //given
    LocalDate testValueDate = LocalDate.of(2022, 2, 2);
    final String testCcyPair = "USDPLN";
    final String testCpty = "YODA3";
    given(businessDayServiceMock.isBusinessDay(testValueDate)).willReturn(true);
    given(currencyServiceMock.isValidCurrency("USD")).willReturn(true);
    given(currencyServiceMock.isValidCurrency("PLN")).willReturn(true);
    given(counterpartyServiceMock.isValidCounterparty(testCpty)).willReturn(false);

    FxTrade testTrade = SpotFxTrade.builder()
        .tradeDate(LocalDate.of(2022, 2, 1))
        .valueDate(testValueDate)
        .ccyPair(testCcyPair)
        .customer(testCpty)
        .build();
    //when
    ValidationResult result = commonFxTradeValidator.validate(testTrade);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(Status.INVALID);
    assertThat(result.getMessage()).containsOnly("customer " + testCpty + " is invalid");
  }
}
