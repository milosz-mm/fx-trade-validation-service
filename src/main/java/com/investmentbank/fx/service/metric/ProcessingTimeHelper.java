package com.investmentbank.fx.service.metric;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


public final class ProcessingTimeHelper {

  private final MeterRegistry meterRegistry;

  private final Map<String, Timer> timerMap = new HashMap<>();

  public ProcessingTimeHelper(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  public <T> T measure(String name, Supplier<T> s) {
    return this.timerMap.computeIfAbsent(name, k -> buildTimer(name)).record(s);
  }


  private Timer buildTimer(String name) {
    return Timer.builder(name + "Time")
        //zero percentile as workaround for min value
        .publishPercentiles(0, 0.95)
        .publishPercentileHistogram()
        .register(meterRegistry);
  }

  public void addTimer(String name) {
    this.timerMap.computeIfAbsent(name, k -> buildTimer(name));
  }
}
