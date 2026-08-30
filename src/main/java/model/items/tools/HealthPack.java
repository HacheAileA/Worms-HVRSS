package model.items.tools;

import model.GameModel;
import model.players.Team;
import model.players.Worm;
//import app.Main;

/**
 * Cette classe représente un HealthPack, un outil permettant de restaurer des points de vie.
 * 
 * @author SAMBA Seth-Ederik
 * 
 * @see Math#getClass()
 * @see Worm#getClass()
 * @see Tools#getClass()
 * 
 * @since 1.1
 * 
 * @version 1.1
 */
public class HealthPack extends Tools {

    // ========================Attributes========================//
    /**
     * La quantité de points de vie restaurée par le HealthPack.
     */
    private final int healAmount;

    // ========================Builders========================//

    /**
     * Constructeur pour créer un nouveau HealthPack.
     * 
     * @param healAmount - La quantité de points de vie restaurée par le HealthPack
     * @param model - Le modèle du jeu
     * 
     * @since 1.1
     */
    public HealthPack(int healAmount, GameModel model) {
        super("Health Pack", 1, model.getCurrentTeam());
        this.healAmount = Math.max((int) Math.round(app.Launcher.CONFIG.getIntParameter("HP")*0.15), healAmount); // Assure que healAmount est de au moins 15% des HP max
        this.model = model;
    }

    /**
     * Constructeur par défaut pour créer un HealthPack avec une quantité prédéfinie de points de vie à restaurer.
     * 
     * @param team - L'équipe à laquelle appartient le HealthPack
     * 
     * @see Tools#Tools(String, int, Team)
     * 
     * @since 1.1
     */
    public HealthPack(Team team) {
        super("Health Pack", 1, team);
        this.healAmount = 50;
    }

    // ========================Accessors========================//

    /**
     * Getter pour obtenir la quantité de points de vie restaurée par le HealthPack.
     * 
     * @return La quantité de points de vie restaurée
     * 
     * @since 1.1
     */
    public int getHealAmount() {
        return this.healAmount;
    }

    // ========================Methods========================//

    /**
     * Méthode pour utiliser le HealthPack sur un Worm.
     * 
     * @param worm - Le Worm qui utilisera le HealthPack
     * 
     * @see Tools#hasAmmo()
     * @see Math#min(int, int)
     * 
     * @since 1.1
     */
    public void useTool(Worm worm) {
        if (worm == null) {
            System.out.println("On ne peut pas Heal de Worm null");
            return;
        }
        if (this.hasAmmo()) {
            worm.setHp(Math.min(worm.getHp() + this.healAmount, /* CONFIG.getIntParameter("HP") */ 100));
            this.ammo--;
        } else {
            System.out.println("No more Health Pack left !");
        }
    }
}
