package org.galatea.starter.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.HistoricalPricesDB;
import org.galatea.starter.domain.HistoricalPricesId;
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

    String dateFormatted = date.substring(0,4) + "-" + date.substring(4, 6)
        + "-" + date.substring(6, 8);

    HistoricalPricesId historicalPricesId =
        new HistoricalPricesId(symbols, dateFormatted, "10:50");

    if (historicalPricesService.exists(historicalPricesId)) {

      List<IexHistoricalPrices> listIexHistoricalPrices = new ArrayList();

      for (LocalTime time = LocalTime.of(9,30);
          time.isBefore(LocalTime.of(16,1));
          time = time.plusMinutes(1)) {

        historicalPricesId =
            new HistoricalPricesId(symbols, dateFormatted, time.toString());

        if (historicalPricesService.exists(historicalPricesId)) {

          getIexHistoricalPricesObjectFromDatabaseHelper(historicalPricesId,
              listIexHistoricalPrices);
        }
      }

      return listIexHistoricalPrices;

    } else {
      // Enter data from new api requests into the database.

      List<IexHistoricalPrices> listIexHistoricalPricesApi =
          iexHistoricalClient.getHistoricalPricesForSymbols(symbols, range, date);

      saveNewApiRequestsFromIexToDbHelper(symbols, date, listIexHistoricalPricesApi);

      return listIexHistoricalPricesApi;
    }

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

    LocalDate startDate = LocalDate.now().minusDays(1);
    LocalDate endDate = null;

    if (range.equals("1d")) {
      return getHistoricalPricesForSymbols(symbols, range,
          startDate.toString().replace("-",""));
    } else {
      endDate = setEndDateHelper(range, startDate);
    }

    List<String> missingDates = new ArrayList();

    List<IexHistoricalPrices> listIexHistoricalPrices = new ArrayList();

    for (LocalDate date = startDate;
        date.isAfter(endDate);
        date = date.minusDays(1)) {

      HistoricalPricesId historicalPricesId =
          new HistoricalPricesId(symbols, date.toString(), "00:00");

      if (historicalPricesService.exists(historicalPricesId)) {

        getIexHistoricalPricesObjectFromDatabaseHelper(historicalPricesId,listIexHistoricalPrices);
      } else {
        missingDates.add(date.toString().replaceAll("-",""));
      }

    }

    if (listIexHistoricalPrices.isEmpty()) {

      List<IexHistoricalPrices> listIexHistoricalPricesApi =
          iexHistoricalClient.getHistoricalPricesForSymbols(symbols,range);
      saveNewApiRequestsFromIexToDbHelper(symbols, "NA", listIexHistoricalPricesApi);
      listIexHistoricalPrices.addAll(listIexHistoricalPricesApi);

    } else {

      for (String dates: missingDates) {

        List<IexHistoricalPrices> listIexHistoricalPricesApi =
            iexHistoricalClient.getHistoricalPricesForSymbolsChartByDay(symbols,"1m",
                dates);

        saveNewApiRequestsFromIexToDbHelper(symbols, "NA", listIexHistoricalPricesApi);
        listIexHistoricalPrices.addAll(listIexHistoricalPricesApi);
      }

    }

    return listIexHistoricalPrices;
  }

  /**
   * Get the historical prices for a Symbol that is passed in.
   *
   * @param symbols the list of symbols to get a historical prices for.
   * @return a list of historical price objects for each Symbol that is passed in.
   */
  public List<IexHistoricalPrices> getHistoricalPricesForSymbols(final String symbols) {

    return getHistoricalPricesForSymbols(symbols,"1m");
  }

  /**
   * Get HistoricalPricesDB from the database and add it to the list.
   *
   * @param historicalPricesId object to searched in the database
   * @param listIexHistoricalPrices list where object is to be added
   */
  public void getIexHistoricalPricesObjectFromDatabaseHelper(
      final HistoricalPricesId historicalPricesId,
      final List<IexHistoricalPrices> listIexHistoricalPrices) {

    Optional<HistoricalPricesDB> listHistoricalPricesDB =
        historicalPricesService.getHistoricalPriceFromDb(historicalPricesId);

    HistoricalPricesDB objHistoricalPricesDB = listHistoricalPricesDB.get();

    IexHistoricalPrices objIexHistoricalPrices = IexHistoricalPrices.builder().build();
    objIexHistoricalPrices.setClose(objHistoricalPricesDB.getClose());
    objIexHistoricalPrices.setDate(objHistoricalPricesDB.getDate());
    objIexHistoricalPrices.setHigh(objHistoricalPricesDB.getHigh());
    objIexHistoricalPrices.setLow(objHistoricalPricesDB.getLow());
    objIexHistoricalPrices.setSymbol(objHistoricalPricesDB.getSymbol());
    objIexHistoricalPrices.setOpen(objHistoricalPricesDB.getOpen());
    objIexHistoricalPrices.setVolume(objHistoricalPricesDB.getVolume());
    objIexHistoricalPrices.setMinute(objHistoricalPricesDB.getMinute());
    listIexHistoricalPrices.add(objIexHistoricalPrices);
  }

  /**
   * Save new api call data into the database.
   *
   * @param symbols stock symbol for which api call is made
   * @param date date for the historical prices
   * @param listIexHistoricalPricesApi list which has all the new data from the api calls
   */
  public void saveNewApiRequestsFromIexToDbHelper(
      final String symbols, final String date,
      final List<IexHistoricalPrices> listIexHistoricalPricesApi) {

    for (IexHistoricalPrices iexHistoricalPrices: listIexHistoricalPricesApi) {

      HistoricalPricesDB historicalPricesDB = new HistoricalPricesDB(
          symbols, iexHistoricalPrices.getDate(),
          iexHistoricalPrices.getClose(), iexHistoricalPrices.getHigh(),
          iexHistoricalPrices.getLow(), iexHistoricalPrices.getOpen(),
          iexHistoricalPrices.getVolume(),
          date.equals("NA") ? "00:00" : iexHistoricalPrices.getMinute());
      historicalPricesService.saveOrUpdate(historicalPricesDB);
    }
  }

  /**
   * Set the end date based on the range parameter provided.
   *
   * @param range range parameter
   * @param startDate the end date is counted down from the start date
   * @return the decided end date
   */
  public LocalDate setEndDateHelper(final String range, final LocalDate startDate) {

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

}
