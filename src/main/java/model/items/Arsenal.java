package model.items;

import java.util.ArrayList;

import model.GameModel;
import model.items.guns.*;

/**
 * Cette classe implémente l'arsenal disponible du jeu.
 * 
 * @author SAMBA Seth-Ederik
 * 
 * @see ArrayList#getClass()
 * 
 * @since 0.0
 * 
 * @version 2.1
 */
public class Arsenal {


    private ArrayList<Guns> availableGuns = new ArrayList<Guns>();
    
    
    //========================Builders========================//
    
    /**
     * Constructeur de la classe Arsenal.
     * 
     * @param model - Le modèle du jeu
     *
     * @see ArrayList#isEmpty()
     *
     * @since 2.1
     */
    protected Arsenal(GameModel model){
        availableGuns.add(new Bazooka(model));
        availableGuns.add(new ShotGun(model));
        availableGuns.add(new Sniper(model));
    }

    // ========================Accessors========================//

    /**
     * Renvoie la liste des différentes armes disponibles dans le jeu.
     * 
     * @return Renvoie la liste des différentes armes disponibles sur le jeu
     * 
     * @since 1.0
     */
    public ArrayList<Guns> getAvailableGuns() {
        return availableGuns;
    }

    //========================Methods========================//

    /**
     * Supprime les armes sans munitions de l'arsenal.
     * 
     * @see ArrayList#get(int)
     * @see ArrayList#remove(int)
     * @see ArrayList#size()
     * @see Guns#getAmmo()
     * 
     * @since 1.0
     */
    protected void removeGunNoAmmo() {
        for (int i = 0; i < availableGuns.size(); i++) {
            if (availableGuns.get(i).getAmmo() <= 0) {
                availableGuns.remove(i);
                i--;
            }
        }
    }

    /**
     * Vide l'arsenal.
     * 
     * @see ArrayList#clear()
     * 
     * @since 1.1
     */
    protected void clear() {
        availableGuns.clear();
    }

    /**
     * Ajoute une arme à l'arsenal, mais, la recharge si elle est déjà présente.
     * 
     * @param gun - L'arme à ajouter
     * 
     * @see Guns#getName()
     * @see ArrayList#add(Object)
     * @see Arsenal#reloadGun(Guns)
     * 
     * @since 1.1
     */
    protected void addGun(Guns gun) {
        if(gun == null){
            return;
        }
        for (Guns g : availableGuns) {
            if (g.getName().equals(gun.getName())) {
                reloadGun(g);;
                return;
            }
        }
        availableGuns.add(gun);
    }

    /**
     * Recharge une arme de l'arsenal.
     * 
     * @param gun - L'arme à recharger
     * 
     * @see Guns#setAmmo(int)
     * @see Guns#getMaxAmmo()
     * 
     * @since 1.1
     */
    protected void reloadGun(Guns gun){
        gun.setAmmo(gun.getMaxAmmo());
    }

    /**
     * Recharge toutes les armes de l'arsenal.
     * 
     * @see Guns#setAmmo(int)
     * @see Guns#getMaxAmmo()
     * 
     * @since 1.1
     */
    protected void reloadAllGuns() {
        for (Guns g : availableGuns) {
            g.setAmmo(g.getMaxAmmo());
        }
    }
}