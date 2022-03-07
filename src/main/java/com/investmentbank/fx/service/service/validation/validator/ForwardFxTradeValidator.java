package com.investmentbank.fx.service.service.validation.validator;

import com.investmentbank.fx.service.model.result.ValidationResult;
import com.investmentbank.fx.service.model.trade.FxTrade;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("ForwardFxTradeValidator")
public class ForwardFxTradeValidator implements FxTradeValidator {

  private final FxTradeValidator commonFxTradeValidator;

  public ForwardFxTradeValidator(
      @Qualifier("CommonFxTradeValidator") FxTradeValidator commonFxTradeValidator) {
    this.commonFxTradeValidator = commonFxTradeValidator;
  }

  @Override
  public ValidationResult validate(FxTrade trade) {
    ValidationResult result = commonFxTradeValidator.validate(trade);
    //TODO validate date against product type?
    return result;
  }
}
