package org.galatea.starter.entrypoint;

import java.util.Collections;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import net.sf.aspect4log.Log.Level;
import org.galatea.starter.domain.IexHistoricalPrices;
import org.galatea.starter.domain.IexLastTradedPrice;
import org.galatea.starter.domain.IexSymbol;
import org.galatea.starter.service.IexService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Log(enterLevel = Level.INFO, exitLevel = Level.INFO)
@Validated
@RestController
@RequiredArgsConstructor
public class IexRestController {


  @NonNull
  private IexService iexService;

  /**
   * Exposes an endpoint to get all of the symbols available on IEX.
   *
   * @return a list of all IexStockSymbols.
   */
  @GetMapping(value = "${mvc.iex.getAllSymbolsPath}", produces = {MediaType.APPLICATION_JSON_VALUE})
  public List<IexSymbol> getAllStockSymbols() {
    return iexService.getAllSymbols();
  }

  /**
   * Get the last traded price for each of the symbols passed in.
   *
   * @param symbols list of symbols to get last traded price for.
   * @return a List of IexLastTradedPrice objects for the given symbols.
   */
  @GetMapping(value = "${mvc.iex.getLastTradedPricePath}", produces = {
      MediaType.APPLICATION_JSON_VALUE})
  public List<IexLastTradedPrice> getLastTradedPrice(
      @RequestParam(value = "symbols") final List<String> symbols) {
    return iexService.getLastTradedPriceForSymbols(symbols);
  }

  /**
   * Get the historical prices for each of the symbols passed in.
   *
   * @param symbols the list of symbols to get a historical prices for.
   * @param range the range (day, month, year) to get a historical prices for.
   * @param date the particular date to get historical prices for.
   * @return a List of IexHistoricalPrice objects for the given symbols.
   */
  @GetMapping(value = "${mvc.iex.getHistoricalPricesPath}", produces = {
      MediaType.APPLICATION_JSON_VALUE})
  public List<IexHistoricalPrices> getHistoricalPrices(
      @RequestParam(value = "symbol") final String symbols,
      @RequestParam(value = "range", required = false) final String range,
      @RequestParam(value = "date", required = false) final String date) {

    if (queryNullLengthCheck(symbols)) {
      return Collections.emptyList();
    }

    if (queryNullLengthCheck(date)) {
      if (queryNullLengthCheck(range)) {
        return iexService.getHistoricalPricesForSymbols(symbols);
      } else {
        return iexService.getHistoricalPricesForSymbols(symbols, range);
      }
    } else {
      return iexService.getHistoricalPricesForSymbols(symbols, range, date);
    }
  }

  /**
   * Utility method for getHistoricalPrices that helps null and length
   * checks
   *
   * @param query a string on which the check will be performed
   * @return boolean value based on the checked result
   */
  private boolean queryNullLengthCheck(String query){

    return query == null || query.length() == 0;
  }

}
