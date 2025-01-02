package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import bean.DailyForeignExchangeRatesRequest;
import bean.DailyForeignExchangeRatesResponse;
import service.dailyForeignExchangeRatesI;

@RestController
public class dailyForeignExchangeRatesController {

  @Autowired
  private dailyForeignExchangeRatesI dailyForeignExchangeRatesServiceImp;


  @ResponseBody
  @PostMapping("/ForexDailyForeignExchangeRates")
  public ResponseEntity<DailyForeignExchangeRatesResponse> forex(
      @RequestBody DailyForeignExchangeRatesRequest dailyForeignExchangeRatesRequest) {
    return new ResponseEntity<DailyForeignExchangeRatesResponse>(dailyForeignExchangeRatesServiceImp
        .queryForex(dailyForeignExchangeRatesRequest, new Object()), HttpStatus.OK);
  }

}

