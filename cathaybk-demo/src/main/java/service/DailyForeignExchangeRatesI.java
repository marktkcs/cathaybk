package service;

import java.io.IOException;
import java.text.ParseException;
import bean.DailyForeignExchangeRatesRequest;
import bean.DailyForeignExchangeRatesResponse;

public interface DailyForeignExchangeRatesI {


  public boolean dataQuerySave(String Date) throws IOException;

  public DailyForeignExchangeRatesResponse queryForex(
      DailyForeignExchangeRatesRequest dailyForeignExchangeRatesRequest) throws ParseException;

}
