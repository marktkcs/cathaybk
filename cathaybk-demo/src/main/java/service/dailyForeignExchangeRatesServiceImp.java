package service;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import bean.DailyForeignExchangeRatesFailed;
import bean.DailyForeignExchangeRatesRequest;
import bean.DailyForeignExchangeRatesResponse;
import bean.DailyForeignExchangeRatesRrror;
import bean.DailyForeignExchangeRatesSuccess;

@Service
public class dailyForeignExchangeRatesServiceImp implements dailyForeignExchangeRatesI {

  @Autowired
  private RestTemplate restTemplate;

  @Override
  public boolean dataQuerySave(String Date, Object mongoDatabase) throws IOException {
    HttpHeaders headers = new HttpHeaders();

    HttpEntity<String> requestEntity = new HttpEntity<>(headers);
    ResponseEntity<String> responseEntity =
        restTemplate.exchange("https://openapi.taifex.com.tw/v1/DailyForeignExchangeRates",
            HttpMethod.GET, requestEntity, String.class);
    String responseBody = responseEntity.getBody();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNodes = objectMapper.readTree(responseBody);
    BsonArray bsonArray = new BsonArray();

    for (JsonNode node : jsonNodes) {
      BsonDocument document = new BsonDocument();
      document.append("Date", new BsonString(node.get("Date").asText()));
      document.append("USD/NTD", new BsonDouble(node.get("USD/NTD").asDouble()));
      bsonArray.add(document);

    }
    Document document = new Document(Date, bsonArray);

    MongoCollection<Document> collection =
        ((MongoDatabase) mongoDatabase).getCollection("dailyForeignExchangeRates");

    collection.insertOne(document);

    return true;
  }


  @Override
  public DailyForeignExchangeRatesResponse queryForex(
      DailyForeignExchangeRatesRequest dailyForeignExchangeRatesRequest, Object mongoDatabase) {

    DailyForeignExchangeRatesResponse dailyForeignExchangeRatesResponse
    =new DailyForeignExchangeRatesResponse();
    
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
      DailyForeignExchangeRatesRrror 
      dailyForeignExchangeRatesRrror=new DailyForeignExchangeRatesRrror();
      dailyForeignExchangeRatesRrror.setCode("E001");
      dailyForeignExchangeRatesRrror.setMessage("日期區間不符");
      DailyForeignExchangeRatesFailed dailyForeignExchangeRatesFailed
     =new DailyForeignExchangeRatesFailed();
      dailyForeignExchangeRatesFailed.setError(dailyForeignExchangeRatesRrror);
      dailyForeignExchangeRatesResponse.setFailed(dailyForeignExchangeRatesFailed);
      dailyForeignExchangeRatesResponse.setSuccess(new DailyForeignExchangeRatesSuccess());
      return dailyForeignExchangeRatesResponse;
    }

    String startDate = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(startDateTime);
    String endDate = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(endDateTime.minusDays(-1));

    MongoCollection<Document> collection =
        ((MongoDatabase) mongoDatabase).getCollection("dailyForeignExchangeRates");


    // 聚合管道：將文件轉換為陣列並篩選 key 範圍
    Bson projectStage =
        project(new Document("keyValues", new Document("$objectToArray", "$$ROOT")));
    Bson matchStage = match(and(gte("keyValues.k", startDate), lte("keyValues.k", endDate)));

    // 執行聚合
    MongoCursor<Document> cursor =
        collection.aggregate(List.of(projectStage, matchStage)).iterator();
    List<Document> results = new ArrayList<>();
    // 輸出符合條件的文檔
    while (cursor.hasNext()) {
      Document documents = cursor.next();
      List<Document> tags = documents.getList("keyValues", Document.class);
      Document document = tags.get(1);
      List<Document> documentNestList = document.getList("v", Document.class);
      List<Document> newDocumentList = new ArrayList<>();
      for (Document q : documentNestList) {

        if (requestCurrency.trim().toUpperCase().contains("USD")
            || requestCurrency.trim().toUpperCase().contains("NTD")) {
          Document d = new Document();
          d.append("date", q.getString("Date"));
        d.append(requestCurrency, String.valueOf(q.getDouble("USD/NTD")));
          newDocumentList.add(d);
        }
      }
      results.addAll(newDocumentList);
    }
    DailyForeignExchangeRatesSuccess dailyForeignExchangeRatesSuccess =
        new DailyForeignExchangeRatesSuccess();
    DailyForeignExchangeRatesRrror dailyForeignExchangeRatesRrror =
        new DailyForeignExchangeRatesRrror();
    dailyForeignExchangeRatesRrror.setCode("0000");
    dailyForeignExchangeRatesRrror.setMessage("成功");
    dailyForeignExchangeRatesSuccess.setCurrency(results);
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
