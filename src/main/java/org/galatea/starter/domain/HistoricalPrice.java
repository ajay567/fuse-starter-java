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
public class HistoricalPrice {

  @Id
  @Column
  @Getter
  @Setter
  private String symbol;

  @Id
  @Column
  @Getter
  @Setter
  private String date;

  @Id
  @Column
  @Getter
  @Setter
  private String minute;

  @Getter
  @Setter
  private BigDecimal close;

  @Getter
  @Setter
  private BigDecimal high;

  @Getter
  @Setter
  private BigDecimal low;

  @Getter
  @Setter
  private BigDecimal open;

  @Getter
  @Setter
  private BigDecimal volume;

}
