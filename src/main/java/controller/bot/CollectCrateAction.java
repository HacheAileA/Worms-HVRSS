package controller.bot;

import javax.swing.Timer;
import model.GameModel;
import model.items.crates.Crate;
import model.players.Worm;

/**
 * Class d'action du bot pour qu'il aille chercher des crates.
 * 
 * @author MESNILDREY Valentin
 * @since 2.1
 * @version 2.1
 */
public class CollectCrateAction implements BotAction {
    /** La crate voulu */
    private final Crate crate;

    /**
     * Constructeur pour que le bot aille collecter les crates sur le terrain
     * @param crate la crate repérée
     * 
     * @since 2.1
     */
    public CollectCrateAction(Crate crate) {
        this.crate = crate;
    }

    @Override
    public void execute(GameModel model) {
        Worm w = model.getCurrentWorm();
        
        int direction = crate.getX() > w.getX() ? 1 : -1;
        double distance = Math.abs(crate.getX() - w.getX());
        double duration = Math.min(distance / 100.0, 1.0);

        boolean shouldJump = false;
        int checkX = (int) w.getX() + direction;
        int checkY = (int) w.getY();
        
        if (checkX >= 0 && checkX < model.getMap().getWidth() && 
            checkY + 1 >= 0 && checkY + 1 < model.getMap().getHeight()) {
            shouldJump = model.getMap().isGround(checkX, checkY + 1);
        }
        
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