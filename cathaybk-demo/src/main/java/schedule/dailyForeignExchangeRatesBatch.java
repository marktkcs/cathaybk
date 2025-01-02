package schedule;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import service.dailyForeignExchangeRatesI;

@Component
public class dailyForeignExchangeRatesBatch {
  private static final Logger logger =
      LoggerFactory.getLogger(dailyForeignExchangeRatesBatch.class);
  @Autowired
  private dailyForeignExchangeRatesI dailyForeignExchangeRatesServiceImp;

  @Scheduled(cron = "0 0 18 * * ?")
  public boolean run() {
    try {
      String date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
          .format(LocalDateTime.now());
      return dailyForeignExchangeRatesServiceImp.dataQuerySave(date, new Object());
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
    return false;

  }
}
