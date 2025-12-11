package services;

import models.ExchangeRateResponse;
import java.util.Map;

public class CurrencyConverter {
    private ExchangeRateResponse exchangeRates;

    public CurrencyConverter(ExchangeRateResponse exchangeRates) {
        this.exchangeRates = exchangeRates;
    }

    // Método principal de conversão
    public double convert(double amount, String fromCurrency, String toCurrency) {
        // Se for mesma moeda, retorna o mesmo valor
        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            return amount;
        }

        Map<String, Double> rates = exchangeRates.getConversionRates();

        // Verificar se as moedas existem
        if (!rates.containsKey(fromCurrency.toUpperCase()) && !fromCurrency.equalsIgnoreCase("USD")) {
            throw new IllegalArgumentException("Moeda de origem não encontrada: " + fromCurrency);
        }
        if (!rates.containsKey(toCurrency.toUpperCase()) && !toCurrency.equalsIgnoreCase("USD")) {
            throw new IllegalArgumentException("Moeda de destino não encontrada: " + toCurrency);
        }

        // Primeiro converter para USD (moeda base)
        double amountInUSD;
        if (fromCurrency.equalsIgnoreCase("USD")) {
            amountInUSD = amount;
        } else {
            double rateFrom = rates.get(fromCurrency.toUpperCase());
            amountInUSD = amount / rateFrom;
        }

        // Depois converter de USD para moeda destino
        double result;
        if (toCurrency.equalsIgnoreCase("USD")) {
            result = amountInUSD;
        } else {
            double rateTo = rates.get(toCurrency.toUpperCase());
            result = amountInUSD * rateTo;
        }

        return result;
    }

    // Método para exibir taxas principais
    public void displayMainRates() {
        Map<String, Double> rates = exchangeRates.getConversionRates();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("💱 TAXAS DE CÂMBIO ATUAIS");
        System.out.println("Base: " + exchangeRates.getBaseCode());
        System.out.println("Atualizado: " + exchangeRates.getTimeLastUpdateUtc());
        System.out.println("=".repeat(60));

        // Moedas principais
        String[] mainCurrencies = {"BRL", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "ARS", "CLP"};

        for (String currency : mainCurrencies) {
            Double rate = rates.get(currency);
            if (rate != null) {
                System.out.printf("1 USD = %s %s%n",
                        currency.equals("JPY") ? String.format("¥ %.2f", rate) :
                                currency.equals("BRL") ? String.format("R$ %.4f", rate) :
                                        currency.equals("EUR") ? String.format("€ %.4f", rate) :
                                                currency.equals("GBP") ? String.format("£ %.4f", rate) :
                                                        currency.equals("ARS") || currency.equals("CLP") ? String.format("$ %.2f", rate) :
                                                                String.format("%.4f", rate), currency);
            }
        }
        System.out.println("=".repeat(60));
        System.out.println("📊 Total de moedas disponíveis: " + rates.size());
        System.out.println("💡 Use 'Conversão Personalizada' para qualquer moeda");
    }

    // Verificar se moeda existe
    public boolean currencyExists(String currencyCode) {
        Map<String, Double> rates = exchangeRates.getConversionRates();
        return rates.containsKey(currencyCode.toUpperCase()) ||
                currencyCode.equalsIgnoreCase("USD");
    }

    // Listar todas as moedas disponíveis
    public void listAllCurrencies() {
        Map<String, Double> rates = exchangeRates.getConversionRates();
        System.out.println("\n📋 TODAS AS MOEDAS DISPONÍVEIS (" + rates.size() + "):");
        System.out.println("-".repeat(40));

        int count = 0;
        for (String currency : rates.keySet()) {
            System.out.printf("%-6s", currency);
            count++;
            if (count % 8 == 0) System.out.println();
        }
        if (count % 8 != 0) System.out.println();
        System.out.println("-".repeat(40));
    }

    // Getter para exchangeRates (para o Menu acessar) - ADICIONADO
    public ExchangeRateResponse getExchangeRates() {
        return exchangeRates;
    }
}