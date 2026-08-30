package controller.gui;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import model.GameModel;
import model.items.guns.Bazooka;
import model.items.guns.Grenade;
import model.items.guns.Guns;
import model.items.guns.ShotGun;
import model.items.guns.Sniper;
import model.physics.Projectile;
import view.gui.GuiView;
import view.gui.MapRenderer;
import view.gui.Particle;

/**
 * Classe qui permettra de tirer un projectile en version graphique.
 * Gère maintenant les armes qui tirent plusieurs projectiles à la fois.
 * 
 * @author MESNILDREY Valentin
 * 
 * @see GameModel#getClass()
 * @see GuiView#getClass()
 * 
 * @since 2.0
 * 
 * @version 2.1
 */
public class ShootController {

    private final GameModel model;
    private final GuiView view;
    private boolean canShoot = true;

    /**
     * Constructeur qui permet d'initialiser le model et la vue
     * 
     * @param model - Permettra d'utiliser la fonction shoot de la classe Worm
     * @param view - Permettra de lancer l'animation du projectile
     */
    public ShootController(GameModel model, GuiView view) {
        this.model = model;
        this.view = view;
    }

    /**
     * Méthode qui permet de tirer un ou plusieurs projectiles ainsi que leur animation.
     * Gère les armes spéciales comme le ShotGun qui tire plusieurs projectiles.
     * 
     * @param mouseX - La position X de la souris lors du clic
     * 
     * @see GameModel#getCurrentWorm()
     * @see GameModel#getProjectiles()
     * @see GuiView#getInventoryPanel()
     * @see GuiView#startProjectileTimer()
     * 
     * @since 2.0
     * 
     * @version 2.1
     */
    private void shoot(double mouseX) {
        if (!canShoot)
            return;

        if (model.getCurrentWorm() == null)
            return;

        if (!model.getProjectiles().isEmpty())
            return;

        // Vérifier si l'arme sélectionnée est un Gun
        if (!(model.getCurrentWorm().getSelectedItem() instanceof Guns)) {
            // Si ce n'est pas un gun, utiliser la méthode shoot normale
            Projectile p = model.getCurrentWorm().shoot(model.getMap(), mouseX / MapRenderer.TILE_SIZE);
            if (p != null) {
                p.setWind(model.getWind());
                model.getProjectiles().add(p);

                if (view.getInventoryPanel() != null) {     
                    view.getInventoryPanel().refresh();
                }

                createShootParticles();
                view.getGamePanel().stopMovementTimer();
                view.startProjectileTimer();
                canShoot = false;
            }
            if (view.getInventoryPanel() != null) {     
                view.getInventoryPanel().refresh();
            }
            return;
        }

        // Si c'est un gun, utiliser la nouvelle méthode createProjectiles
        Guns gun = (Guns) model.getCurrentWorm().getSelectedItem();

        if (!gun.hasAmmo())
            return;

        // Créer les projectiles (1 ou plusieurs selon l'arme)
        List<Projectile> projectiles = gun.createProjectiles(model.getCurrentWorm());

        if (projectiles.isEmpty())
            return;

        // Ajouter tous les projectiles au modèle
        for (Projectile p : projectiles) {
            p.setWind(model.getWind());
            model.getProjectiles().add(p);
        }

        // Jouer le son approprié
        if (gun instanceof ShotGun)
            this.view.soundPlayer.playSoundEffect("/sounds/sounds_effects/shotgun.wav");
        else if (gun instanceof Sniper)
            this.view.soundPlayer.playSoundEffect("/sounds/sounds_effects/sniper.wav");
        else if (gun instanceof Bazooka)
            this.view.soundPlayer.playSoundEffect("/sounds/sounds_effects/bazooka.wav");
        else if (gun instanceof Grenade)
            this.view.soundPlayer.playSoundEffect("/sounds/sounds_effects/grenade.wav");

        // Rafraîchir l'interface
        if (view.getInventoryPanel() != null) {
            view.getInventoryPanel().refresh();
        }

        // Créer les particules
        createShootParticles();

        // Démarrer les animations
        view.getGamePanel().stopMovementTimer();
        view.startProjectileTimer();
        canShoot = false;
    }

    /**
     * Crée et gère les particules visuelles, lors du tir
     */
    private void createShootParticles() {
        for (int i = 0; i < 20; i++) {
            double angle = Math.random() * Math.PI - Math.PI / 2;
            double speed = Math.random() * 2 + 1;
            double dx = Math.cos(angle) * speed;
            double dy = Math.sin(angle) * speed;
            view.addParticles(new Particle(
                    (model.getCurrentWorm().getX() + 1.0 / 2) * MapRenderer.TILE_SIZE,
                    (model.getCurrentWorm().getY() + 1.0 / 2) * MapRenderer.TILE_SIZE,
                    dx, dy,
                    Color.BLACK,
                    40));
        }
        view.startParticleTimer();
    }

    /**
     * Méthode qui permet de tirer (grâce à un clic gauche).
     * 
     * @param panel - Correspondant au panel où on peut cliquer pour tirer
     * 
     * @since 2.0
     */
    public void bindMouseTo(JPanel panel) {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!SwingUtilities.isLeftMouseButton(e))
                    return;

                shoot(e.getX());
                panel.repaint();
            }
        });
    }

    /**
     * Setter pour savoir si le worm peut tirer
     * 
     * @param canShoot booléen qui vaut true si le worm peut tirer
     * 
     * @since 2.0
     */
    public void setCanShoot(boolean canShoot) {
        this.canShoot = canShoot;
    }
}