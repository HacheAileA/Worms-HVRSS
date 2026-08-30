package controller.console;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import app.Launcher;
import controller.GameController;
import controller.GameInitializationService;
import model.Config;
import model.GameModel;
import model.items.Inventory;
import model.items.Item;
import model.items.guns.Guns;
import model.items.tools.Tools;
import model.persistence.LoadManager;
import model.persistence.LoadManager.LoadResult;
import model.persistence.SaveManager;
import model.players.Team;
import model.players.Worm;
import view.console.ConsoleHelper;
import view.console.ConsoleView;

/**
 * Classe qui sert à définir le controlleur pour le mode console.
 * 
 * @author ARNAUD Hugo
 * @author MESNILDREY Valentin
 * @author SAMBA Seth-Ederik
 * 
 * @see ArrayList#getClass()
 * @see InputStream#close()
 * @see Scanner#getClass()
 * 
 * @see Config#getClass()
 * @see ConsoleHelper#getClass()
 * @see ConsoleView#getClass()
 * @see GameInitializationService#getClass()
 * @see GameModel#getClass()
 * @see InputValidator#getClass()
 * @see Inventory#getClass()
 * @see LoadManager#getClass()
 * @see LoadResult#getClass()
 * @see SaveManager#save(GameModel, String, Team)
 * @see Team#getClass()
 * @see Tools#getClass()
 * @see Worm#getClass()
 * 
 * @since 1.1
 * 
 * @version 2.0
 */
public class ConsoleController implements GameController {

    /**
     * Le Scanner de la classe à utiliser
     */
    public final Scanner SCANNER;

    /**
     * Le modèle de la classe à utiliser.
     */
    public GameModel model;

    /**
     * La vue de la classe à utiliser.
     */
    public ConsoleView view;

    /**
     * Boolean représentant le devMode (vers lequel on peut switcher).
     */
    public boolean devMode;

    /**
     * Le délai entre les animations textuelles.
     */
    public int delay;

    /**
     * Constructeur pour créer un controlleur à partir d'un modèle et d'une vue.
     * 
     * @param model - Le modèle à utiliser
     * @param view - La vue à utiliser
     * 
     * @see Scanner#Scanner(java.io.InputStream)
     * @see Config#Config()
     * @see Config#getIntParameter(String)
     * 
     * @since 1.1
     */
    public ConsoleController(GameModel model, ConsoleView view) {
        this.model = model;
        this.view = view;
        this.SCANNER = new Scanner(System.in);
        this.delay = Launcher.CONFIG.getIntParameter("DELAY");
    }

    /**
     * Méthode statique qui permet de fermer tous les scanners.
     * 
     * @throws IOException Gestion de l'exception
     * 
     * @see InputStream#close()
     * 
     * @since 1.1
     */
    public static void closeScanner() throws IOException {
        System.in.close();
    }

    /**
     * Méthode pour savoir si le jeu est en mode développeur ou non.
     * 
     * @param devMode - Un boolean pour activer le devMode (true), ou le dédésactiver (false)
     * 
     * @since 1.1
     */
    @Override
    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }

    /**
     * Méthode pour démarrer l'application.
     * 
     * @throws InterruptedException Gestion de l'exception
     * 
     * @see Config#getIntParameter(String)
     * @see ConsoleController#run()
     * @see ConsoleController#startNewGame()
     * @see ConsoleHelper#clearConsole()
     * @see ConsoleHelper#sleepTime(int)
     * @see ConsoleView#showFailLoad()
     * @see ConsoleView#showIncorrectChoiceMessage()
     * @see ConsoleView#showMainMenu()
     * @see ConsoleView#showNextFeature()
     * @see ConsoleView#showQuitMessage()
     * @see InputValidator#checkIsInt(Scanner, String)
     * @see LoadManager#load()
     * @see LoadResult#getClass()
     * 
     * @since 1.1
     */
    public void startApplication() throws InterruptedException {
        view.showStartGameMessage();
        boolean launcher = false;

        while (!launcher) {
            this.view.showMainMenu();
            int choice = InputValidator.checkIsInt(SCANNER, "Choisissez une option : ");

            switch (choice) {
                case 1:
                    LoadManager.LoadResult result = LoadManager.load();

                    if (result != null) {
                        this.model = result.model;
                        this.view.setModel(this.model);
                        launcher = true;
                    } else {
                        this.view.showFailLoad();
                        ConsoleHelper.sleepTime(app.Launcher.CONFIG.getIntParameter("DELAY") * 2);
                        ConsoleHelper.clearConsole();
                    }
                    break;

                case 2:
                    ConsoleHelper.clearConsole();
                    this.startNewGame();
                    launcher = true;
                    break;

                case 3:
                    this.view.showQuitMessage();
                    return;

                default:
                    this.view.showIncorrectChoiceMessage();
                    ConsoleHelper.sleepTime(app.Launcher.CONFIG.getIntParameter("DELAY") * 2);
                    ConsoleHelper.clearConsole();
            }
        }

        this.run();
    }

    /**
     * Méthode pour démarrer un nouveau jeu.
     * 
     * @throws InterruptedException Gestion de l'exception
     * 
     * @see ConsoleController#createGameInitializationService()
     * @see ConsoleView#setModel(GameModel)
     * @see ConsoleView#showGameInitializationRecap()
     * @see GameInitializationService#initializeNewGameConsole()
     * 
     * @since 1.1
     */
    public void startNewGame() throws InterruptedException {
        this.model = createGameInitializationService().initializeNewGameConsole();
        this.view.setModel(this.model);
        this.view.showGameInitializationRecap();
    }

    /**
     * Méthode annexe pour créer l'initialisation (uti pour les tests).
     * 
     * @see GameInitializationService#GameInitializationService(Scanner, GameModel, boolean)
     * 
     * @return Un GameInitializationService
     * 
     * @since 2.1
     */
    protected GameInitializationService createGameInitializationService() {
        return new GameInitializationService(this.SCANNER, this.model, this.devMode);
    }

    /**
     * Méthode pour jouer au jeu.
     * 
     * @throws InterruptedException Gestion de l'exception
     * 
     * @see ArrayList#addAll(java.util.Collection)
     * @see ArrayList#ArrayList()
     * @see ArrayList#get(int)
     * @see ArrayList#size()
     * @see Scanner#nextLine()
     * 
     * @see ConsoleController#actionChoices()
     * @see ConsoleController#gunsChoices()
     * @see ConsoleHelper#clearConsole()
     * @see ConsoleHelper#sleepTime(int)
     * @see ConsoleView#showEndGameMessage()
     * @see ConsoleView#showGameModelInfo()
     * @see ConsoleView#showIncorrectChoiceMessage()
     * @see ConsoleView#showMap()
     * @see ConsoleView#showNewTurnInfo()
     * @see ConsoleView#showShootMessage()
     * @see ConsoleView#showTeamInfo()
     * @see ConsoleView#showWormInfo(Worm)
     * @see GameModel#deleteWorm(Worm)
     * @see GameModel#getMap()
     * @see GameModel#getWormByName(String)
     * @see GameModel#isGameOver()
     * @see GameModel#update()
     * @see InputValidator#getStringOrCancel(Scanner, String)
     * @see Inventory#getAvailableGuns()
     * @see Inventory#getAvailableTools()
     * @see Inventory#removeToolNoAmmo()
     * @see SaveManager#save(GameModel, String, Team)
     * @see Team#getWorms()
     * @see Tools#useTool(Worm)
     * @see Worm#isDead()
     * 
     * @since 1.1
     */
    public void run() throws InterruptedException {
        Worm currentWorm = this.model.getTeams().get(0).getWorms().get(0);
        Team currentTeam = this.model.getTeams().get(0);
        while (!this.model.isGameOver()) {
            if (currentTeam == null || currentWorm == null) {
                break;
            }

            ConsoleHelper.clearConsole();
            this.view.showGameModelInfo();
            System.out.println();
            this.view.showMap();

            boolean turnFinish = false;
            while (!turnFinish) {
                this.view.showNewTurnInfo();
                int choiceAction = this.actionChoices();

                switch (choiceAction) {
                    case 1:
                        String direction = InputValidator.getStringOrCancel(this.SCANNER,
                                "Déplacement (gauche/droite) :");
                        if (direction == null) {
                            ConsoleHelper.clearConsole();
                            this.view.showGameModelInfo();
                            this.view.showMap();
                            continue;
                        }
                        boolean hasMoved = false;
                        switch (direction) {
                            case "gauche":
                                hasMoved = currentWorm.moveLeft(this.model.getMap());
                                break;

                            case "droite":
                                hasMoved = currentWorm.moveRight(this.model.getMap());
                                break;

                            default:
                                this.view.showIncorrectChoiceMessage();
                        }
                        ConsoleHelper.clearConsole();
                        if (currentWorm.isDead()) {
                            turnFinish = true;
                        }
                        this.view.showGameModelInfo();
                        this.view.showMap();

                        if (!hasMoved) {
                            System.out.println("Déplacement impossible !");
                        }
                        break;

                    case 2:
                        String dir = InputValidator.getStringOrCancel(this.SCANNER, "Saut (gauche/droite) :");
                        if (dir == null) {
                            ConsoleHelper.clearConsole();
                            this.view.showGameModelInfo();
                            this.view.showMap();
                            continue;
                        }
                        boolean hasJumped = false;
                        switch (dir) {
                            case "gauche":
                                hasJumped = currentWorm.jumpLeft(this.model.getMap(), model);
                                break;

                            case "droite":
                                hasJumped = currentWorm.jumpRight(this.model.getMap(), model);
                                break;

                            default:
                                this.view.showIncorrectChoiceMessage();
                        }
                        ConsoleHelper.clearConsole();
                        if (currentWorm.isDead()) {
                            turnFinish = true;
                        }
                        this.view.showGameModelInfo();
                        this.view.showMap();

                        if (!hasJumped) {
                            System.out.println("Saut impossible !");
                            this.view.showGameModelInfo();
                            this.view.showMap();
                        }
                        break;

                    case 3:
                        ConsoleHelper.clearConsole();
                        Inventory inventory = currentTeam.getInventory();
                        int choiceItem = this.gunsChoices();
                        if (choiceItem == -1) {
                            break;
                        }
                        ArrayList<Item> orderedItems = new ArrayList<>();
                        orderedItems.addAll(inventory.getAvailableGuns());
                        orderedItems.addAll(inventory.getAvailableTools());
                        if (choiceItem < 1 || choiceItem > orderedItems.size()) {
                            this.view.showIncorrectChoiceMessage();
                            break;
                        }

                        Item selected = orderedItems.get(choiceItem - 1);

                        if (selected instanceof Guns gun) {

                            boolean isNameCorrect = false;
                            Worm opponent = null;
                            while (!isNameCorrect) {
                                this.view.showShootMessage();
                                for (Team team : this.model.getTeams()) {
                                    if (team != model.getCurrentWorm().getTeam()) {
                                        for (Worm worm : team.getWorms()) {
                                            this.view.showWormInfo(worm);
                                            System.out.print(" | ");
                                        }
                                    }
                                }
                                System.out.println();

                                String target = this.SCANNER.nextLine();
                                Worm found = this.model.getWormByName(target);

                                if (found != null) {
                                    isNameCorrect = true;
                                    opponent = found;
                                } else {
                                    this.view.showIncorrectChoiceMessage();
                                    ConsoleHelper.sleepTime(delay);
                                    ConsoleHelper.clearConsole();
                                }
                            }

                            Guns.shootConsole(gun, opponent);
                            inventory.removeGunNoAmmo();

                            if (opponent.isDead()) {
                                this.model.deleteWorm(opponent);
                            }

                        } else if (selected instanceof Tools tool) {
                            tool.useTool(currentWorm);
                            inventory.removeToolNoAmmo();
                        }

                        ConsoleHelper.sleepTime(delay * 2);
                        turnFinish = true;
                        break;

                    case 4:
                        turnFinish = true;
                        break;

                    case 5:
                        SaveManager.saveWithPrompt(this.model, currentTeam);
                        return;

                    default:
                        this.view.showIncorrectChoiceMessage();
                        if (!this.devMode) {
                            ConsoleHelper.sleepTime(2000);
                        }
                        System.out.println();
                        ConsoleHelper.clearConsole();
                        this.view.showGameModelInfo();
                        System.out.println();
                        this.view.showMap();
                }
            }

            this.view.showTeamInfo();

            ConsoleHelper.clearConsole();
            if (this.model.isGameOver()) {
                this.view.showEndGameMessage();
                System.out.println();
                break;
            }

            model.nextTurn();
            currentWorm = model.getCurrentWorm();
            currentTeam = model.getCurrentTeam();
        }
    }

    /**
     * Méthode pour afficher les actions possibles au début de chaque tour.
     * 
     * @see ConsoleView#showActionsMenu()
     * @see InputValidator#checkIsInt(Scanner, String)
     * 
     * @return Un entier qui représente le choix effectué
     * 
     * @since 1.1
     */
    public int actionChoices() {
        this.view.showActionsMenu();
        return InputValidator.checkIsInt(this.SCANNER, "Choisissez une action : ");
    }

    /**
     * Méthode pour afficher les armes disponibles dans l'Arsenal.
     * 
     * @see ConsoleView#showInventory()
     * @see InputValidator#checkIsInt(Scanner, String)
     * 
     * @return Un entier représentant le numéro de l'arme choisie
     * 
     * @since 1.1
     */
    public int gunsChoices() {
        this.view.showInventory();
        return InputValidator.checkIsInt(this.SCANNER,
                "Choisissez un objet de votre inventaire à utiliser (ou 'retour') : ");
    }
}
