package com.investmentbank.fx.service.service.currency;

import java.util.Currency;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class CurrencyService {

  public boolean isValidCurrency(@NonNull String ccy) {
    try {
      return StringUtils.isNotBlank(ccy) && Currency.getInstance(ccy) != null;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
