package org.galatea.starter.domain;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table
@IdClass(HistoricalPricesId.class)
public class HistoricalPricesDB {

  @Id
  @Column
  private String symbol;

  @Id
  @Column
  private String date;

  @Id
  @Column
  private String minute;

  private BigDecimal close;
  private BigDecimal high;
  private BigDecimal low;
  private BigDecimal open;
  private BigDecimal volume;

  /**
   * Default Constructor.
   */
  public HistoricalPricesDB() {
    //Does Nothing
  }

  /**
   * Constructor that initializes all field variables.
   *
   * @param symbol field symbol
   * @param date field date
   * @param close field close
   * @param high field high
   * @param low field low
   * @param open field open
   * @param volume field volume
   * @param minute field minute
   */
  public HistoricalPricesDB(final String symbol, final String date, final BigDecimal close,
      final BigDecimal high, final BigDecimal low, final BigDecimal open, final BigDecimal volume,
      final String minute) {
    this.symbol = symbol;
    this.date = date;
    this.close = close;
    this.high = high;
    this.low = low;
    this.open = open;
    this.volume = volume;
    this.minute = minute;
  }

  /**
   * Getter for symbol.
   *
   * @return symbol
   */
  public String getSymbol() {
    return symbol;
  }

  /**
   * Getter for date.
   *
   * @return date
   */
  public String getDate() {
    return date;
  }

  /**
   * Getter for close.
   *
   * @return close
   */
  public BigDecimal getClose() {
    return close;
  }

  /**
   * Getter for high.
   *
   * @return high
   */
  public BigDecimal getHigh() {
    return high;
  }

  /**
   * Getter for low.
   *
   * @return low
   */
  public BigDecimal getLow() {
    return low;
  }

  /**
   * Getter for open.
   *
   * @return open
   */
  public BigDecimal getOpen() {
    return open;
  }

  /**
   * Getter for open.
   *
   * @return open
   */
  public BigDecimal getVolume() {
    return volume;
  }

  /**
   * Getter for minute.
   *
   * @return minute
   */
  public String getMinute() {
    return minute;
  }

  /**
   * Setter for symbol.
   *
   * @param symbol field symbol
   */
  public void setSymbol(final String symbol) {
    this.symbol = symbol;
  }

  /**
   * Setter for date.
   *
   * @param date field date
   */
  public void setDate(final String date) {
    this.date = date;
  }

  /**
   * Setter for close.
   *
   * @param close field close
   */
  public void setDate(final BigDecimal close) {
    this.close = close;
  }

  /**
   * Setter for high.
   *
   * @param high field high
   */
  public void setHigh(final BigDecimal high) {
    this.high = high;
  }

  /**
   * Setter for low.
   *
   * @param low field low
   */
  public void setLow(final BigDecimal low) {
    this.low = low;
  }

  /**
   * Setter for open.
   *
   * @param open field open
   */
  public void setOpen(final BigDecimal open) {
    this.open = open;
  }

  /**
   * Setter for open.
   *
   * @param volume field volume
   */
  public void setVolume(final BigDecimal volume) {
    this.volume = volume;
  }

  /**
   * Setter for open.
   *
   * @param minute field minute
   */
  public void setMinute(final String minute) {
    this.minute = minute;
  }


}
