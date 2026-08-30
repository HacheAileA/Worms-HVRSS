package view.console;

import java.util.ArrayList;

import model.GameModel;
import model.Map;
import model.items.Arsenal;
import model.items.Inventory;
import model.items.guns.Guns;
import model.items.tools.Tools;
import model.players.Team;
import model.players.Worm;
import view.GameView;

/**
 * Classe ConsoleView qui gère l'affichage du jeu en vue console.
 * 
 * @author ARNAUD Hugo
 * 
 * @see ArrayList#getClass()
 * @see Arsenal#getClass()
 * @see ConsoleHelper#getClass()
 * @see GameModel#getClass()
 * @see Team#getClass()
 * @see Worm#getClass()
 * @see Guns#getClass()
 * @see Tools#getClass()
 * @see Inventory#getClass()
 * 
 * @since 1.0
 * 
 * @version 1.1
 */
public class ConsoleView implements GameView {

    private GameModel model;
    private boolean devMode;

    /**
     * Constructeur de la classe ConsoleView.
     * 
     * @param model - Le GameModel à associer à la vue
     * 
     * @see ConsoleHelper#clearConsole()
     * @see ConsoleView#showStartGameMessage()
     * @see GameModel#getClass()
     * 
     * @since 1.1
     */
    public ConsoleView(GameModel model) {
        ConsoleHelper.clearConsole();
    }

    /**
     * Setter du model du jeu.
     * 
     * @param model - Le GameModel à associer à la vue
     * 
     * @see GameModel#getClass()
     * 
     * @since 1.0
     */
    public void setModel(GameModel model) {
        this.model = model;
    }

    /**
     * Setter du mode développeur (des.activation).
     * 
     * @param devMode - Le boolean représentant le mode développeur
     * 
     * @since 1.1
     */
    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }

    // ========================Methods========================//

    /**
     * Méthode qui affiche un message de fermeture du jeu.
     * 
     * @since 1.0
     */
    public void showQuitMessage() {
        System.out.println("Fermeture du jeu en cours...");
    }

    // ========================Menu========================//

    /**
     * Méthode pour afficher un menu, représenté sous forme de String[].
     * 
     * @param menu - Le menu à afficher
     * 
     * @see ArrayList#size()
     * @see ArrayList#get(int)
     * 
     * @since 1.1
     */
    private void showMenu(String[] menu) {
        System.out.println();
        for (int i = 0; i < menu.length; i++) {
            System.out.println((i + 1) + ". " + menu[i]);
        }
    }

    /**
     * Méthode pour afficher le menu principal.
     * 
     * @see ConsoleView#showMenu(String[])
     * 
     * @since 1.1
     */
    public void showMainMenu() {
        System.out.print("Menu principal :");
        final String[] menu = new String[3];
        menu[0] = "Continuer une partie sauvegardée";
        menu[1] = "Démarrer une nouvelle partie";
        menu[2] = "Quitter l'application";

        showMenu(menu);
    }

    /**
     * Méthode qui affiche le menu des actions disponibles.
     * 
     * @see ConsoleView#showMenu(String[])
     * 
     * @since 1.1
     */
    public void showActionsMenu() {
        System.out.print("Actions disponibles :");
        final String[] menu = new String[5];
        menu[0] = "Déplacer";
        menu[1] = "Sauter";
        menu[2] = "Ouvrir votre inventaire";
        menu[3] = "Passer le tour";
        menu[4] = "Sauvegarder et quitter la partie";

        showMenu(menu);
    }

    /**
     * Méthode qui affiche le menu des items disponibles.
     * 
     * @see ConsoleView#showMenu(String[])
     * 
     * @since 1.1
     */
    public void showItemChoiceMenu() {
        final String[] menu = new String[0];

        showMenu(menu);
    }

    // ========================Message========================//

    /**
     * Méthode qui affiche le texte au lancement du jeu.
     * 
     * @throws InterruptedException Gestion de l'exception
     * 
     * @see ConsoleHelper#sleepTime(int)
     * 
     * @since 1.0
     */
    public void showStartGameMessage() throws InterruptedException {
        final String startText = "=== Bienvenue dans le jeu Worms (console) ===";
        if (!this.devMode) {
            final int LONG_DELAY_INDEX_1 = 31;
            final int LONG_DELAY_INDEX_2 = 40;
            final int SHORT_DELAY = 80;
            final int LONG_DELAY = 900;
            for (int i = 0; i < startText.length(); i++) {
                System.out.print(startText.charAt(i));
                if (i == LONG_DELAY_INDEX_1 || i == LONG_DELAY_INDEX_2)
                    ConsoleHelper.sleepTime(LONG_DELAY);
                ConsoleHelper.sleepTime(SHORT_DELAY);
            }
            System.out.print("");
        } else {
            System.out.print(startText);
        }
        ConsoleHelper.sleepTime(1000);
        System.out.println("\n\n");
    }

    /**
     * Méthode qui affiche le message de fin de partieet l'équipe gagnante.
     * 
     * @see ConsoleView#showEndGameMessage()
     * @see Team#getName()
     * 
     * @since 1.0
     */
    public void showEndGameMessage() {
        Team winner = this.model.getWinningTeam();
        if (winner != null) {
            System.out.println("L'équipe gagnante est : " + winner.getName());
        } else {
            System.out.println("Aucune équipe n'est gagnante : Partie nulle");
        }
        System.out.println("\n=== Fin de la Partie ===");
    }

    /**
     * Méthode qui affiche une chaîne représentant le message lorsque le joueur souhaite tirer.
     * 
     * @since 1.0
     */
    public void showShootMessage() {
        System.out.println("Sur qui voulez-vous tirez ? (rentrez son nom)");
        System.out.println("Worms Adverse : ");
    }

    /**
     * Méthode qui affiche une chaîne lorsque l'entrée est incorrecte.
     * 
     * @since 1.0
     */
    public void showIncorrectChoiceMessage() {
        System.out.println("Choix invalide, réessayez.");
    }

    // ========================Other========================//

    /**
     * Méthode qui affiche le contenu de l'inventaire.
     * 
     * @see Inventory#getAvailableItems(Team)
     * @see model.items.guns.Guns#getAmmo()
     * @see model.items.tools.Tools#getAmmo()
     * @see model.items.Item#getName()
     * 
     * @since 1.1
     */
    public void showInventory() {

        System.out.println("Contenu de votre inventaire :\n");

        int index = 1;

        System.out.println("--- Armes ---");

        for (var item : this.model.getCurrentTeam().getInventory().getAvailableItems(model.getCurrentTeam())) {
            if (item instanceof Guns gun) {
                System.out.println(index + ") " + gun.getName() +
                        " (Uses left: " + gun.getAmmo() + ")");
                index++;
            }
        }

        System.out.println("\n--- Outils ---");

        for (var item : this.model.getCurrentTeam().getInventory().getAvailableItems(model.getCurrentTeam())) {
            if (item instanceof Tools tool) {
                System.out.println(index + ") " + tool.getName() +
                        " (Uses left: " + tool.getAmmo() + ")");
                index++;
            }
        }
        System.out.println();
    }

    // ========================Info========================//

    /**
     * Méthode qui affiche les informations sur le tour de jeu actuel.
     * 
     * @see Team#getColoredName()
     * @see Worm#getSymbol()
     * @see ConsoleView#showWormInfo(Worm)
     * 
     * @since 1.0
     */
    public void showNewTurnInfo() {
        System.out.println("--- Tour de " + this.model.getCurrentTeam().getColoredName() + " ---");
        System.out.print("Ver actif : (" + this.model.getCurrentWorm().getSymbol() + ") ");
        this.showWormInfo(this.model.getCurrentWorm());
        System.out.println("\n");
    }

    /**
     * Méthode qui affiche les informations de l'équipe.
     * 
     * @see ArrayList#get(int)
     * @see ArrayList#size()
     * @see Team#getName()
     * @see Team#getWorms()
     * 
     * @since 1.0
     */
    public void showTeamInfo() {
        System.out.print("\n\nEquipe " + this.model.getCurrentTeam().getColoredName() + " : ");
        for (int i = 0; i < this.model.getCurrentTeam().getWorms().size(); i++) {
            System.out.print("[");
            this.showWormInfo(this.model.getCurrentTeam().getWorms().get(i));
            System.out.print("]");
            if (i < this.model.getCurrentTeam().getWorms().size() - 1) {
                System.out.print(" => ");
            }
        }
        System.out.println("\n\n");
    }

    /**
     * Méthode qui affiche les informations du worm.
     * 
     * @param worm - Le Worm à afficher
     * 
     * @see Worm#getName()
     * @see Worm#getHp()
     * 
     * @since 1.0
     */
    public void showWormInfo(Worm worm) {
        System.out.print(worm.getName() + " -> " + worm.getHp() + "pv");
    }

    /**
     * Méthode qui affiche les informations de la partie.
     * 
     * @see ArrayList#get(int)
     * @see ArrayList#size()
     * @see Team#getColoredName()
     * @see Team#getWorms()
     * 
     * @since 1.0
     */
    public void showGameModelInfo() {
        if (this.model.getTeams() == null || this.model.getTeams().size() == 0) {
            System.out.println("Anything to print");
        } else {
            System.out.println("Informations des équipes :");
            for (Team t : this.model.getTeams()) {
                System.out.print("- " + t.getColoredName() + " (" + t.getWorms().size() + " worms) : ");
                for (int i = 0; i < t.getWorms().size(); i++) {

                    this.showWormInfo(t.getWorms().get(i));
                    System.out.print(" ");
                    if (i < t.getWorms().size() - 1) {
                        System.out.print("| ");
                    }
                }
                System.out.println();
            }
        }
        System.out.println();
    }

    /**
     * Méthode qui affiche la map.
     * 
     * @see Team#getColor()
     * @see Worm#getX()
     * @see Worm#getY()
     * @see AnsiColor#getColoredString(String, String)
     * 
     * @since 1.1
     */
    public void showMap() {
        for (int y = 0; y < this.model.getMap().grid.length; y++) {
            for (int x = 0; x < this.model.getMap().grid[y].length; x++) {
                char c = this.model.getMap().grid[y][x];
                String colored = " ";

                if (c == '~') {
                    colored = AnsiColor.getColoredString("BLUE", "~");
                } else if (c == '#') {
                    colored = AnsiColor.getColoredString("BROWN", "#");
                } else if (this.model.getMap().isWorm(x, y)) {
                    String colorName = "WHITE";
                    for (Team t : this.model.getTeams()) {
                        for (Worm w : t.getWorms()) {
                            if (w.getX() == x && w.getY() == y && !w.isDead()) {
                                colorName = t.getColor().toUpperCase();
                                break;
                            }
                        }
                        if (!colorName.equals("WHITE"))
                            break;
                    }

                    colored = AnsiColor.getColoredString(colorName, "" + c);
                }
                System.out.print(colored + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Méthode qui affiche un message pour les fonctionnalités non implémentées.
     * 
     * @since 1.1
     */
    public void showNextFeature() {
        System.out.println("Cette fonctionnalité n'a pas encore été implémentée. Merci de patientez un peu.");
    }

    /**
     * Méthode qui affiche un message d'erreur concernant le chargement d'une partie sauvegardé.
     * 2.0
     * 
     * @since 1.1
     */
    public void showFailLoad() {
        System.out.println("Impossible de charger la sauvegarde !");
    }

    /**
     * Méthode privée pour récupérer la couleur correspondant à une équipe.
     * 
     * @param team - La team demandée
     * 
     * @return Une chaîne réprensant le nom de la couleur de l'équipe
     * 
     * @since 1.1
     */
    private String getTeamColorName(Team team) {
        switch (team.getTeamId()) {
            case 0:
                return "RED";
            case 1:
                return "BLUE";
            case 2:
                return "GREEN";
            case 3:
                return "YELLOW";
            default:
                return "WHITE";
        }
    }

    /**
     * Méthode privée pour récupérer du texte en couleur.
     * 
     * @param color - Une chaîne de caractère représentant le nom de la couleur
     * @param text - Le texte à afficher
     * 
     * @see AnsiColor#getColoredString(String, String)
     * 
     * @return Le texte en couleur
     * 
     * @since 1.1
     */
    private String getColoredText(String color, String text) {
        return AnsiColor.getColoredString(color, text);
    }

    /**
     * Méthode affiche le récapitulatif de l'initialisation de la partie.
     * 2.0i
     * 
     * @see ConsoleHelper#sleepTime(int)
     * 
     * @since 1.1
     */
    public void showGameInitializationRecap() {
        System.out.println("\nLes équipes sont prêtes !\n");
        try {
            ConsoleHelper.sleepTime(500);
        } catch (InterruptedException exception) {
        }
        String teamName = "";
        for (int i = 0; i < this.model.getTeams().size(); i++) {
            Team team = this.model.getTeams().get(i);
            String color = this.getTeamColorName(team);
            String name = this.getColoredText(color, team.getName());
            teamName += name;
            if (i < this.model.getTeams().size() - 1) {
                teamName += " VS ";
            }
        }
        System.out.println(teamName + "\n\n");

        System.out.println("\n");
        try {
            ConsoleHelper.sleepTime(2000);
        } catch (InterruptedException exception) {
        }
        ConsoleHelper.clearConsole();
    }

    @Override
    public void onWormPlaced(Worm worm, Map map) {
        //Ne fait rien, fait exprès
    }
}
