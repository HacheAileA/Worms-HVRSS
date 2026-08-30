package controller.bot;

import model.GameModel;
import model.items.guns.Guns;
import model.physics.Projectile;
import model.players.Worm;
import view.gui.GuiView;

/**
 * Classe permettant de tirer un projectile en version bot
 * 
 * @author MESNILDREY Valentin
 * 
 * @see GameModel#getClass()
 * @see GuiView#getClass()
 * 
 * @since 2.1
 * 
 * @version 2.1
 */
public class ShootAction implements BotAction {
    private final Guns gun;
    private final double angle;
    private final GuiView view;

    /**
     * Constructeur permettant d'initialiser le gun, l'angle et la vue
     * 
     * @param gun - Permettra d'utiliser la fonction shoot de la classe Worm
     * @param angle - Permettra de définir l'angle de tir
     * @param view - Permettra de lancer l'animation du projectile
     * 
     * @since 2.1
     */
    public ShootAction(Guns gun, double angle, GuiView view) {
        this.gun = gun;
        this.angle = angle;
        this.view = view;
    }

    @Override
    public void execute(GameModel model) {
        Worm w = model.getCurrentWorm();
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        w.setSelectedItem(gun);
        
        double delta = angle - w.getAimAngle();
        w.rotateAim(delta);
        
        view.startProjectileTimer();
        
        Projectile p = w.shoot(model.getMap(), 0);
        if (p != null) {
            model.getProjectiles().add(p);
        }
    }
}