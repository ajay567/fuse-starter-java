package org.galatea.starter.domain.rpsy;

import org.galatea.starter.domain.HistoricalPricesDB;
import org.galatea.starter.domain.HistoricalPricesId;
import org.springframework.data.repository.CrudRepository;

public interface HistoricalPricesDbRepo
    extends CrudRepository<HistoricalPricesDB, HistoricalPricesId> {
}
