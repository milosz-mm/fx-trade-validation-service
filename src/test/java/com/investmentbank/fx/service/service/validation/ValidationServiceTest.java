package com.investmentbank.fx.service.service.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.investmentbank.fx.service.model.result.ValidationResult;
import com.investmentbank.fx.service.model.trade.ForwardFxTrade;
import com.investmentbank.fx.service.model.trade.SpotFxTrade;
import com.investmentbank.fx.service.model.trade.VanillaOptionFxTrade;
import com.investmentbank.fx.service.service.validation.validator.FxTradeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ValidationServiceTest {

  @Mock
  FxTradeValidator spotFxTradeValidationServiceMock;
  @Mock
  FxTradeValidator vanillaOptionFxTradeValidationServiceMock;
  private ValidationService validationService;
  @Mock
  private FxTradeValidator forwardFxTradeValidationServiceMock;

  @BeforeEach
  public void setUp() {
    validationService = new ValidationService(forwardFxTradeValidationServiceMock,
        spotFxTradeValidationServiceMock, vanillaOptionFxTradeValidationServiceMock);
  }

  @Test
  public void should_use_forward_fx_trade_validator() {
    //given
    ForwardFxTrade forwardFxTrade = ForwardFxTrade.builder()
        .type(ForwardFxTrade.TYPE_NAME)
        .build();
    given(forwardFxTradeValidationServiceMock.validate(forwardFxTrade)).willReturn(
        ValidationResult.builder().build());
    //when
    ValidationResult result = validationService.validateFxTrade(forwardFxTrade);

    //then
    verify(forwardFxTradeValidationServiceMock).validate(forwardFxTrade);
    verify(spotFxTradeValidationServiceMock, never()).validate(any());
    verify(vanillaOptionFxTradeValidationServiceMock, never()).validate(any());
    assertThat(result).isNotNull();
  }

  @Test
  public void should_use_spot_fx_trade_validator() {
    //given
    SpotFxTrade forwardFxTrade = SpotFxTrade.builder()
        .type(SpotFxTrade.TYPE_NAME)
        .build();
    given(spotFxTradeValidationServiceMock.validate(forwardFxTrade)).willReturn(
        ValidationResult.builder().build());
    //when
    ValidationResult result = validationService.validateFxTrade(forwardFxTrade);

    //then
    verify(forwardFxTradeValidationServiceMock, never()).validate(any());
    verify(spotFxTradeValidationServiceMock).validate(forwardFxTrade);
    verify(vanillaOptionFxTradeValidationServiceMock, never()).validate(any());
    assertThat(result).isNotNull();
  }

  @Test
  public void should_use_vanilla_option_trade_validator() {
    //given
    VanillaOptionFxTrade forwardFxTrade = VanillaOptionFxTrade.builder()
        .type(VanillaOptionFxTrade.TYPE_NAME)
        .build();
    given(vanillaOptionFxTradeValidationServiceMock.validate(forwardFxTrade)).willReturn(
        ValidationResult.builder().build());
    //when
    ValidationResult result = validationService.validateFxTrade(forwardFxTrade);

    //then
    verify(forwardFxTradeValidationServiceMock, never()).validate(any());
    verify(spotFxTradeValidationServiceMock, never()).validate(any());
    verify(vanillaOptionFxTradeValidationServiceMock).validate(forwardFxTrade);
    assertThat(result).isNotNull();
  }

  @Test
  public void should_detect_not_supported_trade() {
    //given
    final String unsupportedType = "BermudaOption";
    VanillaOptionFxTrade forwardFxTrade = VanillaOptionFxTrade.builder()
        .type(unsupportedType)
        .build();

    //when
    IllegalStateException thrown = assertThrows(IllegalStateException.class,
        () -> validationService.validateFxTrade(forwardFxTrade),
        "NumberFormatException was expected");

    //then
    verify(forwardFxTradeValidationServiceMock, never()).validate(any());
    verify(spotFxTradeValidationServiceMock, never()).validate(any());
    verify(vanillaOptionFxTradeValidationServiceMock, never()).validate(any());
    assertThat(thrown).isNotNull();
    assertThat(thrown.getMessage()).isEqualTo("Validator not found for type " + unsupportedType);
  }
}
