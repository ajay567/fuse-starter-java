package org.galatea.starter.domain;

import java.io.Serializable;

public class HistoricalPricesId implements Serializable {

  private String symbol;

  private String date;

  private String minute;

  /**
   * Default constructor.
   */
  public HistoricalPricesId() {
    //Does Nothing
  }


  /**
   * Constructor that initializes all field variables.
   *
   * @param symbol field symbol
   * @param date field date
   * @param minute field minute
   */
  public HistoricalPricesId(final String symbol, final String date, final String minute) {
    this.symbol = symbol;
    this.date = date;
    this.minute = minute;
  }
}
