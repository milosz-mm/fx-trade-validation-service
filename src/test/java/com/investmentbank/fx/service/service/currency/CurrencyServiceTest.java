package com.investmentbank.fx.service.service.currency;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;


public class CurrencyServiceTest {

  private final CurrencyService currencyService = new CurrencyService();

  @Test
  public void should_detect_valid_iso_ccy() {
    // given & when
    boolean result = currencyService.isValidCurrency("PLN");

    // then
    assertThat(result).isTrue();
  }

  @Test
  public void should_detect_invalid_iso_ccy() {
    // given & when
    boolean result = currencyService.isValidCurrency("PL");

    // then
    assertThat(result).isFalse();
  }
}
