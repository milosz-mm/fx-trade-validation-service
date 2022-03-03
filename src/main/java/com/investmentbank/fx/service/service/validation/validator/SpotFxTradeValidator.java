package com.investmentbank.fx.service.service.validation.validator;

import com.investmentbank.fx.service.model.result.ValidationResult;
import com.investmentbank.fx.service.model.trade.FxTrade;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("SpotFxTradeValidator")
public class SpotFxTradeValidator implements FxTradeValidator {

  private final FxTradeValidator commonFxTradeValidator;

  public SpotFxTradeValidator(
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
