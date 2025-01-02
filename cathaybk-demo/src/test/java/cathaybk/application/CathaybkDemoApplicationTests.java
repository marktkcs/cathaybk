package cathaybk.application;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import bean.DailyForeignExchangeRatesRequest;
import bean.DailyForeignExchangeRatesResponse;
import schedule.dailyForeignExchangeRatesBatch;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CathaybkDemoApplicationTests {

  @Autowired
  private TestRestTemplate restTemplate;
  @Autowired
  private dailyForeignExchangeRatesBatch testDailyForeignExchangeRatesBatch;

  @Test
  public void testBatchAPI() {
    System.out.println(testDailyForeignExchangeRatesBatch.run());
  }


  @Test
  public void testForexAPI() throws JsonProcessingException {
	  DailyForeignExchangeRatesRequest  dailyForeignExchangeRatesRequest
	  =new DailyForeignExchangeRatesRequest();
      dailyForeignExchangeRatesRequest.setStartDate("2024/12/31");
      dailyForeignExchangeRatesRequest.setEndDate("2025/01/01");
      dailyForeignExchangeRatesRequest.setCurrency("usd");
      // 發送 POST 請求
      ResponseEntity<DailyForeignExchangeRatesResponse> response =
          restTemplate.postForEntity("/ForexDailyForeignExchangeRates",
              dailyForeignExchangeRatesRequest, DailyForeignExchangeRatesResponse.class);
      // 回應資料
      DailyForeignExchangeRatesResponse responseBody = response.getBody();
      ObjectMapper objectMapper = new ObjectMapper();
      System.out.println(objectMapper.writeValueAsString(responseBody));

	}

}
