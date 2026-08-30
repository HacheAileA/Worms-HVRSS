package controller.bot;

import javax.swing.Timer;
import model.GameModel;
import model.players.Worm;

/**
 * Classe qui représente une action de déplacement pour un bot.
 * 
 * @author MESNILDREY Valentin
 * 
 * @see Worm#getClass()
 * @see Timer#getClass()
 * 
 * @since 2.1
 * 
 * @version 2.1
 */
public class MoveAction implements BotAction {
    private final int direction;
    private final double duration;
    private final boolean shouldJump;

    /**
     * Constructeur qui permet d'initialiser la direction, la durée et si le ver doit sauter ou non
     * 
     * @param direction - Permet de définir la direction du déplacement (-1 pour gauche, 1 pour droite)
     * @param duration - Permet de définir la durée du déplacement en secondes
     * @param shouldJump - Permet de définir si le ver doit sauter avant de se déplacer
     * 
     * @since 2.1
     */
    public MoveAction(int direction, double duration, boolean shouldJump) {
        this.direction = direction;
        this.duration = duration;
        this.shouldJump = shouldJump;
    }

    @Override
    public void execute(GameModel model) {
        Worm w = model.getCurrentWorm();
        
        if (shouldJump) {
            w.jumpSmooth(model.getMap());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        if (direction < 0) {
            w.startMoveLeft();
        } else {
            w.startMoveRight();
        }
        
        Timer moveTimer = new Timer((int) (duration * 1000), e -> {
            w.stopMove();
            ((Timer) e.getSource()).stop();
        });
        moveTimer.setRepeats(false);
        moveTimer.start();
    }
}
