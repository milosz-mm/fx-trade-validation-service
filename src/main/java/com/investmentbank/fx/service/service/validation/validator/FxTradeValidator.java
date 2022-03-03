package com.investmentbank.fx.service.service.validation.validator;

import com.investmentbank.fx.service.model.result.ValidationResult;
import com.investmentbank.fx.service.model.trade.FxTrade;

public interface FxTradeValidator {

  ValidationResult validate(FxTrade trade);
}
