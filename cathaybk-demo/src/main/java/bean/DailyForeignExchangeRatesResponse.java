package bean;

public class DailyForeignExchangeRatesResponse {

  private DailyForeignExchangeRatesSuccess Success;

  private DailyForeignExchangeRatesFailed Failed;

  public DailyForeignExchangeRatesSuccess getSuccess() {
    return Success;
  }

  public void setSuccess(DailyForeignExchangeRatesSuccess success) {
    Success = success;
  }

  public DailyForeignExchangeRatesFailed getFailed() {
    return Failed;
  }

  public void setFailed(DailyForeignExchangeRatesFailed failed) {
    Failed = failed;
  }

}
