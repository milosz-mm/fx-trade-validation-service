package com.investmentbank.fx.service.controller;

import com.investmentbank.fx.service.metric.ProcessingTimeHelper;
import com.investmentbank.fx.service.model.result.ValidationResult;
import com.investmentbank.fx.service.model.result.ValidationResultList;
import com.investmentbank.fx.service.model.trade.FxTrade;
import com.investmentbank.fx.service.model.trade.FxTradeContainer;
import com.investmentbank.fx.service.service.validation.ValidationService;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Tag(name = "FxTradeValidationController", description = "FX Trade Validation API")
public class FxTradeValidationController {

  private static final String VERSION = "v1";
  private static final String BULK_VALIDATE_TIMER = "bulkValidate";
  private static final String VALIDATE_TIMER = "validate";

  private final ValidationService validationService;

  private final ProcessingTimeHelper processingTimeHelper;

  public FxTradeValidationController(
      ValidationService validationService,
      MeterRegistry meterRegistry) {
    this.validationService = validationService;
    this.processingTimeHelper = new ProcessingTimeHelper(meterRegistry);
    this.processingTimeHelper.addTimer(VALIDATE_TIMER);
    this.processingTimeHelper.addTimer(BULK_VALIDATE_TIMER);
  }

  @Operation(summary = "Validate single FX trade")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Trade validated and validation result available", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationResult.class))})
  })
  @PostMapping(VERSION + "/validate")
  public ResponseEntity<ValidationResult> validateFxTrade(@RequestBody FxTrade fxTrade) {
    log.info("Received {}", fxTrade);
    ValidationResult result = processingTimeHelper.measure(VALIDATE_TIMER,
        () -> validationService.validateFxTrade(fxTrade));
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "Validate multiple FX trades")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Trades validated and validation result list available", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationResultList.class))})
  })
  @PostMapping(VERSION + "/bulk-validate")
  public ResponseEntity<ValidationResultList> bulkValidateFxTrade(
      @RequestBody FxTradeContainer fxTradeContainer) {
    log.info("Received {}", fxTradeContainer);
    ValidationResultList result = processingTimeHelper.measure(BULK_VALIDATE_TIMER,
        () -> validationService.validateFxTrades(fxTradeContainer));
    return ResponseEntity.ok(result);
  }
}
