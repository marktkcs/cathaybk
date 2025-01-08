package schedule;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import service.DailyForeignExchangeRatesI;

@Component
public class DailyForeignExchangeRatesBatch {
  private static final Logger logger =
      LoggerFactory.getLogger(DailyForeignExchangeRatesBatch.class);
  @Autowired
  private DailyForeignExchangeRatesI dailyForeignExchangeRatesServiceImp;

  @Scheduled(cron = "0 0 18 * * ?")
  public boolean run() {
    try {
      String date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
      return dailyForeignExchangeRatesServiceImp.dataQuerySave(date);
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
    return false;

  }
}
