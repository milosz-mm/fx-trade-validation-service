package com.investmentbank.fx.service.model.trade;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonTypeName("Forward")
public class ForwardFxTrade extends FxTrade {

  public static final String TYPE_NAME = "Forward";
}
