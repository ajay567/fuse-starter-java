package org.galatea.starter.service;

import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.galatea.starter.domain.HistoricalPrice;
import org.galatea.starter.domain.HistoricalPriceId;
import org.galatea.starter.domain.rpsy.HistoricalPriceDbRepo;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistoricalPricesService {

  @NonNull
  private HistoricalPriceDbRepo historicalpricesDbRepo;

  /**
   * Get a particular HistoricalPrice entity from the database if id matches.
   *
   * @param historicalPriceId the id for the entity to be returned
   * @return HistoricalPrice objects
   */
  public Optional<HistoricalPrice> getHistoricalPriceFromDb(
      final HistoricalPriceId historicalPriceId) {
    return historicalpricesDbRepo.findById(historicalPriceId);
  }

  /**
   * Saves a HistoricalPrice entity into the database.
   *
   * @param historicalPrice the entity to be saved
   */
  public HistoricalPrice saveOrUpdate(final HistoricalPrice historicalPrice) {
    historicalpricesDbRepo.save(historicalPrice);

    return historicalPrice;
  }

  /**
   * Checks if a HistoricalPrice entity exists in the database based on the
   * HistoricalPriceId.
   *
   * @param historicalPriceId the id to searched in the database
   * @return true or false based on existence
   */
  public boolean exists(final HistoricalPriceId historicalPriceId) {

    return historicalpricesDbRepo.existsById(historicalPriceId);
  }

}
