package org.galatea.starter.domain.rpsy;

import org.galatea.starter.domain.HistoricalPrice;
import org.galatea.starter.domain.HistoricalPriceId;
import org.springframework.data.repository.CrudRepository;

public interface HistoricalPriceDbRepo
    extends CrudRepository<HistoricalPrice, HistoricalPriceId> {
}
