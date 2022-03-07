package com.investmentbank.fx.service.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConcurrentUtil {

  public static <T> T parallelizeStream(ForkJoinPool executorService, Supplier<T> supplier) {
    try {
      return executorService.submit(supplier::get).get();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

}
