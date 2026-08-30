package model.items.tools;

import model.players.Worm;
import model.GameModel;
import model.physics.Projectile;

/**
 * Cette classe représente un AirStrike, un outil permettant de lancer une attaque aérienne.
 * 
 * @author SAMBA Seth-Ederik
 * @author MESNILDREY Valentin
 * 
 * @see Math#getClass()
 * @see Worm#getClass()
 * @see Tools#getClass()
 * @see Projectile#getClass()
 * 
 * @since 2.1
 * 
 * @version 2.1
 */
public class AirStrike extends Tools {
    // ========================Builders========================//

    /**
     * Constructeur pour créer un nouvel AirStrike.
     * 
     * @param model - Le modèle du jeu
     * 
     * @since 2.1
     */
    public AirStrike(GameModel model) {
        super("Air Strike", 1, model.getCurrentTeam());
        this.model = model;
    }

    @Override
    public void useTool(Worm user) {
    }

    /**
     * Méthode pour utiliser l'AirStrike.
     * 
     * @param user - Le worm qui utilise l'AirStrike
     * @param targetX - La position X ciblée pour l'attaque aérienne
     * 
     * @return Le projectile représentant l'attaque aérienne
     * 
     * @since 2.1
     */
    public Projectile useTool(Worm user, int targetX) {
        if( ammo <= 0) {
            return null;
        }
        ammo--;
        return new Projectile(targetX, 0, Math.PI, 5, 4, 40, 4, user);
    }
}
