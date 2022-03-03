package com.investmentbank.fx.service.service.validation.validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidationHelper {

  public static String requiredField(String field) {
    return field + " is required";
  }

  public static String formatDate(LocalDate date) {
    return DateTimeFormatter.ISO_DATE.format(date);
  }
}
