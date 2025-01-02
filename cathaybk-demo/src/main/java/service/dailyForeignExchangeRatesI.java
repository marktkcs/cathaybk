package service;

import java.io.IOException;
import bean.DailyForeignExchangeRatesRequest;
import bean.DailyForeignExchangeRatesResponse;

public interface dailyForeignExchangeRatesI {


  public boolean dataQuerySave(String Date, Object Object) throws IOException;

  public DailyForeignExchangeRatesResponse queryForex(
      DailyForeignExchangeRatesRequest dailyForeignExchangeRatesRequest, Object Object);

}
