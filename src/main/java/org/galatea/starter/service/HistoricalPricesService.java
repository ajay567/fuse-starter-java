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
   * Get a particular HistoricalPricesDB entity from the database if id matches.
   *
   * @param historicalPricesId the id for the entity to be returned
   * @return HistoricalPricesDB objects
   */
  public Optional<HistoricalPricesDB> getHistoricalPriceFromDb(final HistoricalPricesId
      historicalPricesId) {
    return historicalpricesDbRepo.findById(historicalPricesId);
  }

  /**
   * Saves a HistoricalPricesDB entity into the database.
   *
   * @param historicalPricesDB the entity to be saved
   */
  public void saveOrUpdate(final HistoricalPricesDB historicalPricesDB) {
    historicalpricesDbRepo.save(historicalPricesDB);
  }

  /**
   * Checks if a HistoricalPricesDB entity exists in the database based on the
   * HistoricalPricesId.
   *
   * @param historicalPricesId the id to searched in the database
   * @return true or false based on existence
   */
  public boolean exists(final HistoricalPricesId historicalPricesId) {

    return historicalpricesDbRepo.existsById(historicalPricesId);
  }

}
