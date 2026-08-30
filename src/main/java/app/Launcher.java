package app;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import controller.GameController;
import controller.console.ConsoleController;
import controller.gui.GuiController;
import model.Config;
import model.GameModel;
import view.GameView;
import view.console.ConsoleHelper;
import view.console.ConsoleView;
import view.gui.GuiView;

/**
 * Classe Launcher gérant l'initialisation du jeu.
 * 
 * @see ArrayList#getClass()
 * 
 * @see Config#getClass()
 *
 * @since 1.1
 * 
 * @version 2.0
 */
public class Launcher {

    /**
     * Création d'une classe pour la configuration des paramètres généraux du jeu
     */
    public static Config CONFIG = new Config();

    /**
     * Création d'un supplier pour instancier le GameModel (létat global du jeu) facilement
     */
    public static Supplier<GameModel> modelFactory = GameModel::new;

    /**
     * Création d'une Function pour instancier le ConsoleView (l'affichage du jeu en vue console) facilement
     */
    public static Function<GameModel, ConsoleView> consoleViewFactory = ConsoleView::new;

    /**
     * Création d'une Function pour instancier le GuiView (l'affichage du jeu en vue graphique) facilement
     */
    public static Function<GameModel, GuiView> guiViewFactory = GuiView::new;

    /**
     * Création d'une BiFunction pour instancier le ConsoleController () facilement
     */
    public static BiFunction<GameModel, ConsoleView, ConsoleController> consoleCtrlFactory = ConsoleController::new;

    /**
     * Création d'une BiFunction pour instancier le GuiController facilement
     */
    public static BiFunction<GameModel, GuiView, GuiController> guiCtrlFactory = GuiController::new;

    /**
     * Méthode pour l'initialisation de tout les composants du jeu.
     * 
     * @param args Les arguments du programme
     * 
     * @throws Exception Gestion de l'exception
     * 
     * @see ArrayList#add(Object)
     * @see ArrayList#contains(Object)
     * @see ConsoleController#closeScanner()
     * @see ConsoleController#ConsoleController(GameModel, ConsoleView)
     * @see ConsoleController#setDevMode(boolean)
     * @see ConsoleHelper#setDevMode(boolean)
     * @see ConsoleView#showQuitMessage()
     * @see GameModel#GameModel()
     * @see GameView#setModel(GameModel)
     * @see GuiController#GuiController(GameModel, GuiView)
     * 
     * @since 1.1
     */
    public static void launch(String[] args) throws Exception {
        ArrayList<String> argsList = new ArrayList<>();
        for (String arg : args) {
            argsList.add(arg);
        }

        GameModel model = modelFactory.get();
        GameView view = null;
        GameController controller = null;

        boolean devMode = argsList.contains("dev");

        if (argsList.contains("console")) {
            ConsoleView consoleView = consoleViewFactory.apply(model);
            consoleView.setModel(model);
            view = consoleView;
            controller = consoleCtrlFactory.apply(model, consoleView);
            model.addView(consoleView);
        } else {
            GuiView guiView = guiViewFactory.apply(model);
            guiView.setModel(model);
            view = guiView;
            controller = guiCtrlFactory.apply(model, guiView);
            model.addView(guiView);
        }

        view.setDevMode(devMode);
        ConsoleHelper.setDevMode(devMode);
        controller.setDevMode(devMode);

        if (controller instanceof ConsoleController consoleController) {
            consoleController.startApplication();
            try {
                ConsoleController.closeScanner();
            } catch (Exception exception) {
                System.out.println("Problème lors de la fermeture du Scanner : " + exception);
            }
        }
    }
}
