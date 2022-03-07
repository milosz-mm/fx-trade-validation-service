package com.investmentbank.fx.service.service.validation.validator;

import static com.investmentbank.fx.service.service.validation.validator.ValidationHelper.formatDate;
import static com.investmentbank.fx.service.service.validation.validator.ValidationHelper.requiredField;

import com.investmentbank.fx.service.model.result.Status;
import com.investmentbank.fx.service.model.result.ValidationResult;
import com.investmentbank.fx.service.model.trade.FxTrade;
import com.investmentbank.fx.service.model.trade.VanillaOptionFxTrade;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("VanillaOptionFxTradeValidator")
public class VanillaOptionFxFxTradeValidator implements FxTradeValidator {

  private final FxTradeValidator commonFxTradeValidator;

  public VanillaOptionFxFxTradeValidator(
      @Qualifier("CommonFxTradeValidator") FxTradeValidator commonFxTradeValidator) {
    this.commonFxTradeValidator = commonFxTradeValidator;
  }

  @Override
  public ValidationResult validate(FxTrade trade) {
    ValidationResult result = commonFxTradeValidator.validate(trade);
    List<String> messages = validateVanillaOptionFxTrade((VanillaOptionFxTrade) trade);
    messages.addAll(result.getMessage());
    return result.toBuilder()
        .message(messages)
        .status(result.getStatus() == Status.OK && messages.isEmpty() ? Status.OK : Status.INVALID)
        .build();
  }

  private List<String> validateVanillaOptionFxTrade(VanillaOptionFxTrade trade) {
    List<String> messages = new ArrayList<>();
    checkOptionStyle(trade, messages);
    checkExpiryDateNotNull(trade, messages);
    checkPremiumDateNotNull(trade, messages);
    checkDeliveryDateNotNull(trade, messages);
    checkExpiryDateBeforeDeliveryDate(trade, messages);
    checkPremiumDateBeforeDeliveryDate(trade, messages);
    if (trade.isAmerican()) {
      messages.addAll(validateAmericanOptionFields(trade));
    }
    return messages;
  }

  private void checkPremiumDateBeforeDeliveryDate(VanillaOptionFxTrade trade,
      List<String> messages) {
    if (!trade.getPremiumDate().isBefore(trade.getDeliveryDate())) {
      messages.add("premiumDate " + formatDate(trade.getPremiumDate())
          + " must be before delivery date " + formatDate(trade.getDeliveryDate()));
    }
  }

  private void checkExpiryDateBeforeDeliveryDate(VanillaOptionFxTrade trade,
      List<String> messages) {
    if (!trade.getExpiryDate().isBefore(trade.getDeliveryDate())) {
      messages.add("expiryDate " + formatDate(trade.getExpiryDate())
          + " must be before delivery date " + formatDate(trade.getDeliveryDate()));
    }
  }

  private void checkDeliveryDateNotNull(VanillaOptionFxTrade trade, List<String> messages) {
    if (trade.getDeliveryDate() == null) {
      messages.add(requiredField("deliveryDate"));
    }
  }

  private void checkPremiumDateNotNull(VanillaOptionFxTrade trade, List<String> messages) {
    if (trade.getPremiumDate() == null) {
      messages.add(requiredField("premiumDate"));
    }
  }

  private void checkExpiryDateNotNull(VanillaOptionFxTrade trade, List<String> messages) {
    if (trade.getExpiryDate() == null) {
      messages.add(requiredField("expiryDate"));
    }
  }

  private void checkOptionStyle(VanillaOptionFxTrade trade, List<String> messages) {
    if (!isValidOptionStyle(trade.getStyle())) {
      messages.add("Invalid option style " + trade.getStyle());
    }
  }

  private Collection<String> validateAmericanOptionFields(VanillaOptionFxTrade trade) {
    List<String> messages = new ArrayList<>();
    if (trade.getExpiryDate() == null) {
      messages.add("expiryDate is required for " + trade.getStyle() + " option");
    }
    if (!trade.getExcerciseStartDate().isAfter(trade.getTradeDate())) {
      messages.add("exerciseStartDate " + formatDate(trade.getExcerciseStartDate())
          + " must be after tradeDate " + formatDate(trade.getTradeDate()));
    }
    if (!trade.getExcerciseStartDate().isBefore(trade.getExpiryDate())) {
      messages.add("exerciseStartDate " + formatDate(trade.getExcerciseStartDate())
          + " must be before expiryDate " + formatDate(trade.getExpiryDate()));
    }
    return messages;
  }

  private boolean isValidOptionStyle(String style) {
    return VanillaOptionFxTrade.STYLE_AMERICAN.equalsIgnoreCase(style)
        || VanillaOptionFxTrade.STYLE_EUROPEAN.equalsIgnoreCase(style);
  }
}
