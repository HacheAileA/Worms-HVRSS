package model.items.guns;

import model.GameModel;
import model.players.Team;

/**
 * Cette classe implémente l'arme intitulée Sniper.
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
public class Sniper extends Guns {

    private GameModel model;

    // ========================Builders========================//

    /**
     * Constructeur pour créer un nouveau Sniper.
     * 
     * @param model - Le modèle du jeu
     * 
     * @see Guns#Guns(String, int, int, Team)
     * 
     * @since 1.0
     */
    public Sniper(GameModel model) {
        super("Sniper", 5, 50, model.getCurrentTeam());

        setSpeed(40);
        this.model = model;
        setDestructFunctionEnable(0);
    }

    // ========================Methods========================//

    /**
     * Méthode pour avoir une chance aléatoire de toucher.
     * 
     * @see Math#random()
     * 
     * @return true, si le tir a touché, false sinon
     * 
     * @since 1.0
     */
    @Override
    protected boolean randomHitConsole() {
        return Math.random() < 0.4; // 40% de chance de toucher
    }

    /**
     * Méthode pour copier l'arme Sniper.
     */
    @Override
    public Guns copy() {
        Sniper b = new Sniper(model);
        b.setAmmo(this.ammo);
        return b;
    }

}