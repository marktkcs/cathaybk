package repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import entity.DailyForeignExchangeRatesNowDateEntity;


public interface DailyForeignExchangeRatesRepository
    extends MongoRepository<DailyForeignExchangeRatesNowDateEntity, String> {

  // 查詢指定範圍內的匯率資料
  // List<DailyForeignExchangeRatesNowDateEntity> findByRatesByDateContainingKey(Date startDate);
}
