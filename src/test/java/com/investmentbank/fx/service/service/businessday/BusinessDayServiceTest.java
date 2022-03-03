package com.investmentbank.fx.service.service.businessday;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class BusinessDayServiceTest {

  private final BusinessDayService businessDayService = new BusinessDayService();

  @Test
  public void should_detect_business_day() {
    // given
    LocalDate testBusinessDay = LocalDate.of(2022, 2, 2);

    // when
    boolean result = businessDayService.isBusinessDay(testBusinessDay);

    // then
    assertThat(result).isTrue();
  }

  @Test
  public void should_detect_day_on_weekend() {
    // given
    LocalDate testBusinessDay = LocalDate.of(2022, 2, 13);

    // when
    boolean result = businessDayService.isBusinessDay(testBusinessDay);

    // then
    assertThat(result).isFalse();
  }
}
