package controller.bot;

import model.GameModel;
import model.items.tools.Tools;
import model.items.tools.AirStrike;
import model.players.Worm;

/**
 * Classe d'action pour le bot. Utilisation d'un outil
 * 
 * @author MESNILDREY Valentin
 * 
 * @since 2.1
 * @version 2.1
 */
public class UseToolAction implements BotAction {
    /** l'outil utilisé */
    private final Tools tool;
    /** Le worm qui utilise l'outil */
    private final Worm user;
    /** La localisation ciblé avec l'outil */
    private final Integer targetX;

    /**
     *  Constructeur "classique" pour que le bot utilise un outil
     * @param tool outil utilisé
     * @param user worm qui fait l'action
     * 
     * @since 2.1
     */
    public UseToolAction(Tools tool, Worm user) {
        this.tool = tool;
        this.user = user;
        this.targetX = null;
    }

    /**
     * Constructeur pour que le bot utilise un outil (spécialement pour le airStrike)
     * @param tool L'outil utilisé
     * @param user Le worm qui utilise l'outil
     * @param targetX La cible visée
     * 
     * @since 2.1
     */
    public UseToolAction(Tools tool, Worm user, int targetX) {
        this.tool = tool;
        this.user = user;
        this.targetX = targetX;
    }

    @Override
    public void execute(GameModel model) {
        if (tool.getName().equalsIgnoreCase("Air Strike") && targetX != null) {
            if (tool instanceof AirStrike airStrike) {
                airStrike.useTool(user, targetX);
            }
        } else {
            tool.useTool(user);
        }
    }
}