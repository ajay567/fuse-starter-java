package org.galatea.starter.domain;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@IdClass(HistoricalPriceId.class)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HistoricalPrice {

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

}
