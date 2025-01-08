package bean;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class DailyForeignExchangeRatesRequest {

  private String startDate;
  private String endDate;
  private String currency;


}
