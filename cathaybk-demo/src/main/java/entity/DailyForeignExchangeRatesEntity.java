package entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Document(collection = "dailyForeignExchangeRates")
public class DailyForeignExchangeRatesEntity {



  private String Date;

  @Field("USD/NTD")
  private Double usdToNtd;

}
