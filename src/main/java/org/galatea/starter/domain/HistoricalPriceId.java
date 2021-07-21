package org.galatea.starter.domain;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class HistoricalPriceId implements Serializable {

  private String symbol;

  private String date;

  private String minute;
}
