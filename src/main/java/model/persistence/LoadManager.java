package model.persistence;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

import controller.console.InputValidator;
import model.GameModel;
import model.Map;
import model.items.Item;
import model.items.guns.Guns;
import model.items.tools.Tools;
import model.players.Team;
import model.players.Worm;

/**
 * Classe LoadManager permettant de charger une partie sauvegardée depuis un fichier.
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
 * @since 1.1
 * 
 * @version 2.0
 */
public class LoadManager {
    /** Répertoire de sauvegarde */
    private static String SAVE_DIR = "../saves/";
    /** Extension des fichiers de sauvegarde */
    private static String FILE_EXTENSION = ".txt";

    /** Résultat de chargement */
    public static class LoadResult {
        /** Le modèle de jeu chargé */
        public final GameModel model;
        /** L'équipe suivante */
        public final Team nextTeam;

        /**
         * Constructeur de LoadResult
         * 
         * @param model - Le modèle de jeu chargé
         * @param nextTeam - L'équipe suivante
         */
        public LoadResult(GameModel model, Team nextTeam) {
            this.model = model;
            this.nextTeam = nextTeam;
        }
    }

    /**
     * Méthode qui charge une partie depuis un fichier de sauvegarde spécifié par son nom.
     * 
     * @param filename - Le nom du fichier de sauvegarde
     * 
     * @return Le résultat du chargement contenant le modèle de jeu et l'équipe suivante, ou null en cas d'erreur
     */
    public static LoadResult load(String filename) {
        Path filePath = Paths.get(SAVE_DIR, filename);

        if (!Files.exists(filePath)) {
            System.out.println("Save file not found: " + filename);
            return null;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            return parseSaveFile(reader);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Méthode qui charge une partie depuis un fichier de sauvegarde spécifié par son objet File.
     * 
     * @param file - Le fichier de sauvegarde
     * 
     * @return Le résultat du chargement contenant le modèle de jeu et l'équipe suivante, ou null en cas d'erreur
     */
    public static LoadResult load(File file) {
        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            return parseSaveFile(reader);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Méthode qui charge une partie en demandant à l'utilisateur de choisir un fichier de sauvegarde.
     * 
     * @return Le résultat du chargement contenant le modèle de jeu et l'équipe suivante, ou null en cas d'erreur ou d'annulation
     */
    public static LoadResult load() {
        return loadWithPrompt();
    }

    /**
     * Méthode qui charge une partie en affichant les fichiers de sauvegarde disponibles et en demandant à l'utilisateur de faire un choix.
     * 
     * @return Le résultat du chargement contenant le modèle de jeu et l'équipe suivante, ou null en cas d'erreur ou d'annulation
     */
    public static LoadResult loadWithPrompt() {
        ArrayList<Path> saveFiles = getAllSaveFiles();

        if (saveFiles.isEmpty()) {
            System.out.println("Aucune sauvegarde disponible.");
            return null;
        }

        showAvailableSaves(saveFiles);

        try (BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {
            int chosenIndex = getUserFileChoice(console, saveFiles.size());

            if (chosenIndex == -1) {
                return null;
            }

            return load(saveFiles.get(chosenIndex).getFileName().toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Méthode qui analyse le contenu d'un fichier de sauvegarde et crée le modèle de jeu correspondant.
     * 
     * @param reader - Le BufferedReader pour lire le fichier de sauvegarde
     * 
     * @throws IOException En cas d'erreur de lecture
     * 
     * @return Le résultat du chargement contenant le modèle de jeu et l'équipe suivante
     */
    private static LoadResult parseSaveFile(BufferedReader reader) throws IOException {
        ArrayList<Team> teams = new ArrayList<>();
        Team nextTeam = null;
        boolean friendlyFire = false;
        boolean windEnabled = false;
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.startsWith("Team")) {
                Team team = readTeamSection(line, reader);
                if (nextTeam == null) {
                    nextTeam = team;
                }
                teams.add(team);
            }

            if (line.contains("Friendly fire")) {
                friendlyFire = true;
            }

            if (line.contains("Wind Enabled")) {
                windEnabled = true;
            }

            if (line.startsWith("=== MAP ===")) {
                break;
            }
        }

        Map map = readMapSection(reader);
        GameModel model = new GameModel(teams, map, friendlyFire);
        model.setWindEnabled(windEnabled);

        return new LoadResult(model, nextTeam);
    }

    /**
     * Méthode qui lit une section d'équipe dans le fichier de sauvegarde.
     * 
     * @param headerLine - La ligne d'en-tête de l'équipe
     * @param reader - Le BufferedReader pour lire le fichier de sauvegarde
     * 
     * @throws IOException En cas d'erreur de lecture
     * 
     * @return L'équipe chargée
     */
    private static Team readTeamSection(String headerLine, BufferedReader reader) throws IOException {
        String teamName = extractTeamName(headerLine);
        char teamSymbol = extractTeamSymbol(headerLine);
        int teamId = extractTeamId(headerLine);
        boolean isBot = extractIsBot(headerLine);

        ArrayList<Worm> worms = readWormsSection(reader, teamId);
        ArrayList<Item> inventoryItems = readInventorySection(reader, teamId, teamName);

        GameModel tempModel = new GameModel(new ArrayList<>(), new Map(1, 1), false);
        Team team = new Team(teamName, teamId, worms, teamSymbol, tempModel);

        for (Worm worm : worms) {
            worm.setTeam(team);
        }

        team.getInventory().loadItemsFromSave(inventoryItems, team);
        team.setBot(isBot);

        return team;
    }

    /**
     * Méthode qui extrait le nom de l'équipe depuis la ligne d'en-tête.
     * 
     * @param line - La ligne d'en-tête de l'équipe
     * 
     * @return Le nom de l'équipe
     */
    private static String extractTeamName(String line) {
        int colonIndex = line.indexOf(':');
        int pipeIndex = line.indexOf('|');
        return line.substring(colonIndex + 1, pipeIndex).trim();
    }

    /**
     * Méthode qui extrait le symbole de l'équipe depuis la ligne d'en-tête.
     * 
     * @param line - La ligne d'en-tête de l'équipe
     * 
     * @return Le symbole de l'équipe
     */
    private static char extractTeamSymbol(String line) {
        String[] parts = line.split("\\|");
        for (String part : parts) {
            if (part.trim().startsWith("Symbol")) {
                String symbolValue = part.replace("Symbol", "").replace("=", "").trim();
                if (!symbolValue.isEmpty()) {
                    return symbolValue.charAt(0);
                }
            }
        }
        return '3';
    }

    /**
     * Méthode qui extrait l'identifiant de l'équipe depuis la ligne d'en-tête.
     * 
     * @param line - La ligne d'en-tête de l'équipe
     * 
     * @return L'identifiant de l'équipe
     */
    private static int extractTeamId(String line) {
        String[] parts = line.split("\\|");
        for (String part : parts) {
            if (part.trim().startsWith("TeamId")) {
                return Integer.parseInt(part.replace("TeamId", "").replace("=", "").trim());
            }
        }
        return 3;
    }

    /**
     * Méthode qui extrait si l'équipe est un bot depuis la ligne d'en-tête.
     * 
     * @param line - La ligne d'en-tête de l'équipe
     * 
     * @return true si l'équipe est un bot, false sinon
     */
    private static boolean extractIsBot(String line) {
        String[] parts = line.split("\\|");
        for (String part : parts) {
            if (part.trim().startsWith("isBot")) {
                return Boolean.parseBoolean(part.replace("isBot", "").replace("=", "").trim());
            }
        }
        return false;
    }

    /**
     * Méthode qui lit la section des worms dans le fichier de sauvegarde.
     * 
     * @param reader - Le BufferedReader pour lire le fichier de sauvegarde
     * @param teamId - L'identifiant de l'équipe à laquelle appartiennent les worms
     * 
     * @throws IOException En cas d'erreur de lecture
     * 
     * @return La liste des worms chargés
     */
    private static ArrayList<Worm> readWormsSection(BufferedReader reader, int teamId) throws IOException {
        ArrayList<Worm> worms = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.startsWith("Inventory")) {
                break;
            }

            if (line.startsWith("Worms")) {
                continue;
            }

            if (line.startsWith("-")) {
                Worm worm = parseWormLine(line, teamId);
                worms.add(worm);
            }
        }

        return worms;
    }

    /**
     * Méthode d'analyse d'une ligne de worm et crée l'objet Worm correspondant.
     * 
     * @param line - La ligne de worm à analyser
     * @param teamId - L'identifiant de l'équipe à laquelle appartient le worm
     * 
     * @return Le worm créé
     */
    private static Worm parseWormLine(String line, int teamId) {
        line = line.substring(1).trim();
        String[] parts = line.split("\\|");

        String wormName = parts[0].trim();
        int hp = Integer.parseInt(parts[1].replace("HP", "").replace("=", "").trim());

        String positionPart = parts[2].replace("Position", "").replace("=", "").trim();
        positionPart = positionPart.substring(1, positionPart.length() - 1);
        String[] coords = positionPart.split(",");
        int x = (int) Double.parseDouble(coords[0].trim());
        int y = (int) Double.parseDouble(coords[1].trim());

        char wormSymbol = '1';
        if (parts.length > 3) {
            String symbolPart = parts[3].replace("Symbol", "").replace("=", "").trim();
            if (!symbolPart.isEmpty()) {
                wormSymbol = symbolPart.charAt(0);
            }
        }

        Worm worm = new Worm(wormName, hp, wormSymbol, teamId);
        worm.setPosition(x, y);

        return worm;
    }

    /**
     * Méthode de lecture de la section de l'inventaire dans le fichier de sauvegarde.
     * 
     * @param reader - Le BufferedReader pour lire le fichier de sauvegarde
     * @param teamId - L'identifiant de l'équipe à laquelle appartient l'inventaire
     * @param teamName - Le nom de l'équipe à laquelle appartient l'inventaire
     * 
     * @throws IOException En cas d'erreur de lecture
     * 
     * @return La liste des items chargés dans l'inventaire 
     */
    private static ArrayList<Item> readInventorySection(BufferedReader reader, int teamId, String teamName)
            throws IOException {
        ArrayList<Item> items = new ArrayList<>();
        boolean readingGuns = false;
        boolean readingTools = false;
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.isEmpty() || line.startsWith("---")) {
                break;
            }

            if (line.contains("- Guns:")) {
                readingGuns = true;
                readingTools = false;
                continue;
            }

            if (line.contains("- Tools:")) {
                readingTools = true;
                readingGuns = false;
                continue;
            }

            if (line.startsWith("-")) {
                if (readingGuns) {
                    Item gun = parseGunLine(line, teamId, teamName);
                    if (gun != null) {
                        items.add(gun);
                    }
                } else if (readingTools) {
                    Item tool = parseToolLine(line, teamId, teamName);
                    if (tool != null) {
                        items.add(tool);
                    }
                }
            }
        }

        return items;
    }

    /**
     * Méthode d'analyse d'une ligne de gun et crée l'objet Gun correspondant.
     * 
     * @param line - La ligne de gun à analyser
     * @param teamId - L'identifiant de l'équipe à laquelle appartient le gun
     * @param teamName - Le nom de l'équipe à laquelle appartient le gun
     * 
     * @return Le gun créé
     */
    private static Item parseGunLine(String line, int teamId, String teamName) {
        line = line.substring(1).trim();
        String[] parts = line.split("\\|");

        String gunName = parts[0].trim();
        int ammo = 0;

        if (parts.length > 1) {
            ammo = Integer.parseInt(parts[1].replace("Ammo", "").replace("=", "").trim());
        }

        GameModel tempModel = new GameModel(new ArrayList<>(), new Map(1, 1), false);
        Item gun = createGunByName(gunName, tempModel);

        if (gun != null && gun instanceof Guns) {
            ((Guns) gun).setAmmo(ammo);
        }

        return gun;
    }

    /**
     * Méthode d'analyse d'une ligne de tool et crée l'objet Tool correspondant.
     * 
     * @param line - La ligne de tool à analyser
     * @param teamId - L'identifiant de l'équipe à laquelle appartient le tool
     * @param teamName - Le nom de l'équipe à laquelle appartient le tool
     * 
     * @return Le tool créé
     */
    private static Item parseToolLine(String line, int teamId, String teamName) {
        line = line.substring(1).trim();
        String[] parts = line.split("\\|");

        String toolName = parts[0].trim();
        int uses = 0;

        if (parts.length > 1) {
            uses = Integer.parseInt(parts[1].replace("Uses left", "").replace("=", "").trim());
        }

        GameModel tempModel = new GameModel(new ArrayList<>(), new Map(1, 1), false);
        Team tempTeam = new Team(teamName, teamId, new ArrayList<>(), (char) ('A' + teamId), tempModel);
        Item tool = createToolByName(toolName, tempTeam);

        if (tool != null && tool instanceof Tools) {
            ((Tools) tool).setAmmo(uses);
        }

        return tool;
    }

    /**
     * Méthode de lecture de la section de la carte dans le fichier de sauvegarde.
     * 
     * @param reader Le BufferedReader pour lire le fichier de sauvegarde
     * 
     * @throws IOException En cas d'erreur de lecture
     * 
     * @return La carte chargée
     */
    private static Map readMapSection(BufferedReader reader) throws IOException {
        String mapType = reader.readLine().replace("MapType:", "").trim();

        String dimensionsLine = reader.readLine().replace("Dimensions:", "").trim();
        String[] dimensions = dimensionsLine.split("x");
        int width = Integer.parseInt(dimensions[0].trim());
        int height = Integer.parseInt(dimensions[1].trim());

        reader.readLine();

        Map map = new Map(height, width);
        map.setMapType(mapType);

        for (int y = 0; y < height; y++) {
            String mapLine = reader.readLine();
            fillMapRow(map, mapLine, y, width);
        }

        return map;
    }

    /**
     * Méthode de remplissage d'une ligne de la carte à partir d'une ligne de texte.
     * 
     * @param map - La carte à remplir
     * @param line - La ligne de texte représentant la ligne de la carte
     * @param y - L'indice de la ligne à remplir
     * @param width - La largeur de la carte
     */
    private static void fillMapRow(Map map, String line, int y, int width) {
        for (int x = 0; x < width; x++) {
            char cell = ' ';

            if (x < line.length()) {
                cell = line.charAt(x);
            }

            if (cell == '.') {
                cell = ' ';
            }

            map.grid[y][x] = cell;
        }
    }

    /**
     * Méthode de création d'une instance de Gun à partir de son nom.
     * 
     * @param gunName - Le nom du gun
     * @param model - Le modèle de jeu
     * 
     * @return L'instance de Gun créée, ou null en cas d'erreur
     */
    private static Item createGunByName(String gunName, GameModel model) {
        try {
            String className = "model.items.guns." + gunName;
            Class<?> gunClass = Class.forName(className);
            return (Item) gunClass.getConstructor(GameModel.class).newInstance(model);
        } catch (Exception e) {
            System.out.println("Failed to create gun: " + gunName);
            return null;
        }
    }

    /**
     * Méthode de création d'une instance de Tool à partir de son nom.
     * 
     * @param toolName - Le nom du tool
     * @param team - L'équipe propriétaire du tool
     * 
     * @return L'instance de Tool créée, ou null en cas d'erreur
     */
    private static Item createToolByName(String toolName, Team team) {
        try {
            String cleanToolName = toolName.trim().replaceAll(" ", "");
            String className = "model.items.tools." + cleanToolName;
            Class<?> toolClass = Class.forName(className);
            return (Tools) toolClass.getConstructor(Team.class).newInstance(team);
        } catch (Exception e) {
            System.out.println("Failed to create tool: " + toolName);
            return null;
        }
    }

    /**
     * Méthode de récupération de tous les fichiers de sauvegarde disponibles.
     * 
     * @return La liste des chemins des fichiers de sauvegarde
     */
    private static ArrayList<Path> getAllSaveFiles() {
        Path saveDirectory = Paths.get(SAVE_DIR);

        if (!Files.exists(saveDirectory) || !Files.isDirectory(saveDirectory)) {
            return new ArrayList<>();
        }

        try {
            return Files.list(saveDirectory)
                    .filter(path -> path.toString().endsWith(FILE_EXTENSION))
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Méthode d'affichage des fichiers de sauvegarde disponibles.
     * 
     * @param saveFiles - La liste des chemins des fichiers de sauvegarde
     */
    private static void showAvailableSaves(ArrayList<Path> saveFiles) {
        System.out.println("Sauvegardes disponibles :");
        for (int i = 0; i < saveFiles.size(); i++) {
            System.out.println((i + 1) + ") " + saveFiles.get(i).getFileName());
        }
    }

    /**
     * Méthode pour demander à l'utilisateur de choisir un fichier de sauvegarde.
     * 
     * @param console - Le BufferedReader pour lire l'entrée utilisateur
     * @param maxChoice - Le nombre maximum de choix disponibles
     * 
     * @throws IOException En cas d'erreur de lecture
     * 
     * @return L'index du fichier choisi, ou -1 en cas d'erreur
     */
    private static int getUserFileChoice(BufferedReader console, int maxChoice) throws IOException {
        int choice = -1;
        do {
            choice = InputValidator.checkIsInt(new Scanner(System.in), "Choisissez une sauvegarde (numéro) : ");
            if (choice < 1 || choice > maxChoice) {
                System.out.println("Choix invalide.");
                choice = -1;
            }
        } while (choice == -1);
        return choice - 1;
    }
}