package com.investmentbank.fx.service.service.validation;

import static com.investmentbank.fx.service.util.ConcurrentUtil.parallelizeStream;

import com.codepoetics.protonpack.StreamUtils;
import com.google.common.collect.ImmutableMap;
import com.investmentbank.fx.service.model.result.Status;
import com.investmentbank.fx.service.model.result.ValidationResult;
import com.investmentbank.fx.service.model.result.ValidationResultList;
import com.investmentbank.fx.service.model.trade.ForwardFxTrade;
import com.investmentbank.fx.service.model.trade.FxTrade;
import com.investmentbank.fx.service.model.trade.SpotFxTrade;
import com.investmentbank.fx.service.model.trade.VanillaOptionFxTrade;
import com.investmentbank.fx.service.service.validation.validator.FxTradeValidator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

  private final Map<String, FxTradeValidator> tradeValidators;

  private ForkJoinPool forkJoinPool;

  public ValidationService(
      @Qualifier("ForwardFxTradeValidator") FxTradeValidator forwardFxTradeValidationService,
      @Qualifier("SpotFxTradeValidator") FxTradeValidator spotFxTradeValidationService,
      @Qualifier("VanillaOptionFxTradeValidator") FxTradeValidator vanillaOptionFxTradeValidationService) {
    tradeValidators = ImmutableMap.of(
        SpotFxTrade.TYPE_NAME, spotFxTradeValidationService,
        ForwardFxTrade.TYPE_NAME, forwardFxTradeValidationService,
        VanillaOptionFxTrade.TYPE_NAME, vanillaOptionFxTradeValidationService);
    forkJoinPool = new ForkJoinPool();
  }

  @PreDestroy
  public void destroy() {
    forkJoinPool.shutdown();
    forkJoinPool = null;
  }

  public ValidationResult validateFxTrade(FxTrade fxTrade) {
    return validateSingleFxTrade(fxTrade);
  }

  public ValidationResultList validateFxTrades(List<FxTrade> fxTradeList) {
    List<ValidationResult> result = parallelizeStream(forkJoinPool,
        () -> StreamUtils.zipWithIndex(fxTradeList.stream()).parallel()
            .map(pair -> validateSingleFxTrade(pair.getValue()).toBuilder()
                .index((int) pair.getIndex()).build())
            .collect(Collectors.toList()));

    Status status =
        result.stream().anyMatch(r -> r.getStatus() == Status.INVALID) ? Status.INVALID : Status.OK;
    return ValidationResultList.builder()
        .results(result)
        .status(status)
        .build();
  }


  private ValidationResult validateSingleFxTrade(FxTrade fxTrade) {
    return Optional.ofNullable(tradeValidators.get(fxTrade.getType()))
        .map(validator -> validator.validate(fxTrade))
        .orElseThrow(
            () -> new IllegalStateException(
                "Validator not found for type " + fxTrade.getType()));
  }
}
