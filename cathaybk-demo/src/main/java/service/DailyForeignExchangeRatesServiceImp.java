package service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import bean.DailyForeignExchangeRatesFailed;
import bean.DailyForeignExchangeRatesRequest;
import bean.DailyForeignExchangeRatesResponse;
import bean.DailyForeignExchangeRatesRrror;
import bean.DailyForeignExchangeRatesSuccess;
import entity.DailyForeignExchangeRatesEntity;
import entity.DailyForeignExchangeRatesNowDateEntity;
import repository.DailyForeignExchangeRatesRepository;

@Service
public class DailyForeignExchangeRatesServiceImp implements DailyForeignExchangeRatesI {

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private DailyForeignExchangeRatesRepository dailyForeignExchangeRatesRepository;


  @Override
  public boolean dataQuerySave(String date) throws IOException {
    HttpHeaders headers = new HttpHeaders();

    HttpEntity<String> requestEntity = new HttpEntity<>(headers);
    ResponseEntity<String> responseEntity =
        restTemplate.exchange("https://openapi.taifex.com.tw/v1/DailyForeignExchangeRates",
            HttpMethod.GET, requestEntity, String.class);
    String responseBody = responseEntity.getBody();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNodes = objectMapper.readTree(responseBody);
    List<DailyForeignExchangeRatesEntity> dailyForeignExchangeRatesEntityList = new ArrayList<>();
    for (JsonNode node : jsonNodes) {
      DailyForeignExchangeRatesEntity dailyForeignExchangeRatesEntity =
          new DailyForeignExchangeRatesEntity();
      dailyForeignExchangeRatesEntity.setDate(node.get("Date").asText());
      dailyForeignExchangeRatesEntity.setUsdToNtd(node.get("USD/NTD").asDouble());
      dailyForeignExchangeRatesEntityList.add(dailyForeignExchangeRatesEntity);
    }
    DailyForeignExchangeRatesNowDateEntity dailyForeignExchangeRatesNowDateEntity =
        new DailyForeignExchangeRatesNowDateEntity();
    Map<String, List<DailyForeignExchangeRatesEntity>> dailyForeignExchangeRatesmap =
        new HashMap<>();
    dailyForeignExchangeRatesmap.put(date, dailyForeignExchangeRatesEntityList);
    dailyForeignExchangeRatesNowDateEntity.setId(UUID.randomUUID().toString());
    dailyForeignExchangeRatesNowDateEntity.setRatesByDate(dailyForeignExchangeRatesmap);
    DailyForeignExchangeRatesNowDateEntity x =
        dailyForeignExchangeRatesRepository.save(dailyForeignExchangeRatesNowDateEntity);

    return true;
  }


  @Override
  public DailyForeignExchangeRatesResponse queryForex(
      DailyForeignExchangeRatesRequest dailyForeignExchangeRatesRequest) throws ParseException {

    DailyForeignExchangeRatesResponse dailyForeignExchangeRatesResponse =
        new DailyForeignExchangeRatesResponse();

    String requestStartDateTime = dailyForeignExchangeRatesRequest.getStartDate();
    String requestEndDateTime = dailyForeignExchangeRatesRequest.getEndDate();
    String requestCurrency = dailyForeignExchangeRatesRequest.getCurrency();

    if (requestCurrency.trim().length() > 3) {
      DailyForeignExchangeRatesRrror dailyForeignExchangeRatesRrror =
          new DailyForeignExchangeRatesRrror();
      dailyForeignExchangeRatesRrror.setCode("E002");
      dailyForeignExchangeRatesRrror.setMessage("一次只能查詢一種幣別");
      DailyForeignExchangeRatesFailed dailyForeignExchangeRatesFailed =
          new DailyForeignExchangeRatesFailed();
      dailyForeignExchangeRatesFailed.setError(dailyForeignExchangeRatesRrror);
      dailyForeignExchangeRatesResponse.setFailed(dailyForeignExchangeRatesFailed);
      dailyForeignExchangeRatesResponse.setSuccess(new DailyForeignExchangeRatesSuccess());
      return dailyForeignExchangeRatesResponse;
    }

    String[] requestStartDateTimeArray = requestStartDateTime.split("/");
    String[] requestEndDateTimeArray = requestEndDateTime.split("/");

    // 設定查詢的日期範圍
    LocalDateTime startDateTime = LocalDateTime.of(Integer.valueOf(requestStartDateTimeArray[0]),
        Integer.valueOf(requestStartDateTimeArray[1]),
        Integer.valueOf(requestStartDateTimeArray[2]), 0, 0, 0);
    LocalDateTime endDateTime = LocalDateTime.of(Integer.valueOf(requestEndDateTimeArray[0]),
        Integer.valueOf(requestEndDateTimeArray[1]), Integer.valueOf(requestEndDateTimeArray[2]),
        23, 59, 59, 999999999);

    // 判斷時間
    if (!dateVerify(startDateTime, endDateTime)) {
      DailyForeignExchangeRatesRrror dailyForeignExchangeRatesRrror =
          new DailyForeignExchangeRatesRrror();
      dailyForeignExchangeRatesRrror.setCode("E001");
      dailyForeignExchangeRatesRrror.setMessage("日期區間不符");
      DailyForeignExchangeRatesFailed dailyForeignExchangeRatesFailed =
          new DailyForeignExchangeRatesFailed();
      dailyForeignExchangeRatesFailed.setError(dailyForeignExchangeRatesRrror);
      dailyForeignExchangeRatesResponse.setFailed(dailyForeignExchangeRatesFailed);
      dailyForeignExchangeRatesResponse.setSuccess(new DailyForeignExchangeRatesSuccess());
      return dailyForeignExchangeRatesResponse;
    }


    Date startDate = new SimpleDateFormat("yyyyMMdd")
        .parse(DateTimeFormatter.ofPattern("yyyyMMdd").format(startDateTime));
    Date endDate = new SimpleDateFormat("yyyyMMdd")
        .parse(DateTimeFormatter.ofPattern("yyyyMMdd").format(endDateTime.minusDays(-1)));

    List<DailyForeignExchangeRatesNowDateEntity> dailyForeignExchangeRatesNowDateEntity =
        dailyForeignExchangeRatesRepository.findAll();
    List<Map<String, Object>> currency = new ArrayList<>();
    for (DailyForeignExchangeRatesNowDateEntity dailyForeign : dailyForeignExchangeRatesNowDateEntity) {
      for (Entry<String, List<DailyForeignExchangeRatesEntity>> enty : dailyForeign.getRatesByDate()
          .entrySet()) {
        Calendar date = Calendar.getInstance();
        Date d = new SimpleDateFormat("yyyy-MM-dd").parse(enty.getKey());
        date.setTime(d);

        Calendar startDateCalendar = Calendar.getInstance();
        startDateCalendar.setTime(startDate);
        Calendar endDateCalendar = Calendar.getInstance();
        endDateCalendar.setTime(endDate);

        if (date.after(startDateCalendar) && date.before(endDateCalendar)) {


          List<DailyForeignExchangeRatesEntity> Daily = enty.getValue();
          for (DailyForeignExchangeRatesEntity temp : Daily) {

            String tempDate = temp.getDate();
            Map<String, Object> usdToNtdMap = new HashMap<>();
            usdToNtdMap.put("date", tempDate);
            usdToNtdMap.put(requestCurrency, temp.getUsdToNtd());
            currency.add(usdToNtdMap);
          }


        }
      }
    }
    DailyForeignExchangeRatesSuccess dailyForeignExchangeRatesSuccess =
        new DailyForeignExchangeRatesSuccess();
    DailyForeignExchangeRatesRrror dailyForeignExchangeRatesRrror =
        new DailyForeignExchangeRatesRrror();
    dailyForeignExchangeRatesRrror.setCode("0000");
    dailyForeignExchangeRatesRrror.setMessage("成功");
    dailyForeignExchangeRatesSuccess.setCurrency(currency);
    dailyForeignExchangeRatesSuccess.setError(dailyForeignExchangeRatesRrror);
    dailyForeignExchangeRatesResponse.setSuccess(dailyForeignExchangeRatesSuccess);
    dailyForeignExchangeRatesResponse.setFailed(new DailyForeignExchangeRatesFailed());
    return dailyForeignExchangeRatesResponse;
  }

  /*
   * time Verify
   */
  private boolean dateVerify(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    if (startDateTime.toLocalDate().isAfter(endDateTime.toLocalDate()))
      return false;
    LocalDateTime beforeYearLocalTime = LocalDateTime.now().minusYears(1);
    LocalDateTime beforeDayLocalTime = LocalDateTime.now().minusDays(1);

    if (startDateTime.toLocalDate().isBefore(beforeYearLocalTime.toLocalDate())
        || beforeDayLocalTime.toLocalDate().isBefore(endDateTime.toLocalDate()))
      return false;
    return true;
  }



}
