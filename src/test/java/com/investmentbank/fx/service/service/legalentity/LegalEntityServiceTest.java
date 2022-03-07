package com.investmentbank.fx.service.service.legalentity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class LegalEntityServiceTest {

  @Autowired
  private LegalEntityService legalEntityService;

  @Test
  public void should_detect_valid_legal_entity() {
    // given & when
    boolean result = legalEntityService.isValidLegalEntity("UBS AG");

    // then
    assertThat(result).isTrue();
  }

  @Test
  public void should_detect_invalid_legal_entity() {
    // given & when
    boolean result = legalEntityService.isValidLegalEntity("MBANK");

    // then
    assertThat(result).isFalse();
  }
}
