package com.investmentbank.fx.service.service.counterparty;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CounterpartyServiceTest {

  @Autowired
  private CounterpartyService legalEntityService;

  @Test
  public void should_detect_valid_cpty_YODA1() {
    // given & when
    boolean result = legalEntityService.isValidCounterparty("YODA1");

    // then
    assertThat(result).isTrue();
  }

  @Test
  public void should_detect_valid_cpty_YODA2() {
    // given & when
    boolean result = legalEntityService.isValidCounterparty("YODA2");

    // then
    assertThat(result).isTrue();
  }

  @Test
  public void should_detect_invalid_cpty() {
    // given & when
    boolean result = legalEntityService.isValidCounterparty("MBANK");

    // then
    assertThat(result).isFalse();
  }
}
