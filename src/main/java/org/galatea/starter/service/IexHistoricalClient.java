package org.galatea.starter.service;

import java.util.List;
import org.galatea.starter.domain.IexHistoricalPrices;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * A Feign Declarative REST Client to access endpoints from the IEX API to get historical
 * prices.
 */
@FeignClient(name = "IEX", url = "${spring.rest.iexCloudPath}")
public interface IexHistoricalClient {

  /**
   * Get the historical prices for a stock symbol passed in.
   *
   * @param symbols the list of symbols to get a historical prices for.
   * @param range the range (day, month, year) to get a historical prices for.
   * @param date the particular date to get historical prices for.
   * @return a list of the historical price for each of the symbols passed in.
   */
  @GetMapping("/stock/{symbol}/chart/{range}/{date}?token=${spring.rest.iexApiToken}")
  List<IexHistoricalPrices> getHistoricalPricesForSymbols(
      @PathVariable(value = "symbol") String symbols, @PathVariable(value = "range") String range,
      @PathVariable(value = "date") String date);


  /**
   * Get the historical prices for a stock symbol passed in.
   *
   * @param symbols the list of symbols to get a historical prices for.
   * @param range the range (day, month, year) to get a historical prices for.
   * @return a list of the historical price for each of the symbols passed in.
   */
  @GetMapping("/stock/{symbol}/chart/{range}?token=${spring.rest.iexApiToken}")
  List<IexHistoricalPrices> getHistoricalPricesForSymbols(
      @PathVariable(value = "symbol") String symbols, @PathVariable(value = "range") String range);

  /**
   * Get the historical prices for a stock symbol passed in.
   *
   * @param symbols the list of symbols to get a historical prices for.
   * @return a list of the historical price for each of the symbols passed in.
   */
  @GetMapping("/stock/{symbol}/chart?token=${spring.rest.iexApiToken}")
  List<IexHistoricalPrices> getHistoricalPricesForSymbols(
      @PathVariable(value = "symbol") String symbols);
}


