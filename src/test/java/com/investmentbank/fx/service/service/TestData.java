package com.investmentbank.fx.service.service;

import com.investmentbank.fx.service.model.result.Status;
import com.investmentbank.fx.service.model.result.ValidationResult;
import java.util.Collections;

public class TestData {

  public static final ValidationResult VALIDATION_RESULT_OK = ValidationResult.builder()
      .message(Collections.emptyList())
      .index(0)
      .status(Status.OK)
      .build();
}
