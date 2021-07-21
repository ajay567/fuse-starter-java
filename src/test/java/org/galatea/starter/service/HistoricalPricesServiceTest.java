package org.galatea.starter.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.HistoricalPricesDB;
import org.galatea.starter.domain.HistoricalPricesId;
import org.galatea.starter.domain.rpsy.HistoricalPricesDbRepo;
import org.galatea.starter.testutils.TestDataGenerator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

public class HistoricalPricesServiceTest extends ASpringTest {

  @MockBean
  private HistoricalPricesDbRepo historicalPricesDbRepo;

  private HistoricalPricesService historicalPricesService;

  @Before
  public void setup() {
    historicalPricesService = new HistoricalPricesService(historicalPricesDbRepo);
  }


  @Test
  public void testGetHistoricalPriceFromDbFound() {
    String symbol = "MSFT";
    String date = "21072021";
    String minute = "10:08";

    HistoricalPricesDB historicalPricesDB = TestDataGenerator.historicalPriceData();
    HistoricalPricesId historicalPricesId = new HistoricalPricesId(symbol, date, minute);

    given(this.historicalPricesDbRepo.findById(historicalPricesId)).
        willReturn(Optional.of(historicalPricesDB));

    Optional<HistoricalPricesDB> historicalPricesDbObject =
        historicalPricesService.getHistoricalPriceFromDb(historicalPricesId);

    assertTrue(historicalPricesDbObject.isPresent());
  }


  @Test
  public void testGetHistoricalPriceFromDbNotFound() {
    String symbol = "MSFT";
    String date = "21072021";
    String minute = "10:08";

    HistoricalPricesDB historicalPricesDB = TestDataGenerator.historicalPriceData();
    HistoricalPricesId historicalPricesId = new HistoricalPricesId(symbol, date, minute);

    given(this.historicalPricesDbRepo.findById(historicalPricesId)).
        willReturn(Optional.of(historicalPricesDB));

    Optional<HistoricalPricesDB> historicalPricesDbObject =
        historicalPricesService.getHistoricalPriceFromDb(new HistoricalPricesId("MSFT",
            "1",
            "2"));

    assertFalse(historicalPricesDbObject.isPresent());
  }


  @Test
  public void testSaveOrUpdate() {
    HistoricalPricesDB historicalPricesDB = TestDataGenerator.historicalPriceData();

    given(this.historicalPricesDbRepo.save(historicalPricesDB)).willReturn(historicalPricesDB);

    assertNotNull(historicalPricesService.saveOrUpdate(historicalPricesDB));
  }


  @Test
  public void testExistsTrue() {
    String symbol = "MSFT";
    String date = "21072021";
    String minute = "10:08";

    HistoricalPricesId historicalPricesId = new HistoricalPricesId(symbol, date, minute);

    given(this.historicalPricesDbRepo.existsById(historicalPricesId)).willReturn(true);

    assertTrue(historicalPricesService.exists(historicalPricesId));
  }


  @Test
  public void testExistsFalse() {
    String symbol = "MSFT";
    String date = "21072021";
    String minute = "10:08";

    HistoricalPricesId historicalPricesId = new HistoricalPricesId(symbol, date, minute);

    given(this.historicalPricesDbRepo.existsById(historicalPricesId)).willReturn(true);

    assertFalse(historicalPricesService.exists(new HistoricalPricesId(
        "MSFT",
        "1",
        "1")));
  }
}
