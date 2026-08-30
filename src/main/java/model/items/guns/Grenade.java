package model.items.guns;

import model.GameModel;
import model.players.Team;

/**
 * Cette classe implémente l'arme intitulée Grenade.
 * 
 * @author SAMBA Seth-Ederik
 * 
 * @see Math#getClass()
 * @see Guns#getClass()
 * 
 * @since 2.1
 * 
 * @version 2.1
 */
public class Grenade extends Guns {
    
    private GameModel model;

    // ========================Builders========================//

    /**
     * Constructeur pour créer une nouvelle Grenade.
     * 
     * @param model - Le modèle de jeu
     * 
     * @see Guns#Guns(String, int, int, Team)
     * 
     * @since 1.0
     */
    public Grenade(GameModel model) {
        super("Grenade", 2, 35, model.getCurrentTeam());
        this.model = model;
        setSpeed(20);
        setDestructFunctionEnable(3);
    }

    // ========================Methods========================//

    @Override
    public Guns copy() {
        Grenade g = new Grenade(model);
        g.setAmmo(this.ammo);
        return g;
    }
}
