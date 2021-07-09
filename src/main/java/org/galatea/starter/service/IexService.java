package org.galatea.starter.service;

import java.util.Collections;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.IexHistoricalPrices;
import org.galatea.starter.domain.IexLastTradedPrice;
import org.galatea.starter.domain.IexSymbol;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * A layer for transformation, aggregation, and business required when retrieving data from IEX.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IexService {

  @NonNull
  private IexClient iexClient;

  @NonNull
  private IexHistoricalClient iexHistoricalClient;


  /**
   * Get all stock symbols from IEX.
   *
   * @return a list of all Stock Symbols from IEX.
   */
  public List<IexSymbol> getAllSymbols() {
    return iexClient.getAllSymbols();
  }

  /**
   * Get the last traded price for each Symbol that is passed in.
   *
   * @param symbols the list of symbols to get a last traded price for.
   * @return a list of last traded price objects for each Symbol that is passed in.
   */
  public List<IexLastTradedPrice> getLastTradedPriceForSymbols(final List<String> symbols) {
    if (CollectionUtils.isEmpty(symbols)) {
      return Collections.emptyList();
    } else {
      return iexClient.getLastTradedPriceForSymbols(symbols.toArray(new String[0]));
    }
  }

  /**
   * Get the historical prices for each Symbol, range and date that is passed in.
   *
   * @param symbols the list of symbols to get a historical prices for.
   * @param range the range (day, month, year) to get a historical prices for.
   * @return a list of historical price objects for each Symbol that is passed in.
   */
  public List<IexHistoricalPrices> getHistoricalPricesForSymbols(final String symbols,
      final String range, final String date) {

    return iexHistoricalClient.getHistoricalPricesForSymbols(symbols, range, date);

  }


  /**
   * Get the historical prices for a Symbol and range that is passed in.
   *
   * @param symbols the list of symbols to get a historical prices for.
   * @param range the range (day, month, year) to get a historical prices for.
   * @return a list of historical price objects for each Symbol that is passed in.
   */
  public List<IexHistoricalPrices> getHistoricalPricesForSymbols(final String symbols,
      final String range) {

    return iexHistoricalClient.getHistoricalPricesForSymbols(symbols, range);

  }

  /**
   * Get the historical prices for a Symbol that is passed in.
   *
   * @param symbols the list of symbols to get a historical prices for.
   * @return a list of historical price objects for each Symbol that is passed in.
   */
  public List<IexHistoricalPrices> getHistoricalPricesForSymbols(final String symbols) {

    return iexHistoricalClient.getHistoricalPricesForSymbols(symbols);

  }


}
