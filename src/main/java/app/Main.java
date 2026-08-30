package app;

import java.awt.EventQueue;

/**
 * Classe Main qui gère le démarrage du jeu
 * 
 * @see EventQueue#getClass()
 * @see Thread#getClass()
 * @see Launcher#getClass()
 *
 * @since 0.0
 * @version 2.0
 */
public class Main {

    /**
     * Méthode principale pour l'exécution du jeu
     * 
     * @param args Le tableau d'arguments
     * 
     * @see EventQueue#invokeLater(Runnable)
     * @see Thread#currentThread()
     * @see Thread#interrupt()
     * @see Launcher#launch(String[])
     * 
     * @since 1.1
     */
    public static void main(String[] args) {
        /*
         * EventQueue.invokeLater(new Runnable() {
         * 
         * @Override
         * public void run() {
         * try {
         * Launcher.launch(args);
         * } catch (Exception exception) {
         * System.err.println("Le jeu a été interrompu de manière inattendue.");
         * System.out.println(exception);
         * Thread.currentThread().interrupt();
         * }
         * System.exit(0);
         * }
         * });
         */

        try {
            Launcher.launch(args);
        } catch (Exception exception) {
            System.err.println("Le jeu a été interrompu de manière inattendue.");
            exception.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
