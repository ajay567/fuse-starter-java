package org.galatea.starter.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.HistoricalPrice;
import org.galatea.starter.domain.HistoricalPriceId;
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
  private HistoricalPricesService historicalPricesService;

  @NonNull
  private IexClient iexClient;

  @NonNull
  private IexHistoricalClient iexHistoricalClient;

  private final String dateDoesNotMatterWhenQueryIsRange = "NA";


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
   * @param symbols symbol to get a last traded price for.
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
   * @param symbols symbol to get a historical prices for.
   * @param range the range (day, month, year) to get a historical prices for.
   * @return a list of historical price objects for each Symbol that is passed in.
   */
  public List<IexHistoricalPrices> getHistoricalPricesForSymbols(final String symbols,
      final String range, final String date) {

    String dateFormatted = stringDateFormatter(date);

    if (dateFormatted == null) {
      return Collections.emptyList();
    }

    String timeToCheckForPreviousRequests = "10:50";

    HistoricalPriceId historicalPriceId =
        new HistoricalPriceId(symbols, dateFormatted, timeToCheckForPreviousRequests);

    if (historicalPricesService.exists(historicalPriceId)) {

      return retrieveInformationFromDatabaseForDate(symbols, dateFormatted);

    } else {
      // Enter data from new api requests into the database.

      List<IexHistoricalPrices> iexHistoricalPricesApi =
          iexHistoricalClient.getHistoricalPricesForSymbols(symbols, range, date);

      saveDataFromNewApiRequestsToDatabase(symbols, date, timeToCheckForPreviousRequests,
          iexHistoricalPricesApi);

      return iexHistoricalPricesApi;
    }

  }


  /**
   * Get the historical prices for a Symbol and range that is passed in.
   *
   * @param symbols symbol to get a historical prices for.
   * @param range the range (day, month, year) to get a historical prices for.
   * @return a list of historical price objects for each Symbol that is passed in.
   */
  public List<IexHistoricalPrices> getHistoricalPricesForSymbols(final String symbols,
      final String range) {

    LocalDate startDate = LocalDate.now().minusDays(1);
    LocalDate endDate = null;

    String timeForMinuteWhenQueryIsRange = "00:00";

    if (range.equals("1d")) {
      return getHistoricalPricesForSymbols(symbols, range,
          startDate.toString().replace("-",""));
    } else {
      endDate = calculateLowerBoundDate(range, startDate);
    }

    List<String> missingDates = new ArrayList<>();

    List<IexHistoricalPrices> iexHistoricalPrices = new ArrayList<>();

    retrieveInformationFromDatabaseForRange(startDate, endDate, symbols,
        timeForMinuteWhenQueryIsRange, iexHistoricalPrices, missingDates);

    if (iexHistoricalPrices.isEmpty()) {

      List<IexHistoricalPrices> iexHistoricalPricesApi =
          iexHistoricalClient.getHistoricalPricesForSymbols(symbols,range);
      saveDataFromNewApiRequestsToDatabase(symbols, dateDoesNotMatterWhenQueryIsRange,
          timeForMinuteWhenQueryIsRange, iexHistoricalPricesApi);
      iexHistoricalPrices.addAll(iexHistoricalPricesApi);

    } else {

      for (String dates: missingDates) {

        List<IexHistoricalPrices> iexHistoricalPricesApi =
            iexHistoricalClient.getHistoricalPricesForSymbolsChartByDay(symbols,"1m",
                dates);

        saveDataFromNewApiRequestsToDatabase(symbols, dateDoesNotMatterWhenQueryIsRange,
            timeForMinuteWhenQueryIsRange, iexHistoricalPricesApi);
        iexHistoricalPrices.addAll(iexHistoricalPricesApi);
      }

    }

    return iexHistoricalPrices;
  }

  /**
   * Get the historical prices for a Symbol that is passed in.
   *
   * @param symbols symbol to get a historical prices for.
   * @return a list of historical price objects for each Symbol that is passed in.
   */
  public List<IexHistoricalPrices> getHistoricalPricesForSymbols(final String symbols) {

    return getHistoricalPricesForSymbols(symbols,"1m");
  }


  /**
   * Save new api call data into the database.
   *
   * @param symbols stock symbol for which api call is made
   * @param date date for the historical prices
   * @param iexHistoricalPricesApi list which has all the new data from the api calls
   */
  private void saveDataFromNewApiRequestsToDatabase(
      final String symbols, final String date, final String timeForMinuteWhenQueryIsRange,
      final List<IexHistoricalPrices> iexHistoricalPricesApi) {

    for (IexHistoricalPrices iexHistoricalPrices: iexHistoricalPricesApi) {

      HistoricalPrice historicalPrice = new HistoricalPrice(
          symbols, iexHistoricalPrices.getDate(),
          getMinuteValueForDateAndRangeQuery(date, timeForMinuteWhenQueryIsRange,
              iexHistoricalPrices),
          iexHistoricalPrices.getClose(), iexHistoricalPrices.getHigh(),
          iexHistoricalPrices.getLow(), iexHistoricalPrices.getOpen(),
          iexHistoricalPrices.getVolume());
      historicalPricesService.saveOrUpdate(historicalPrice);
    }
  }

  private String getMinuteValueForDateAndRangeQuery(final String date,
      final String timeForMinuteWhenQueryIsRange,
      final IexHistoricalPrices iexHistoricalPrices) {

    return date.equals(dateDoesNotMatterWhenQueryIsRange) ? timeForMinuteWhenQueryIsRange :
        iexHistoricalPrices.getMinute();
  }

  /**
   * Set the end date based on the range parameter provided.
   *
   * @param range range parameter
   * @param startDate the end date is counted down from the start date
   * @return the decided end date
   */
  private LocalDate calculateLowerBoundDate(final String range, final LocalDate startDate) {

    if (range.equals("5y")) {
      return startDate.minusYears(5);
    } else if (range.equals("2y")) {
      return startDate.minusYears(2);
    } else if (range.equals("1y")) {
      return startDate.minusYears(1);
    } else if (range.equals("6m")) {
      return startDate.minusMonths(6);
    } else if (range.equals("3m")) {
      return startDate.minusMonths(3);
    } else if (range.equals("5d")) {
      return startDate.minusDays(5);
    }

    return startDate.minusMonths(1);
  }

  /**
   * Format the date.
   *
   * @param date value to be formatted
   * @return correctly formatted date as string
   */
  private String stringDateFormatter(final String date) {

    try {
      return (new SimpleDateFormat("yyyy-MM-dd"))
          .format(new SimpleDateFormat("yyyyMMdd").parse(date));
    } catch (Exception parseException) {
      System.out.println("The provided date has the wrong format.");
      return null;
    }
  }

  /**
   * Get information from database for existing api requests for date.
   *
   * @param symbols symbol to get a historical prices for
   * @param dateFormatted formatted date value
   * @return list of IexHistoricalPrices
   */
  private List<IexHistoricalPrices> retrieveInformationFromDatabaseForDate(final String symbols,
      final String dateFormatted) {

    List<IexHistoricalPrices> iexHistoricalPrices = new ArrayList<>();

    for (LocalTime time = LocalTime.of(9,30);
        time.isBefore(LocalTime.of(16,1));
        time = time.plusMinutes(1)) {

      HistoricalPriceId historicalPriceId =
          new HistoricalPriceId(symbols, dateFormatted, time.toString());

      if (historicalPricesService.exists(historicalPriceId)) {

        getIexHistoricalPricesObjectFromDatabase(historicalPriceId,
            iexHistoricalPrices);
      }
    }

    return iexHistoricalPrices;
  }

  /**
   * Get information from database for existing api requests for range.
   *
   * @param startDate the date to start the loop at
   * @param endDate the date to end the loop at
   * @param symbols the symbol for which data has to be retrieved
   * @param iexHistoricalPrices list for IexHistoricalPrices
   * @param missingDates list for missing dates not present in the database
   */
  private void retrieveInformationFromDatabaseForRange(final LocalDate startDate,
      final LocalDate endDate, final String symbols, final String timeForMinuteWhenQueryIsRange,
      final List<IexHistoricalPrices> iexHistoricalPrices,
      final List<String> missingDates) {

    for (LocalDate date = startDate;
        date.isAfter(endDate);
        date = date.minusDays(1)) {

      HistoricalPriceId historicalPriceId =
          new HistoricalPriceId(symbols, date.toString(), timeForMinuteWhenQueryIsRange);

      if (historicalPricesService.exists(historicalPriceId)) {

        getIexHistoricalPricesObjectFromDatabase(historicalPriceId, iexHistoricalPrices);
      } else {
        missingDates.add(date.toString().replaceAll("-",""));
      }
    }

  }


  /**
   * Get HistoricalPrice from the database and add it to the list.
   *
   * @param historicalPriceId object to searched in the database
   * @param iexHistoricalPrices list for IexHistoricalPrices
   */
  private void getIexHistoricalPricesObjectFromDatabase(
      final HistoricalPriceId historicalPriceId,
      final List<IexHistoricalPrices> iexHistoricalPrices) {

    Optional<HistoricalPrice> historicalPricesDb =
        historicalPricesService.getHistoricalPriceFromDb(historicalPriceId);

    HistoricalPrice historicalPrice = historicalPricesDb.get();

    if (historicalPrice != null) {
      IexHistoricalPrices objIexHistoricalPrices = IexHistoricalPrices.builder()
          .close(historicalPrice.getClose()).date(historicalPrice.getDate())
          .high(historicalPrice.getHigh()).low(historicalPrice.getLow())
          .symbol(historicalPrice.getSymbol()).open(historicalPrice.getOpen())
          .volume(historicalPrice.getVolume()).minute(historicalPrice.getMinute())
          .build();

      iexHistoricalPrices.add(objIexHistoricalPrices);
    }
  }

}
