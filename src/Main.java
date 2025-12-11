import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("\n🚀 Inicializando Conversor de Moedas...");

        // Oferecer escolha entre GUI e Console
        String[] options = {"🖥️ Interface Gráfica (GUI)", "📟 Versão Console", "❌ Sair"};

        int choice = JOptionPane.showOptionDialog(null,
                "Escolha a versão do Conversor de Moedas:",
                "Conversor de Moedas Internacional",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        switch (choice) {
            case 0 -> iniciarGUI();
            case 1 -> iniciarConsole();
            default -> System.out.println("Programa cancelado.");
        }
    }

    private static void iniciarGUI() {
        System.out.println("Iniciando interface gráfica...");
        ConversorGUI.main(new String[]{});
    }

    private static void iniciarConsole() {
        System.out.println("Iniciando versão console...");
        Menu menu = new Menu();
        menu.iniciar();
        System.out.println("\n✨ Programa finalizado. Até logo! ✨");
    }
}