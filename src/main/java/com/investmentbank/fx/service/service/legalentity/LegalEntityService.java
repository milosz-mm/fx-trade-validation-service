package com.investmentbank.fx.service.service.legalentity;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class LegalEntityService {

  //TODO in real scenario such data should be fetched from external system/database/config
  @Value("#{'${fx.trade.legalEntities}'.split(',')}")
  private List<String> legalEntities;

  public boolean isValidLegalEntity(@NonNull String legalEntityName) {
    return legalEntities.contains(legalEntityName);
  }
}
