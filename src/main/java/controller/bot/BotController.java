package controller.bot;

import javax.swing.Timer;

import model.GameModel;
import model.players.Worm;
import view.gui.GuiView;

/**
 * Classe qui gére les actions des bots dans le jeu.
 * 
 * @author MESNILDREY Valentin
 * 
 * @see BotDecisionEngine#getClass()
 * @see GuiView#getClass()
 * 
 * @since 2.1
 * 
 * @version 2.1
 */
public class BotController {
    private final BotDecisionEngine engine;

    /**
     * Constructeur permettant d'initialiser le moteur de décision du bot.
     * 
     * @param difficulty - La difficulté du bot (1: facile, 2: moyen, 3: difficile)
     * @param view - Vue graphique pour les animations
     * 
     * @since 2.1
     */
    public BotController(int difficulty, GuiView view) {
        this.engine = new BotDecisionEngine(difficulty, view);
    }

    /**
     * Permet de faire jouer le tour du bot en cours.
     * 
     * @param model - Le modèle de jeu actuel
     * 
     * @since 2.1
     */
    public void playTurn(GameModel model) {
        Worm self = model.getCurrentWorm();
        
        Timer botTimer = new Timer(500, null);
        botTimer.addActionListener(e -> {
            if (model.getCurrentWorm() != self) {
                ((Timer) e.getSource()).stop();
                return;
            }

            BotAction action = engine.decide(model, self);
            
            if (action != null) {
                action.execute(model);
            }
            
            if (action instanceof ShootAction || action == null) {
                ((Timer) e.getSource()).stop();
            }
        });
        botTimer.start();
    }
}
