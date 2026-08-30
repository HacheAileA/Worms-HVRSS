package model.persistence;

import model.Map;
import model.items.Item;
import model.items.guns.Guns;
import model.items.tools.Tools;
import model.players.Team;
import model.players.Worm;
import model.GameModel;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Classe SaveManager qui gère la sauvegarde du jeu, dans des fichiers texte.
 * 
 * @author MESNILDREY Valentin
 * 
 * @see GameModel#getClass()
 * @see Team#getClass()
 * @see Worm#getClass()
 * @see Map#getClass()
 * @see Guns#getClass()
 * @see Tools#getClass()
 * 
 * @since 2.0
 * 
 * @version 2.0
 */
public class SaveManager {

    // ==================== CONSTANTS ====================
    /** Répertoire de sauvegarde */
    private static String SAVE_DIR = "../saves/";
    /** Extension des fichiers de sauvegarde */
    private static String FILE_EXTENSION = ".txt";

    /**
     * Méthode qui sauvegarde le jeu dans un fichier avec le nom donné.
     * 
     * @param model - Le GameModel à sauvegarder
     * @param filename - Le nom du fichier de sauvegarde
     * @param nextTeam - L'équipe qui jouera après la sauvegarde
     */
    public static void save(GameModel model, String filename, Team nextTeam) {
        ensureDirectory();
        String finalFilename = ensureExtension(filename);
        Path filePath = Paths.get(SAVE_DIR, finalFilename);
        String content = buildSaveContent(model, nextTeam);
        
        try {
            Files.writeString(filePath, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sauvegarde le jeu en demandant à l'utilisateur un nom de fichier via la console. Si le fichier existe déjà, demande confirmation avant d'écraser.
     * 
     * @param model - Le GameModel à sauvegarder
     * @param nextTeam - L'équipe qui jouera après la sauvegarde
     */
    public static void saveWithPrompt(GameModel model, Team nextTeam) {
        ensureDirectory();
        
        try (BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {
            String filename = promptForValidFilename(console);
            save(model, filename, nextTeam);
            System.out.println("Sauvegarde effectuée : " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ==================== PRIVATE METHODS ====================
    /**
     * Méthode qui s'assure que le répertoire de sauvegarde existe
     */
    private static void ensureDirectory() {
        try {
            Files.createDirectories(Paths.get(SAVE_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode qui demande à l'utilisateur un nom de fichier valide via la console. Si le fichier existe déjà, demande confirmation avant d'écraser.
     * 
     * @param console - Le BufferedReader pour lire l'entrée utilisateur
     * 
     * @throws IOException En cas d'erreur de lecture
     * 
     * @return Le nom de fichier validé
     */
    private static String promptForValidFilename(BufferedReader console) throws IOException {
        while (true) {
            System.out.print("Nom de la sauvegarde (sans extension) : ");
            String filename = ensureExtension(console.readLine().trim());
            Path filePath = Paths.get(SAVE_DIR, filename);

            if (!Files.exists(filePath)) {
                return filename;
            }
            
            System.out.print("Le fichier existe déjà. Voulez-vous l'écraser ? (oui/non) : ");
            String response = console.readLine().trim().toLowerCase();
            
            if (response.equals("oui") || response.equals("o")) {
                return filename;
            }
            
            System.out.println("Veuillez entrer un autre nom de fichier.");
        }
    }

    /**
     * Méthode qui s'assure que le nom de fichier a la bonne extension
     * 
     * @param filename - Le nom du fichier
     * 
     * @return Le nom du fichier avec l'extension correcte
     */
    private static String ensureExtension(String filename) {
        return filename.endsWith(FILE_EXTENSION) ? filename : filename + FILE_EXTENSION;
    }

    private static String buildSaveContent(GameModel model, Team nextTeam) {
        StringBuilder content = new StringBuilder();
        
        content.append("===== GAME SAVE =====\n\n");
        content.append("=== TEAMS ORDER ===\n");
        
        ArrayList<Team> orderedTeams = getTeamsStartingFrom(model.getTeams(), nextTeam);
        writeAllTeams(content, orderedTeams);
        writeGameSettings(content, model);
        writeMap(content, model.getMap());
        
        content.append("\n===== END SAVE =====\n");
        
        return content.toString();
    }

    /**
     * Méthode qui réorganise la liste des équipes pour commencer à partir de l'équipe donnée
     * 
     * @param teams - La liste originale des équipes
     * @param startTeam - L'équipe à partir de laquelle commencer
     * 
     * @return La liste réorganisée des équipes
     */
    private static ArrayList<Team> getTeamsStartingFrom(ArrayList<Team> teams, Team startTeam) {
        int startIndex = teams.indexOf(startTeam);
        ArrayList<Team> reordered = new ArrayList<>(teams.size());
        
        for (int i = 0; i < teams.size(); i++) {
            reordered.add(teams.get((startIndex + i) % teams.size()));
        }
        
        return reordered;
    }

    /**
     * Méthode qui écrit les informations de toutes les équipes dans le contenu de sauvegarde
     * 
     * @param content - Le StringBuilder pour le contenu de sauvegarde
     * @param teams - La liste des équipes à écrire
     */
    private static void writeAllTeams(StringBuilder content, ArrayList<Team> teams) {
        int teamNumber = 1;
        
        for (Team team : teams) {
            content.append("\n------------------------------\n");
            writeTeamInfo(content, team, teamNumber++);
            writeTeamWorms(content, team);
            writeTeamInventory(content, team);
        }
    }

    /**
     * Méthode qui écrit les informations d'une équipe dans le contenu de sauvegarde
     * 
     * @param content - Le StringBuilder pour le contenu de sauvegarde
     * @param team - L'équipe dont les informations sont écrites
     * @param teamNumber - Le numéro de l'équipe dans l'ordre
     */
    private static void writeTeamInfo(StringBuilder content, Team team, int teamNumber) {
        content.append("Team ").append(teamNumber).append(": ").append(team.getName())
               .append(" | Symbol = ").append(team.getSymbol())
               .append(" | TeamId = ").append(team.getTeamId())
               .append(" | isBot = ").append(team.isBot())
               .append("\n");
    }

    /**
     * Méthode qui écrit les informations des worms d'une équipe dans le contenu de sauvegarde
     * 
     * @param content - Le StringBuilder pour le contenu de sauvegarde
     * @param team - L'équipe dont les worms sont écrits
     */
    private static void writeTeamWorms(StringBuilder content, Team team) {
        content.append("  Worms:\n");
        
        for (Worm worm : team.getWorms()) {
            content.append("    - ").append(worm.getName())
                   .append(" | HP = ").append(worm.getHp())
                   .append(" | Position = (").append(worm.getX()).append(", ").append(worm.getY()).append(")")
                   .append(" | Symbol = ").append(worm.getSymbol())
                   .append(" | TeamId = ").append(worm.getTeamId())
                   .append("\n");
        }
    }

    /**
     * Méthode qui écrit les informations de l'inventaire d'une équipe dans le contenu de sauvegarde
     * 
     * @param content - Le StringBuilder pour le contenu de sauvegarde
     * @param team - L'équipe dont l'inventaire est écrit
     */
    private static void writeTeamInventory(StringBuilder content, Team team) {
        content.append("  Inventory:\n");
        
        ArrayList<Item> allItems = team.getInventory().getAvailableItems(team);
        
        content.append("    - Guns:\n");
        writeGunsFromItems(content, allItems);
        
        content.append("    - Tools:\n");
        writeToolsFromItems(content, allItems);
    }

    /**
     * Méthode qui écrit les informations des armes à feu dans le contenu de sauvegarde
     * 
     * @param content - Le StringBuilder pour le contenu de sauvegarde
     * @param items - La liste des items à filtrer pour les armes à feu
     */
    private static void writeGunsFromItems(StringBuilder content, ArrayList<Item> items) {
        for (Item item : items) {
            if (item instanceof Guns) {
                Guns gun = (Guns) item;
                content.append("        - ").append(gun.getName())
                       .append(" | Ammo = ").append(gun.getAmmo())
                       .append("\n");
            }
        }
    }

    /**
     * Méthode qui écrit les informations des outils dans le contenu de sauvegarde
     * 
     * @param content - Le StringBuilder pour le contenu de sauvegarde
     * @param items - La liste des items à filtrer pour les outils
     */
    private static void writeToolsFromItems(StringBuilder content, ArrayList<Item> items) {
        for (Item item : items) {
            if (item instanceof Tools) {
                Tools tool = (Tools) item;
                content.append("        - ").append(tool.getName())
                       .append(" | Uses left = ").append(tool.getAmmo())
                       .append("\n");
            }
        }
    }

    /**
     * Méthode qui écrit les paramètres du jeu dans le contenu de sauvegarde
     * 
     * @param content - Le StringBuilder pour le contenu de sauvegarde
     * @param model - Le GameModel dont les paramètres sont écrits
     */
    private static void writeGameSettings(StringBuilder content, GameModel model) {
        if (model.getFriendlyFire()) {
            content.append("Friendly fire\n");
        }
        
        if (model.isWindEnabled()) {
            content.append("Wind Enabled\n");
        }
        
        content.append("\n\n");
    }

    /**
     * Méthode qui écrit les informations de la carte dans le contenu de sauvegarde
     * 
     * @param content - Le StringBuilder pour le contenu de sauvegarde
     * @param map - La carte à écrire
     */
    private static void writeMap(StringBuilder content, Map map) {
        content.append("=== MAP ===\n");
        content.append("MapType: ").append(map.getMapType()).append("\n");
        content.append("Dimensions: ").append(map.getWidth()).append(" x ").append(map.getHeight()).append("\n\n");
        
        for (int y = 0; y < map.getHeight(); y++) {
            writeMapRow(content, map, y);
        }
    }

    /**
     * Méthode qui écrit une ligne de la carte dans le contenu de sauvegarde
     * 
     * @param content - Le StringBuilder pour le contenu de sauvegarde
     * @param map - La carte à écrire
     * @param y - La ligne y à écrire
     */
    private static void writeMapRow(StringBuilder content, Map map, int y) {
        for (int x = 0; x < map.getWidth(); x++) {
            char cell = map.grid[y][x];
            content.append((cell == ' ' || cell == 0) ? '.' : cell);
        }
        content.append("\n");
    }
}