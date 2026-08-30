package view.console;

import java.util.Hashtable;

/**
 * Classe AnsiColor qui gère les couleurs ANSI dans la console.
 * 
 * @author MESNILDREY Valentin
 * @author NESI Romain
 * 
 * @see AnsiColor#color(String, String)
 * @see AnsiColor#getColoredString(String, String) 
 * 
 * @since 1.1
 * 
 * @version 1.1
 */
public class AnsiColor {
    /**
     * Table de hachage contenant les codes ANSI pour différentes couleurs.
     * 
     * @see Hashtable#put(Object, Object)
     * 
     * @since 1.1
     */
    public static Hashtable<String, String> COLORS = new Hashtable<>();
    static{
        COLORS = new Hashtable<>();
        COLORS.put("RESET", "\u001B[0m");
        COLORS.put("BLUE", "\u001B[34m");
        COLORS.put("CYAN", "\u001B[36m");
        COLORS.put("GREEN", "\u001B[32m");
        COLORS.put("BROWN", "\u001B[38;5;94m");
        COLORS.put("WHITE", "\u001B[37m");
        COLORS.put("RED", "\u001B[31m");
        COLORS.put("YELLOW", "\u001B[33m");
        COLORS.put("MAGENTA", "\u001B[35m");
        COLORS.put("BRIGHT_BLUE", "\u001B[94m");
    }

    /**
     * Méthode qui applique une couleur ANSI à un texte.
     * 
     * @param code - Le code ANSI de la couleur
     * @param texte - Le texte à colorer
     * 
     * @see AnsiColor#COLORS
     * 
     * @since 1.1
     * 
     * @return le texte coloré avec le code ANSI
     */
    public static String color (String code, String texte) {
        return code + texte + COLORS.get("RESET");
    }

    /**
     * Méthode qui retourne une chaîne de caractères colorée selon la couleur spécifiée.
     * 
     * @param color - Le nom de la couleur (ex: "RED", "BLUE", etc.)
     * @param texte - Le texte à colorer
     * 
     * @see AnsiColor#color(String, String)
     * @see AnsiColor#COLORS
     * 
     * @return Le texte coloré avec la couleur spécifiée
     * 
     * @since 1.1
     */
    public static String getColoredString(String color, String texte) {
        switch (color.toUpperCase()) {
            case "RED":
                return color(COLORS.get("RED"), texte);
            case "BLUE":
                return color(COLORS.get("BLUE"), texte);
            case "GREEN":
                return color(COLORS.get("GREEN"), texte);
            case "YELLOW":
                return color(COLORS.get("YELLOW"), texte);
            case "CYAN":
                return color(COLORS.get("CYAN"), texte);
            case "MAGENTA":
                return color(COLORS.get("MAGENTA"), texte);
            case "BRIGHT_BLUE":
                return color(COLORS.get("BRIGHT_BLUE"), texte);
            case "BROWN":
                return color(COLORS.get("BROWN"), texte);
            default:
                return color(COLORS.get("WHITE"), texte);
        }
    }
}
