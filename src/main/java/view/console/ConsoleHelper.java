package view.console;

import app.Main;

/**
 * Classe ConsoleHelper qui gére la console proprement.
 * 
 * @author MESNILDREY Valentin
 * 
 * @see Thread#getClass()
 * @see Main#getClass()
 * 
 * @since 1.0
 * 
 * @version 1.1
 */
public class ConsoleHelper {

    private static boolean devMode;

    // ========================Methods========================//

    /**
     * Méthode statique qui permet de nettoyer (clear) la console.
     * 
     * @since 1.0
     */
    public static void clearConsole() {
        System.out.println("\n\n");
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Méthode statique pour définir si le mode développeur est activé.
     * 
     * @param newDevMode - Un boolean représentant le mode développeur
     * 
     * @since 1.1
     */
    public static void setDevMode(boolean newDevMode) {
        devMode = newDevMode;
    }

    /**
     * Méthode statique qui marque ou non une pause pour l'affichage.
     * 
     * @param time - Le temps de pause qui va être marqué, en millisecondes
     * 
     * @throws InterruptedException Gestion de l'exception
     * 
     * @see Thread#sleep(long)
     * 
     * @since 1.0
     */
    public static void sleepTime(int time) throws InterruptedException {
        if (!devMode) {
            Thread.sleep(time);
        }
    }
}