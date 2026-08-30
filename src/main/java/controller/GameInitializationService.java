package controller;

import java.util.ArrayList;
import java.util.Scanner;

import controller.console.InputValidator;
import model.GameModel;
import model.Map;
import model.players.Team;
import model.players.Worm;
import view.console.ConsoleHelper;

/**
 * Classe permettant d'initialiser le jeu.
 * 
 * @author ARNAUD Hugo
 * @author MESNILDREY Valentin
 * 
 * @see Math#getClass()
 * @see Scanner#getClass()
 * 
 * @see GameModel#getClass()
 * @see GameSettings#getClass()
 * @see InputValidator#getClass()
 * @see Team#getClass()
 * 
 * @since 1.1
 * 
 * @version 2.0
 */
public class GameInitializationService {

    /**
     * Classes statique permettant de définir les paramètres de la partie.
     * 
     * @see Math#getClass()
     * 
     * @since 1.1
     */
    public static class GameSettings {

        private final int nbTeam;
        private final int nbWormsPerTeam;
        private final int mapWidth;
        private final int mapHeight;

        // ========================Builders========================//

        /**
         * Constructeur pour définir les paramètres de la partie à partir du nombre
         * d'équipes, le nombre de worms par équipes et un boolean pour le devMode.
         * 
         * @param nbTeam         Le nombre d'équipes
         * @param nbWormsPerTeam Le nombre de worms par équipe
         * @param devMode        Un boolean représentant le devMode
         * 
         * @see Math#max(int, int)
         * 
         * @since 1.1
         */

        public GameSettings(int nbTeam, int nbWormsPerTeam, boolean devMode) {
            this.nbTeam = nbTeam;
            this.nbWormsPerTeam = nbWormsPerTeam;
            this.mapHeight = 13;
            this.mapWidth = Math.max(20, (int) (this.nbTeam * this.nbWormsPerTeam * 1.7) + 6);
        }

        // ========================Accessors========================//

        /**
         * Getter pour le nombre d'équipes.
         * 
         * @return Le nombre d'équipes
         * 
         * @since 1.1
         */
        public int getNbTeam() {
            return this.nbTeam;
        }

        /**
         * Getter pour le nombre de worms par équipes.
         * 
         * @return Le nombre de worms par équipes
         * 
         * @since 1.1
         */
        public int getNbWormsPerTeam() {
            return this.nbWormsPerTeam;
        }

        /**
         * Getter pour la largeur de la carte.
         * 
         * @return La largeur de la carte
         * 
         * @since 1.1
         */
        public int getMapWidth() {
            return this.mapWidth;
        }

        /**
         * Getter pour la hauteur de la carte.
         * 
         * @return La hauteur de la carte
         * 
         * @since 1.1
         */
        public int getMapHeight() {
            return this.mapHeight;
        }
    }

    private final Scanner scanner;
    private final boolean devMode;
    private final GameModel model;

    /**
     * Constructeur pour créer un nouvel initialiser à partir d'un Sanner et d'un
     * boolean représentant le devMode.
     * 
     * @param scanner Le Scanner principal
     * @param model   Le GameModel de la partie
     * @param devMode Un boolean représentant le devMode
     * 
     * @since 1.1
     */
    public GameInitializationService(Scanner scanner, GameModel model, boolean devMode) {
        this.scanner = scanner;
        this.model = model;
        this.devMode = devMode;
    }

    /**
     * Méthode pour créer un nouveau modèle qui sera utilisé pour une nouvelle
     * partie.
     * 
     * @return Le nouveau modèle qui sera utilisé
     * 
     * @see GameInitializationService#initMap(GameSettings)
     * @see GameInitializationService#initTeams(GameSettings, Scanner, boolean)
     * @see GameModel#GameModel(ArrayList, Map, boolean)
     * @see GameModel#setCurrentWorm(Worm)
     * @see GameSettings#GameSettings(int, int, boolean)
     * @see InputValidator#checkIsInt(Scanner, String)
     * @see Team#placeWormsOnMap(Map)
     * 
     * @since 1.1
     */
    public GameModel initializeNewGameConsole() {
        int nbTeam, nbWormsPerTeam;

        if (!this.devMode) {
            nbTeam = Math.max(2,
                    InputValidator.checkIsInt(this.scanner, "Entrez le nombre d'équipes (au moins 2) : \n> "));
            nbWormsPerTeam = Math.max(1,
                    Math.min(8, InputValidator.checkIsInt(this.scanner,
                            "Entrez le nombre de vers par équipe (maximum 8) :\n> ")));
        } else {
            nbTeam = 2;
            nbWormsPerTeam = 2;
        }

        GameSettings settings = new GameSettings(nbTeam, nbWormsPerTeam, this.devMode);

        ArrayList<Team> teams = this.initTeams(settings, this.scanner, this.devMode);
        Map map = this.initMap(settings);

        for (Team t : teams) {
            t.placeWormsOnMap(map);
        }

        GameModel model = new GameModel(teams, map, true);
        model.setCurrentTeam(model.getTeams().get(0));
        model.setCurrentWorm(model.getTeams().get(0).getWorms().get(0));

        return model;
    }

    ArrayList<Team> initTeams(GameSettings settings, Scanner scanner, boolean devMode) {
        int nbTeam = settings.getNbTeam();
        int nbWormsPerTeam = settings.getNbWormsPerTeam();
        ArrayList<Team> teams = new ArrayList<>(nbTeam);

        char[] TEAM_SYMBOLS = new char[nbTeam];
        char initChar = 'A';

        if (!devMode) {
            System.out.println("Entrez le nom des équipes :");
            for (int i = 0; i < nbTeam; i++) {
                TEAM_SYMBOLS[i] = (char) (initChar + i);
                System.out.print("Nom de l'équipe " + (i + 1) + " : ");
                String name = scanner.nextLine();
                if (name.isEmpty()) {
                    name = "Team" + (i + 1);
                }
                teams.add(new Team(name, i, nbWormsPerTeam, model));
            }
            try {
                ConsoleHelper.sleepTime(0);
            } catch (InterruptedException exception) {
            }
        } else {
            teams.add(new Team("Player", 1, nbWormsPerTeam, model));
            teams.add(new Team("Bot", 2, nbWormsPerTeam, model));
        }

        return teams;
    }

    Map initMap(GameSettings settings) {
        Map map = new Map(settings.getMapHeight(), settings.getMapWidth());
        map.generateDefaultMap();
        return map;
    }
}
