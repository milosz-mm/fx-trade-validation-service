package com.investmentbank.fx.service.service.validation.validator;

import static com.investmentbank.fx.service.service.validation.validator.ValidationHelper.formatDate;

import com.investmentbank.fx.service.model.result.Status;
import com.investmentbank.fx.service.model.result.ValidationResult;
import com.investmentbank.fx.service.model.trade.FxTrade;
import com.investmentbank.fx.service.service.businessday.BusinessDayService;
import com.investmentbank.fx.service.service.counterparty.CounterpartyService;
import com.investmentbank.fx.service.service.currency.CurrencyService;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

//TODO externalize validation messages
@Service("CommonFxTradeValidator")
public class CommonFxTradeValidator implements FxTradeValidator {

  private final BusinessDayService businessDayService;
  private final CurrencyService currencyValidator;
  private final CounterpartyService counterPartyService;

  public CommonFxTradeValidator(
      BusinessDayService businessDayService,
      CurrencyService currencyValidator,
      CounterpartyService counterPartyService) {
    this.businessDayService = businessDayService;
    this.currencyValidator = currencyValidator;
    this.counterPartyService = counterPartyService;
  }

  public ValidationResult validate(FxTrade fxTrade) {
    List<String> messages = new ArrayList<>();
    checkValueDateNotNull(fxTrade, messages);
    checkTradeDateNotNull(fxTrade, messages);
    checkValueDateBeforeTradeDate(fxTrade, messages);
    checkValueDateIsBusinessDay(fxTrade, messages);
    checkSupportedCpty(fxTrade, messages);
    checkSupportedCcy(fxTrade, messages);
    return ValidationResult.builder()
        .message(messages)
        .status(getStatus(messages))
        .build();
  }

  private void checkSupportedCcy(FxTrade fxTrade, List<String> messages) {
    if (!isValidCurrencyPair(fxTrade.getCcyPair())) {
      messages.add("ccyPair " + fxTrade.getCcyPair() + " is not valid ISO 4217 currency pair");
    }
  }

  private void checkSupportedCpty(FxTrade fxTrade, List<String> messages) {
    if (!isSupportedCpty(fxTrade.getCustomer())) {
      messages.add("customer " + fxTrade.getCustomer() + " is invalid");
    }
  }

  private void checkValueDateIsBusinessDay(FxTrade fxTrade, List<String> messages) {
    if (fxTrade.getValueDate() != null && !businessDayService.isBusinessDay(
        fxTrade.getValueDate())) {
      messages.add("valueDate " + formatDate(fxTrade.getValueDate()) + " is not a business day");
    }
  }

  private void checkValueDateBeforeTradeDate(FxTrade fxTrade, List<String> messages) {
    if (fxTrade.getValueDate() != null && fxTrade.getTradeDate() != null
        && fxTrade.getValueDate().isBefore(fxTrade.getTradeDate())) {
      messages.add("valueDate " + formatDate(fxTrade.getValueDate()) + " is before tradeDate "
          + formatDate(fxTrade.getTradeDate()));
    }
  }

  private void checkTradeDateNotNull(FxTrade fxTrade, List<String> messages) {
    if (fxTrade.getTradeDate() == null) {
      messages.add(ValidationHelper.requiredField("tradeDate"));
    }
  }

  private void checkValueDateNotNull(FxTrade fxTrade, List<String> messages) {
    if (fxTrade.getValueDate() == null) {
      messages.add(ValidationHelper.requiredField("valueDate"));
    }
  }

  private boolean isValidCurrencyPair(String ccyPair) {
    final String firstCcy = StringUtils.substring(ccyPair, 0, 3);
    final String secondCcy = StringUtils.substring(ccyPair, 3, 6);
    return currencyValidator.isValidCurrency(firstCcy) && currencyValidator.isValidCurrency(
        secondCcy);
  }

  private Status getStatus(List<String> messages) {
    return messages.isEmpty() ? Status.OK : Status.INVALID;
  }

  private boolean isSupportedCpty(String customer) {
    return counterPartyService.isValidCounterparty(customer);
  }
}
