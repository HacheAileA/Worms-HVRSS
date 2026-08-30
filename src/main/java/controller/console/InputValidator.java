package controller.console;

import java.util.Scanner;

import view.console.ConsoleHelper;

/**
 * Classe InputValidator pour gérer les entrées et éviter les crashs.
 * 
 * @author MESNILDREY Valentin
 * 
 * @see Double#getClass()
 * @see Integer#getClass()
 * @see NumberFormatException#getClass()
 * @see Scanner#getClass()
 * @see String#getClass()
 * @see ConsoleHelper#clearConsole()
 * 
 * @since 1.0
 * 
 * @version 1.1
 */
public class InputValidator {

    // ========================Methods========================/

    /**
     * Méthode qui empêche le crash, si l'utilisateur rentre autre chose qu'un entier.
     * 
     * @param sc - Scanner
     * @param message - Message à afficher
     * 
     * @see Integer#parseInt(String)
     * @see Scanner#nextLine()
     * @see String#equalsIgnoreCase(String)
     * @see ConsoleHelper#clearConsole()
     * 
     * @return Renvoie l'entier entré par l'utilisateur
     * 
     * @since 1.0
     */
    public static int checkIsInt(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            String input = sc.nextLine();

            if (input.equalsIgnoreCase("retour")) {
                ConsoleHelper.clearConsole();
                return -1;
            }
            try {
                System.out.println();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Ceci n'est pas un nombre");
            }
        }
    }

    /**
     * Méthode qui empêche le crash si l'utilisateur entre autre chose qu'un double.
     * 
     * @param sc - Scanner
     * @param message - Message à afficher
     * 
     * @see Double#NaN
     * @see Double#parseDouble(String)
     * @see Scanner#nextLine()
     * @see String#equalsIgnoreCase(String)
     * @see ConsoleHelper#clearConsole()
     * 
     * @return Renvoie le double entré par l'utilisateur
     * 
     * @since 1.1
     */
    public static double checkIsDouble(Scanner sc, String message) {
        while (true) {
            System.out.print(message + " (ou 'retour') : ");
            String input = sc.nextLine();

            if (input.equalsIgnoreCase("retour")) {
                ConsoleHelper.clearConsole();
                return Double.NaN;
            }

            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Ceci n'est pas un nombre");
            }
        }
    }

    /**
     * Méthode qui récupère une chaîne de caractères, ou annule l'opération si l'utilisateur entre "retour".
     * 
     * @param sc - Scanner
     * @param message - Message à afficher
     * 
     * @see Scanner#nextLine()
     * @see String#equalsIgnoreCase(String)
     * @see ConsoleHelper#clearConsole()
     * 
     * @return Renvoie la chaîne de caractères entrée par l'utilisateur ou null si "retour" est entré
     * 
     * @since 1.1
     */
    public static String getStringOrCancel(Scanner sc, String message) {
        System.out.print(message + " (ou 'retour') : ");
        String input = sc.nextLine();
        if (input.equalsIgnoreCase("retour")) {
            ConsoleHelper.clearConsole();
            return null;
        }
        return input;
    }
}
