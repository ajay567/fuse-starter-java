package org.galatea.starter.service;

import java.util.Optional;
import org.galatea.starter.domain.HistoricalPricesDB;
import org.galatea.starter.domain.HistoricalPricesId;
import org.galatea.starter.domain.rpsy.HistoricalPricesDbRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistoricalPricesService {

  @Autowired
  HistoricalPricesDbRepo historicalpricesDbRepo;

  /**
   *
   * @param obj
   * @return
   */
  public Optional<HistoricalPricesDB> getPrices(HistoricalPricesId obj) {
    return historicalpricesDbRepo.findById(obj);
  }

  /**
   *
   * @param historicalPricesDB
   */
  public void save(HistoricalPricesDB historicalPricesDB) {
    historicalpricesDbRepo.save(historicalPricesDB);
  }

  /**
   *
   * @param historicalPricesId
   * @return
   */
  public boolean exists(HistoricalPricesId historicalPricesId) {
    return historicalpricesDbRepo.existsById(historicalPricesId);
  }

}
