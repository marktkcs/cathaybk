package controller;

import java.text.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import bean.DailyForeignExchangeRatesRequest;
import bean.DailyForeignExchangeRatesResponse;
import service.DailyForeignExchangeRatesI;

@RestController
public class DailyForeignExchangeRatesController {
  private static final Logger logger =
      LoggerFactory.getLogger(DailyForeignExchangeRatesController.class);

  @Autowired
  private DailyForeignExchangeRatesI dailyForeignExchangeRatesServiceImp;


  @ResponseBody
  @PostMapping("/ForexDailyForeignExchangeRates")
  public ResponseEntity<DailyForeignExchangeRatesResponse> forex(
      @RequestBody DailyForeignExchangeRatesRequest dailyForeignExchangeRatesRequest) {
    try {
      return new ResponseEntity<DailyForeignExchangeRatesResponse>(
          dailyForeignExchangeRatesServiceImp.queryForex(dailyForeignExchangeRatesRequest),
          HttpStatus.OK);
    } catch (ParseException e) {
      logger.error(e.getMessage());
      ResponseEntity<DailyForeignExchangeRatesResponse> results =
          new ResponseEntity<DailyForeignExchangeRatesResponse>(HttpStatus.SERVICE_UNAVAILABLE);
      return results;
    }
  }

}

