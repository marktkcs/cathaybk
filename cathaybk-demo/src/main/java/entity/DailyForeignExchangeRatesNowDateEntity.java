package entity;

import java.util.List;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Document(collection = "dailyForeignExchangeRates")
public class DailyForeignExchangeRatesNowDateEntity {

  @Id
  private String id;


  private Map<String, List<DailyForeignExchangeRatesEntity>> ratesByDate;
}
