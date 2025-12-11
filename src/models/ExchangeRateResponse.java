package models;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class ExchangeRateResponse {
    @SerializedName("result")
    private String result;

    @SerializedName("base_code")
    private String baseCode;

    @SerializedName("time_last_update_utc")
    private String timeLastUpdateUtc;

    @SerializedName("conversion_rates")
    private Map<String, Double> conversionRates;

    // Getters
    public String getResult() { return result; }
    public String getBaseCode() { return baseCode; }
    public String getTimeLastUpdateUtc() { return timeLastUpdateUtc; }
    public Map<String, Double> getConversionRates() { return conversionRates; }

    // Método para pegar taxa específica
    public Double getRate(String currencyCode) {
        if (conversionRates == null) return null;
        return conversionRates.get(currencyCode.toUpperCase());
    }
}