package org.galatea.starter.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.HistoricalPrice;
import org.galatea.starter.domain.HistoricalPriceId;
import org.galatea.starter.domain.rpsy.HistoricalPriceDbRepo;
import org.galatea.starter.testutils.TestDataGenerator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

public class HistoricalPricesServiceTest extends ASpringTest {

  @MockBean
  private HistoricalPriceDbRepo historicalPriceDbRepo;

  private HistoricalPricesService historicalPricesService;

  @Before
  public void setup() {
    historicalPricesService = new HistoricalPricesService(historicalPriceDbRepo);
  }


  @Test
  public void testGetHistoricalPriceFromDbFound() {
    String symbol = "MSFT";
    String date = "21072021";
    String minute = "10:08";

    HistoricalPrice historicalPrice = TestDataGenerator.historicalPriceData();
    HistoricalPriceId historicalPriceId = new HistoricalPriceId(symbol, date, minute);

    given(this.historicalPriceDbRepo.findById(historicalPriceId)).
        willReturn(Optional.of(historicalPrice));

    Optional<HistoricalPrice> historicalPricesDbObject =
        historicalPricesService.getHistoricalPriceFromDb(historicalPriceId);

    assertTrue(historicalPricesDbObject.isPresent());
  }


  @Test
  public void testGetHistoricalPriceFromDbNotFound() {
    String symbol = "MSFT";
    String date = "21072021";
    String minute = "10:08";

    HistoricalPrice historicalPrice = TestDataGenerator.historicalPriceData();
    HistoricalPriceId historicalPriceId = new HistoricalPriceId(symbol, date, minute);

    given(this.historicalPriceDbRepo.findById(historicalPriceId)).
        willReturn(Optional.of(historicalPrice));

    Optional<HistoricalPrice> historicalPricesDbObject =
        historicalPricesService.getHistoricalPriceFromDb(new HistoricalPriceId("MSFT",
            "1",
            "2"));

    assertFalse(historicalPricesDbObject.isPresent());
  }


  @Test
  public void testSaveOrUpdate() {
    HistoricalPrice historicalPrice = TestDataGenerator.historicalPriceData();

    given(this.historicalPriceDbRepo.save(historicalPrice)).willReturn(historicalPrice);

    assertNotNull(historicalPricesService.saveOrUpdate(historicalPrice));
  }


  @Test
  public void testExistsTrue() {
    String symbol = "MSFT";
    String date = "21072021";
    String minute = "10:08";

    HistoricalPriceId historicalPriceId = new HistoricalPriceId(symbol, date, minute);

    given(this.historicalPriceDbRepo.existsById(historicalPriceId)).willReturn(true);

    assertTrue(historicalPricesService.exists(historicalPriceId));
  }


  @Test
  public void testExistsFalse() {
    String symbol = "MSFT";
    String date = "21072021";
    String minute = "10:08";

    HistoricalPriceId historicalPriceId = new HistoricalPriceId(symbol, date, minute);

    given(this.historicalPriceDbRepo.existsById(historicalPriceId)).willReturn(true);

    assertFalse(historicalPricesService.exists(new HistoricalPriceId(
        "MSFT",
        "1",
        "1")));
  }
}
