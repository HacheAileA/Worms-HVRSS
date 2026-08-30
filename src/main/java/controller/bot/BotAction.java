package controller.bot;

import model.GameModel;

/**
 * Interface qui représente une action qu'un bot peut exécuter.
 * 
 * @author MESNILDREY Valentin
 * 
 * @see GameModel#getClass()
 * 
 * @since 2.1
 * 
 * @version 2.1
 */
@FunctionalInterface
public interface BotAction {
    /**
     * Exécute l'action que doit effectuer le bot sur le modèle de jeu donné.
     * 
     * @param model - Le modèle de jeu sur lequel l'action sera exécutée
     * 
     * @since 2.1
     */
    void execute (GameModel model);
}
