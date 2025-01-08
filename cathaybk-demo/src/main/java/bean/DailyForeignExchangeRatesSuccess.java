package bean;

import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Setter
public class DailyForeignExchangeRatesSuccess {


  private List<Map<String, Object>> currency;

  private DailyForeignExchangeRatesRrror error;


}
