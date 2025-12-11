import services.ExchangeRateService;
import services.CurrencyConverter;
import models.ExchangeRateResponse;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class ConversorGUI extends JFrame {
    private CurrencyConverter converter;
    private ExchangeRateResponse exchangeRates;
    private DecimalFormat df;

    // Componentes da interface
    private JComboBox<String> fromComboBox;
    private JComboBox<String> toComboBox;
    private JTextField amountField;
    private JLabel resultLabel;
    private JLabel rateLabel;
    private JTextArea ratesArea;

    public ConversorGUI() {
        // Configuração da janela
        setTitle("💱 Conversor de Moedas Internacional");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Formato de números
        df = new DecimalFormat("#,##0.00");
        df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));

        // Carregar dados
        carregarDados();

        // Criar componentes
        criarComponentes();

        // Posicionar componentes
        posicionarComponentes();

        setLocationRelativeTo(null); // Centralizar na tela
        setVisible(true);
    }

    private void carregarDados() {
        try {
            ExchangeRateService apiService = new ExchangeRateService();
            exchangeRates = apiService.getExchangeRates("USD");
            converter = new CurrencyConverter(exchangeRates);

            JOptionPane.showMessageDialog(this,
                    "✅ Taxas carregadas com sucesso!\n" +
                            "📊 " + exchangeRates.getConversionRates().size() + " moedas disponíveis\n" +
                            "📅 Atualizado: " + exchangeRates.getTimeLastUpdateUtc(),
                    "Conexão Bem-Sucedida",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "❌ Erro ao conectar com a API: " + e.getMessage() +
                            "\n⚠️ Usando dados de exemplo...",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);

            ExchangeRateService apiService = new ExchangeRateService();
            exchangeRates = apiService.getMockExchangeRates();
            converter = new CurrencyConverter(exchangeRates);
        }
    }

    private void criarComponentes() {
        // Painel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Painel superior (título)
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("🏦 CONVERSOR DE MOEDAS INTERNACIONAL");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 100, 0));
        titlePanel.add(titleLabel);

        // Painel de conversão
        JPanel conversionPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        conversionPanel.setBorder(BorderFactory.createTitledBorder("🔄 Conversão"));

        // Moedas disponíveis (ordenadas)
        Map<String, Double> rates = exchangeRates.getConversionRates();
        TreeMap<String, Double> sortedRates = new TreeMap<>(rates);
        String[] currencies = sortedRates.keySet().toArray(new String[0]);

        // Adicionar USD no início
        String[] allCurrencies = new String[currencies.length + 1];
        allCurrencies[0] = "USD";
        System.arraycopy(currencies, 0, allCurrencies, 1, currencies.length);

        fromComboBox = new JComboBox<>(allCurrencies);
        toComboBox = new JComboBox<>(allCurrencies);
        toComboBox.setSelectedItem("BRL"); // Padrão: USD → BRL

        amountField = new JTextField("100.00");
        amountField.setFont(new Font("Arial", Font.PLAIN, 16));

        JButton convertButton = new JButton("🔄 Converter");
        convertButton.setBackground(new Color(70, 130, 180));
        convertButton.setForeground(Color.WHITE);
        convertButton.setFont(new Font("Arial", Font.BOLD, 14));
        convertButton.addActionListener(e -> realizarConversao());

        resultLabel = new JLabel("Resultado: ");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 18));
        resultLabel.setForeground(new Color(0, 100, 0));

        rateLabel = new JLabel("Taxa: ");
        rateLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        // Adicionar componentes ao painel de conversão
        conversionPanel.add(new JLabel("De:"));
        conversionPanel.add(fromComboBox);
        conversionPanel.add(new JLabel("Para:"));
        conversionPanel.add(toComboBox);
        conversionPanel.add(new JLabel("Valor:"));
        conversionPanel.add(amountField);
        conversionPanel.add(new JLabel(""));
        conversionPanel.add(convertButton);

        // Painel de resultado
        JPanel resultPanel = new JPanel(new BorderLayout(10, 10));
        resultPanel.setBorder(BorderFactory.createTitledBorder("📊 Resultado"));
        resultPanel.add(resultLabel, BorderLayout.NORTH);
        resultPanel.add(rateLabel, BorderLayout.CENTER);

        // Painel de taxas
        JPanel ratesPanel = new JPanel(new BorderLayout(10, 10));
        ratesPanel.setBorder(BorderFactory.createTitledBorder("💱 Taxas de Câmbio"));

        ratesArea = new JTextArea();
        ratesArea.setEditable(false);
        ratesArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(ratesArea);

        JButton refreshRatesButton = new JButton("🔄 Atualizar Taxas");
        refreshRatesButton.addActionListener(e -> atualizarTaxas());

        ratesPanel.add(scrollPane, BorderLayout.CENTER);
        ratesPanel.add(refreshRatesButton, BorderLayout.SOUTH);

        // Botões extras
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton swapButton = new JButton("⇄ Trocar Moedas");
        swapButton.addActionListener(e -> trocarMoedas());

        JButton clearButton = new JButton("🗑️ Limpar");
        clearButton.addActionListener(e -> limparCampos());

        JButton exitButton = new JButton("🚪 Sair");
        exitButton.setBackground(new Color(220, 20, 60));
        exitButton.setForeground(Color.WHITE);
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(swapButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(exitButton);

        // Adicionar painéis ao painel principal
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(conversionPanel, BorderLayout.WEST);
        mainPanel.add(resultPanel, BorderLayout.CENTER);
        mainPanel.add(ratesPanel, BorderLayout.EAST);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Carregar taxas na área de texto
        atualizarAreaTaxas();

        // Converter automaticamente ao iniciar
        realizarConversao();
    }

    private void posicionarComponentes() {
        // Já feito no criarComponentes()
    }

    private void realizarConversao() {
        try {
            String fromCurrency = (String) fromComboBox.getSelectedItem();
            String toCurrency = (String) toComboBox.getSelectedItem();
            double amount = Double.parseDouble(amountField.getText());

            double result = converter.convert(amount, fromCurrency, toCurrency);

            // Formatar resultado
            String fromSymbol = getCurrencySymbol(fromCurrency);
            String toSymbol = getCurrencySymbol(toCurrency);

            resultLabel.setText(String.format("<html><b>💰 %s %s = %s %s</b></html>",
                    fromSymbol + df.format(amount), fromCurrency,
                    toSymbol + df.format(result), toCurrency));

            // Calcular e mostrar taxa
            if (!fromCurrency.equals("USD")) {
                Double rateFrom = exchangeRates.getRate(fromCurrency);
                if (rateFrom != null) {
                    double directRate = result / amount;
                    rateLabel.setText(String.format("Taxa: 1 %s = %s %.6f %s",
                            fromCurrency, toSymbol, directRate, toCurrency));
                }
            }

        } catch (NumberFormatException e) {
            resultLabel.setText("❌ Valor inválido! Use números.");
            rateLabel.setText("");
        } catch (Exception e) {
            resultLabel.setText("❌ Erro: " + e.getMessage());
            rateLabel.setText("");
        }
    }

    private void atualizarAreaTaxas() {
        Map<String, Double> rates = exchangeRates.getConversionRates();
        StringBuilder sb = new StringBuilder();

        sb.append("Base: ").append(exchangeRates.getBaseCode()).append("\n");
        sb.append("Atualizado: ").append(exchangeRates.getTimeLastUpdateUtc()).append("\n");
        sb.append("=".repeat(50)).append("\n\n");

        // Moedas principais
        String[] mainCurrencies = {"BRL", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "ARS", "CLP"};

        for (String currency : mainCurrencies) {
            Double rate = rates.get(currency);
            if (rate != null) {
                sb.append(String.format("%-5s: %10.4f\n", currency, rate));
            }
        }

        sb.append("\n").append("=".repeat(50)).append("\n");
        sb.append("Total de moedas: ").append(rates.size());

        ratesArea.setText(sb.toString());
    }

    private void trocarMoedas() {
        String from = (String) fromComboBox.getSelectedItem();
        String to = (String) toComboBox.getSelectedItem();

        fromComboBox.setSelectedItem(to);
        toComboBox.setSelectedItem(from);

        realizarConversao();
    }

    private void limparCampos() {
        amountField.setText("100.00");
        resultLabel.setText("Resultado: ");
        rateLabel.setText("Taxa: ");
    }

    private void atualizarTaxas() {
        int resposta = JOptionPane.showConfirmDialog(this,
                "Deseja atualizar as taxas de câmbio?\nIsso fará uma nova requisição à API.",
                "Atualizar Taxas",
                JOptionPane.YES_NO_OPTION);

        if (resposta == JOptionPane.YES_OPTION) {
            carregarDados();
            atualizarAreaTaxas();
            realizarConversao();
        }
    }

    private String getCurrencySymbol(String currencyCode) {
        return switch (currencyCode) {
            case "BRL" -> "R$ ";
            case "USD" -> "$ ";
            case "EUR" -> "€ ";
            case "GBP" -> "£ ";
            case "JPY" -> "¥ ";
            case "ARS", "CLP", "CAD", "AUD" -> "$ ";
            default -> "";
        };
    }

    public static void main(String[] args) {
        // Usar look and feel do sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new ConversorGUI();
        });
    }
}