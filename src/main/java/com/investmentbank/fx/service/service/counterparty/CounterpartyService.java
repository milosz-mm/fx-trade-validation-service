package com.investmentbank.fx.service.service.counterparty;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class CounterpartyService {

  //TODO in real scenario such data should be fetched from external system/database/config
  @Value("#{'${fx.trade.counterparties}'.split(',')}")
  private List<String> counterparties;

  public boolean isValidCounterparty(@NonNull String cptyName) {
    return counterparties.contains(cptyName);
  }
}
