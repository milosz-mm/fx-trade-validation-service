package com.investmentbank.fx.service.model.trade;

import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("VanillaOption")
public class VanillaOptionFxTrade extends FxTrade {

  public static final String TYPE_NAME = "VanillaOption";
  public static final String STYLE_AMERICAN = "AMERICAN";
  public static final String STYLE_EUROPEAN = "EUROPEAN";

  private String premiumCcy;
  private String premiumType;
  private LocalDate expiryDate;
  private double premium;
  private String style;
  private String strategy;
  private LocalDate deliveryDate;
  private String payCcy;
  private LocalDate premiumDate;
  private LocalDate excerciseStartDate;

  public boolean isAmerican() {
    return STYLE_AMERICAN.equalsIgnoreCase(style);
  }
}