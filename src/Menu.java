import services.ExchangeRateService;
import services.CurrencyConverter;
import models.ExchangeRateResponse;
import java.util.Scanner;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Menu {
    private Scanner scanner;
    private CurrencyConverter converter;
    private DecimalFormat df;

    public Menu() {
        this.scanner = new Scanner(System.in);
        // Formato universal (sempre usar ponto como decimal)
        this.df = new DecimalFormat("#,##0.00");
        this.df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
    }

    public void iniciar() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("         🏦 CONVERSOR DE MOEDAS INTERNACIONAL 🏦");
        System.out.println("=".repeat(60));

        try {
            // Carregar taxas da API
            System.out.println("\n📡 Conectando ao serviço de câmbio...");
            ExchangeRateService apiService = new ExchangeRateService();
            ExchangeRateResponse rates = apiService.getExchangeRates("USD");

            this.converter = new CurrencyConverter(rates);
            System.out.println("✅ Taxas carregadas com sucesso!");

            // Mostrar taxas atuais
            converter.displayMainRates();

            // Menu principal
            int opcao;
            do {
                exibirMenu();
                opcao = scanner.nextInt();
                scanner.nextLine(); // Limpar buffer

                switch (opcao) {
                    case 1 -> converterRealParaDolar();
                    case 2 -> converterDolarParaReal();
                    case 3 -> converterRealParaEuro();
                    case 4 -> converterEuroParaReal();
                    case 5 -> converterDolarParaEuro();
                    case 6 -> converterEuroParaDolar();
                    case 7 -> converterPersonalizado();
                    case 8 -> converter.displayMainRates();
                    case 0 -> System.out.println("\n👋 Encerrando o programa...");
                    default -> System.out.println("\n❌ Opção inválida! Tente novamente.");
                }

                if (opcao != 0) {
                    System.out.println("\nPressione Enter para continuar...");
                    scanner.nextLine();
                }

            } while (opcao != 0);

        } catch (Exception e) {
            System.out.println("\n❌ Erro ao carregar taxas: " + e.getMessage());
            System.out.println("Usando dados de exemplo...");

            // Fallback para dados mock
            ExchangeRateService apiService = new ExchangeRateService();
            ExchangeRateResponse mockRates = apiService.getMockExchangeRates();
            this.converter = new CurrencyConverter(mockRates);

            System.out.println("✅ Dados de exemplo carregados.");
            converter.displayMainRates();

            // Continuar com menu mesmo com dados mock
            converterPersonalizado();
        }

        scanner.close();
    }

    private void exibirMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📊 MENU DE CONVERSÃO");
        System.out.println("=".repeat(50));
        System.out.println("1. Real Brasileiro (BRL) → Dólar Americano (USD)");
        System.out.println("2. Dólar Americano (USD) → Real Brasileiro (BRL)");
        System.out.println("3. Real Brasileiro (BRL) → Euro (EUR)");
        System.out.println("4. Euro (EUR) → Real Brasileiro (BRL)");
        System.out.println("5. Dólar Americano (USD) → Euro (EUR)");
        System.out.println("6. Euro (EUR) → Dólar Americano (USD)");
        System.out.println("7. Conversão Personalizada (qualquer moeda)");
        System.out.println("8. Mostrar taxas atuais");
        System.out.println("0. Sair");
        System.out.println("=".repeat(50));
        System.out.print("👉 Escolha uma opção: ");
    }

    private void converterRealParaDolar() {
        System.out.print("\n💰 Digite o valor em Reais (BRL): R$ ");
        double valor = scanner.nextDouble();
        double convertido = converter.convert(valor, "BRL", "USD");
        System.out.printf("💵 Valor convertido: $ %s USD%n", df.format(convertido));
    }

    private void converterDolarParaReal() {
        System.out.print("\n💵 Digite o valor em Dólares (USD): $ ");
        double valor = scanner.nextDouble();
        double convertido = converter.convert(valor, "USD", "BRL");
        System.out.printf("💰 Valor convertido: R$ %s BRL%n", df.format(convertido));
    }

    private void converterRealParaEuro() {
        System.out.print("\n💰 Digite o valor em Reais (BRL): R$ ");
        double valor = scanner.nextDouble();
        double convertido = converter.convert(valor, "BRL", "EUR");
        System.out.printf("💶 Valor convertido: € %s EUR%n", df.format(convertido));
    }

    private void converterEuroParaReal() {
        System.out.print("\n💶 Digite o valor em Euros (EUR): € ");
        double valor = scanner.nextDouble();
        double convertido = converter.convert(valor, "EUR", "BRL");
        System.out.printf("💰 Valor convertido: R$ %s BRL%n", df.format(convertido));
    }

    private void converterDolarParaEuro() {
        System.out.print("\n💵 Digite o valor em Dólares (USD): $ ");
        double valor = scanner.nextDouble();
        double convertido = converter.convert(valor, "USD", "EUR");
        System.out.printf("💶 Valor convertido: € %s EUR%n", df.format(convertido));
    }

    private void converterEuroParaDolar() {
        System.out.print("\n💶 Digite o valor em Euros (EUR): € ");
        double valor = scanner.nextDouble();
        double convertido = converter.convert(valor, "EUR", "USD");
        System.out.printf("💵 Valor convertido: $ %s USD%n", df.format(convertido));
    }

    private void converterPersonalizado() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🔄 CONVERSÃO PERSONALIZADA");
        System.out.println("=".repeat(60));

        // Opção para ver todas as moedas
        System.out.print("Deseja ver todas as moedas disponíveis? (s/n): ");
        String verTodas = scanner.next();
        if (verTodas.equalsIgnoreCase("s")) {
            converter.listAllCurrencies();
        }

        System.out.println("\n💎 Moedas principais: USD, BRL, EUR, GBP, JPY, CAD, AUD, CHF, CNY, ARS, CLP");

        boolean moedaValida = false;
        String origem = "";
        String destino = "";

        while (!moedaValida) {
            System.out.print("\nDigite a moeda de ORIGEM (ex: BRL): ");
            origem = scanner.next().toUpperCase();

            System.out.print("Digite a moeda de DESTINO (ex: USD): ");
            destino = scanner.next().toUpperCase();

            if (converter.currencyExists(origem) && converter.currencyExists(destino)) {
                moedaValida = true;
            } else {
                System.out.println("❌ Uma ou ambas as moedas não são válidas!");
                System.out.println("💡 Moedas devem estar no formato de 3 letras (ex: USD, BRL, EUR)");
            }
        }

        System.out.printf("Digite o valor em %s: ", origem);
        double valor = scanner.nextDouble();

        try {
            double convertido = converter.convert(valor, origem, destino);
            System.out.println("\n" + "=".repeat(60));
            System.out.println("🎯 RESULTADO DA CONVERSÃO:");
            System.out.println("=".repeat(60));
            System.out.printf("💰 %s %s = %s %s%n",
                    df.format(valor), origem,
                    df.format(convertido), destino);

            // Mostrar taxa atual (CORRIGIDO - usando getter)
            if (!origem.equals("USD")) {
                Double taxaOrigem = converter.getExchangeRates().getRate(origem);
                if (taxaOrigem != null) {
                    System.out.printf("📈 1 %s = $ %.6f USD%n", origem, 1/taxaOrigem);
                }
            }
            if (!destino.equals("USD")) {
                Double taxaDestino = converter.getExchangeRates().getRate(destino);
                if (taxaDestino != null) {
                    System.out.printf("📈 1 USD = %s %.6f%n", destino, taxaDestino);
                }
            }
            System.out.println("=".repeat(60));
        } catch (Exception e) {
            System.out.println("\n❌ Erro na conversão: " + e.getMessage());
        }
    }
}