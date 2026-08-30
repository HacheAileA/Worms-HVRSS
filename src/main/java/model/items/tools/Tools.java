package model.items.tools;

import model.GameModel;
import model.items.Item;
import model.players.Team;
import model.players.Worm;

/**
 * Cette classe implémente les méthodes communes aux différents outils du jeu.
 * 
 * @author SAMBA Seth-Ederik
 * 
 * @since 1.1
 * 
 * @version 1.1
 */
public abstract class Tools implements Item {    

    //========================Attributes========================//
    /**
     * Le nombre de munitions.
     */
    protected int ammo;

    /**
     * Le nombre maximum de munitions.
     */
    protected int maxAmmo;

    /**
     * Le nom de l'outil.
     */
    protected final String name;

    /**
     * L'équipe associée à l'outil.
     */
    protected Team team;

    /**
     * Le modèle du jeu.
     */
    protected GameModel model;
    //========================Builders========================//

    /**
     * Constructeur de la classe Tools.
     * 
     * @param name - Nom de l'outil
     * @param ammo - Nombre de munitions
     * @param team - L'équipe à laquelle appartient l'outil
     * 
     * @since 1.1
     */
    protected Tools(String name, int ammo, Team team) {
        this.name = name;
        this.ammo = ammo;
        this.maxAmmo = ammo;
        setTeam(team);
    }


    //========================Accessors========================//

    /**
     * Setter pour définir le modèle du jeu.
     * 
     * @param model - Le modèle du jeu
     * 
     * @since 1.1
     */
    public void setModel(GameModel model) {
        this.model = model;
    }

    /**
     * Getter pour obtenir le nom de l'outil.
     * 
     * @return Le nom de l'outil
     * 
     * @since 1.1
     */
    public String getName() {
        return this.name;
    }

    /**
     * Getter pour obtenir le nombre de munitions restantes.
     * 
     * @return Le nombre de munitions restantes
     * 
     * @since 1.1
     */
    @Override
    public int getAmmo() {
        return this.ammo;
    }

    /**
     * Setter pour définir le nombre de munitions restantes.
     * 
     * @param ammo - Le nouveau nombre de munitions
     * 
     * @since 1.1
     */
    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }

    /**
     * Getter pour obtenir le nombre maximum de munitions.
     * 
     * @return Le nombre maximum de munitions
     * 
     * @since 1.1
     */
    public int getMaxAmmo() {
        return this.maxAmmo;
    }
    
    /**
     * Méthode qui permet de savoir si l'outil a des munitions.
     * 
     * @return true, si l'outil a des munitions, false sinon
     * 
     * @since 1.1
     */
    public boolean hasAmmo() {
        return this.ammo > 0;
    }

    /**
     * Setter pour définir l'équipe associée.
     * 
     * @param team - L'équipe associée à l'outil
     * 
     * @since 1.1
     */
    public void setTeam(Team team) {
        this.team = team;
    }

    /**
     * Getter pour obtenir l'équipe associée à l'outil.
     * 
     * @return L'équipe associée
     * 
     * @since 1.1
     */
    public Team getTeam() {
        return team;
    }

    /**
     * Méthode abstraite pour permettre à un Worm d'utiliser l'outil.
     * 
     * @param worm - Le Worm qui utilisera l'outil
     * 
     * @since 1.1
     */
    public abstract void useTool(Worm worm);
}