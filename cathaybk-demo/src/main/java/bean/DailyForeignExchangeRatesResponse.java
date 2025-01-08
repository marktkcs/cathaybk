package bean;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class DailyForeignExchangeRatesResponse {

  private DailyForeignExchangeRatesSuccess Success;

  private DailyForeignExchangeRatesFailed Failed;



}
