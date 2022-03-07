package com.investmentbank.fx.service.service.businessday;

import java.time.DayOfWeek;
import java.time.LocalDate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class BusinessDayService {

  public boolean isBusinessDay(@NonNull LocalDate date) {
    //TODO usual practice is to keep business days in separate database or configuration file
    //as they may differ between countries and depend on current business/political circumstances
    return date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY;
  }
}
