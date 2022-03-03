package com.investmentbank.fx.service.model.trade;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@JsonTypeInfo(use = Id.NAME, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = SpotFxTrade.class, name = SpotFxTrade.TYPE_NAME),
    @JsonSubTypes.Type(value = ForwardFxTrade.class, name = ForwardFxTrade.TYPE_NAME),
    @JsonSubTypes.Type(value = VanillaOptionFxTrade.class, name = VanillaOptionFxTrade.TYPE_NAME)
})
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class FxTrade implements Serializable {

  private String type;
  private String customer;
  private String ccyPair;
  private LocalDate tradeDate;
  private LocalDate valueDate;
  private String trader;
  private double amount1;
  private double amount2;
  private double rate;
  private String legalEntity;
  private String direction;
}
