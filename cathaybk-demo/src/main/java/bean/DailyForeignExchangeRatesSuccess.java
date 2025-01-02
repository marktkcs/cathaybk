package bean;

import java.util.List;
import org.bson.Document;

public class DailyForeignExchangeRatesSuccess {


  private List<Document> currency;

  private DailyForeignExchangeRatesRrror error;

  public List<Document> getCurrency() {
    return currency;
  }

  public void setCurrency(List<Document> currency) {
    this.currency = currency;
  }

  public DailyForeignExchangeRatesRrror getError() {
    return error;
  }

  public void setError(DailyForeignExchangeRatesRrror error) {
    this.error = error;
  }


}
