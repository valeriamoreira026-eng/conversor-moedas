package services;

import models.ExchangeRateResponse;
import com.google.gson.Gson;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.util.Scanner;

public class ExchangeRateService {
    private static final String API_KEY = "8bf9e427ac08fa55541d8245";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/";

    public ExchangeRateResponse getExchangeRates(String baseCurrency) throws Exception {
        String urlString = BASE_URL + API_KEY + "/latest/" + baseCurrency;

        // Criar conexão HTTP
        URL url = new URI(urlString).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.connect();

        // Verificar resposta
        int responseCode = conn.getResponseCode();

        if (responseCode != 200) {
            String errorMessage = "Erro na API: Código " + responseCode;
            if (responseCode == 404) errorMessage += " - Chave inválida ou endpoint errado";
            if (responseCode == 429) errorMessage += " - Limite de requisições excedido";
            throw new RuntimeException(errorMessage);
        }

        // Ler resposta
        StringBuilder response = new StringBuilder();
        Scanner scanner = new Scanner(url.openStream());
        while (scanner.hasNext()) {
            response.append(scanner.nextLine());
        }
        scanner.close();
        conn.disconnect();

        // Converter JSON para objeto
        Gson gson = new Gson();
        return gson.fromJson(response.toString(), ExchangeRateResponse.class);
    }

    // Método para testar sem chave (com dados fictícios)
    public ExchangeRateResponse getMockExchangeRates() {
        String mockJson = """
            {
                "result": "success",
                "base_code": "USD",
                "time_last_update_utc": "Fri, 15 Feb 2024 00:00:01 +0000",
                "conversion_rates": {
                    "USD": 1.0,
                    "BRL": 5.07,
                    "EUR": 0.92,
                    "GBP": 0.79,
                    "JPY": 150.25,
                    "ARS": 845.50,
                    "CLP": 950.30,
                    "CAD": 1.35,
                    "AUD": 1.52,
                    "CHF": 0.88,
                    "CNY": 7.20
                }
            }
            """;

        Gson gson = new Gson();
        return gson.fromJson(mockJson, ExchangeRateResponse.class);
    }
}