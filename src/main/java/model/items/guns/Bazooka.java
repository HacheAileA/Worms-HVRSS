package model.items.guns;

import model.GameModel;
import model.players.Team;

/**
 * Cette classe implémente l'arme intitulée Bazooka.
 * 
 * @author SAMBA Seth-Ederik
 * 
 * @see Math#getClass()
 * @see Guns#getClass()
 * 
 * @since 0.0
 * 
 * @version 1.0
 */
public class Bazooka extends Guns {

    private GameModel model;

    // ========================Builders========================//

    /**
     * Constructeur pour créer un nouveau Bazooka.
     * 
     * @param model - Le modèle de jeu
     * 
     * @see Guns#Guns(String, int, int, Team)
     * 
     * @since 1.0
     */
    public Bazooka(GameModel model) {
        super("Bazooka", 1, 40, model.getCurrentTeam());
        this.model = model;
        setSpeed(20);
        setGravity(0);
        setDestructFunctionEnable(3);
    }

    // ========================Methods========================//

    /**
     * Méthode pour avoir une chance aléatoire de toucher.
     * 
     * @return true, si le tir a touché, false sinon
     * 
     * @see Math#random()
     * 
     * @since 1.0
     */
    @Override
    protected boolean randomHitConsole() {
        return Math.random() < 0.6; // 60% de chance de toucher
    }

    /**
     * Méthode pour copier le Bazooka.
     */
    @Override
    public Guns copy() {
        Bazooka b = new Bazooka(model);
        b.setAmmo(this.ammo);
        return b;
    }

}