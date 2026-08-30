package model.items;

import java.util.ArrayList;
import model.items.guns.Guns;
import model.items.tools.Tools;
import model.GameModel;
import model.players.Team;

/**
 * Cette classe implémente l'inventaire d'une équipe, contenant son arsenal (d'armes et d'outils).
 * 
 * @author SAMBA Seth-Ederik
 * 
 * @see ArrayList#getClass()
 * @see Arsenal#getClass()
 * @see ArsenalTools#getClass()
 * @see Guns#getClass()
 * @see Tools#getClass()
 * 
 * @since 1.1
 * 
 * @version 2.1
 */
public class Inventory {

    // ========================Attributes========================//

    /**
     * L'arsenal d'armes de l'inventaire.
     */
    private Arsenal guns;

    /**
     * L'arsenal d'outils de l'inventaire.
     */
    private ArsenalTools tools;

    /**
     * Le modèle du jeu.
     */
    private GameModel model;

    // ========================Builders========================//

    /**
     * Constructeur de la classe Inventory, qui initialise l'arsenal d'armes et d'outils.
     * 
     * @param model Le modèle du jeu
     * 
     * @see Arsenal#Arsenal(GameModel)
     * @see ArsenalTools#ArsenalTools(GameModel)
     * 
     * @since 1.1
     */
    public Inventory(GameModel model) {
        this.model = model;
        this.guns = new Arsenal(model);
        this.tools = new ArsenalTools(model);
    }

    // ========================Accessors========================//

    /**
     * Getter pour obtenir l'arsenal d'armes de l'inventaire.
     * 
     * @return L'arsenal d'armes
     * 
     * @since 1.1
     */
    public Arsenal getGuns() {
        return guns;
    }

    /**
     * Getter pour obtenir l'arsenal d'outils de l'inventaire.
     * 
     * @return L'arsenal d'outils
     * 
     * @since 1.1
     */
    public ArsenalTools getTools() {
        return tools;
    }

    /**
     * Getter pour obtenir la liste des armes disponibles dans l'inventaire.
     * 
     * @param filterTeam - L'équipe pour filtrer les armes
     * 
     * @see Arsenal#getAvailableGuns()
     * 
     * @return La liste des armes disponibles
     * 
     * @since 1.1
     */
    public ArrayList<Guns> getAvailableGuns(Team filterTeam) {
        ArrayList<Guns> filtered = new ArrayList<>();
        for (Guns gun : guns.getAvailableGuns()) {
            if (gun.getTeam() == filterTeam) {
                filtered.add(gun);
            }
        }
        return filtered;
    }

    /**
     * Getter pour obtenir la liste des armes disponibles dans l'inventaire.
     * 
     * @see Arsenal#getAvailableGuns()
     * 
     * @return La liste des armes disponibles
     * 
     * @since 1.1
     */
    public ArrayList<Guns> getAvailableGuns() {
        return guns.getAvailableGuns();
    }

    /**
     * Getter pour obtenir la liste des outils disponibles dans l'inventaire.
     * 
     * @see ArsenalTools#getAvailableTools()
     * 
     * @return La liste des outils disponibles
     * 
     * @since 1.1
     */
    public ArrayList<Tools> getAvailableTools() {
        return tools.getAvailableTools();
    }

    /**
     * Getter pour obtenir la liste de tous les items (armes et outils) disponibles dans l'inventaire.
     * 
     * @param team - L'équipe pour filtrer les armes
     * 
     * @return La liste de tous les items disponibles
     * 
     * @since 1.1
     */
    public ArrayList<Item> getAvailableItems(Team team) {
        ArrayList<Item> items = new ArrayList<>();
        items.addAll(getAvailableGuns(team));
        items.addAll(getAvailableTools());
        return items;
    }

    /**
     * Getter pour obtenir une arme spécifique de l'inventaire en fonction de sa classe.
     * 
     * @param gunClass - La classe de l'arme à obtenir
     * 
     * @return L'arme correspondant à la classe spécifiée, ou null si non trouvé
     * 
     * @since 1.1
     */
    public Guns getGun(Class<? extends Guns> gunClass) {
        for (Guns gun : guns.getAvailableGuns()) {
            if (gun.getClass().equals(gunClass)) {
                return gun;
            }
        }
        return null;
    }

    /**
     * Getter pour obtenir un outil spécifique de l'inventaire en fonction de sa classe.
     * 
     * @param toolClass - La classe de l'outil à obtenir
     * 
     * @return L'outil correspondant à la classe spécifiée, ou null si non trouvé
     * 
     * @since 1.1
     */
    public Tools getTool(Class<? extends Tools> toolClass) {
        for (Tools tool : tools.getAvailableTools()) {
            if (tool.getClass().equals(toolClass)) {
                return tool;
            }
        }
        return null;
    }

    // ========================Methods========================//

    /**
     * Supprime les armes sans munitions de l'inventaire.
     * 
     * @see Arsenal#removeGunNoAmmo()
     * 
     * @since 1.1
     */
    public void removeGunNoAmmo() {
        guns.removeGunNoAmmo();
    }

    /**
     * Supprime les outils sans munitions de l'inventaire.
     * 
     * @see ArsenalTools#removeToolNoAmmo()
     * 
     * @since 1.1
     */
    public void removeToolNoAmmo() {
        tools.removeToolNoAmmo();
    }

    /**
     * Charge les items (armes et outils) depuis une liste d'items sauvegardés.
     * 
     * @param savedItems - La liste des items sauvegardés
     * @param owner - L'équipe propriétaire des items
     * 
     * @see Arsenal#addGun(Guns)
     * @see Arsenal#clear()
     * @see ArsenalTools#addTool(Tools)
     * @see ArsenalTools#clear()
     * 
     * @since 1.1
     */
    public void loadItemsFromSave(ArrayList<Item> savedItems, Team owner) {
        guns.clear();
        tools.clear();

        for (Item item : savedItems) {
            if (item instanceof Guns) {
                Guns gunCopy = ((Guns) item).copy();
                gunCopy.setTeam(owner);
                guns.addGun(gunCopy);
            } else if (item instanceof Tools) {
                Tools tool = (Tools) item;
                tool.setTeam(owner);
                tools.addTool((Tools) item);
            }
        }
    }

    // ========================Display========================//

    /**
     * Ajoute une arme à l'inventaire.
     * 
     * @param gun - L'arme à ajouter
     * 
     * @see Guns#setTeam(Team)
     * @see Arsenal#addGun(Guns)
     * 
     * @since 1.1
     */
    public void addItem(Guns gun) {
        gun.setTeam(model.getCurrentTeam());
        guns.addGun(gun);
    }

    /**
     * Ajoute un outil à l'inventaire.
     * 
     * @param tool - L'outil à ajouter
     * 
     * @see ArsenalTools#addTool(Tools)
     * 
     * @since 1.1
     */
    public void addItem(Tools tool) {
        tool.setTeam(model.getCurrentTeam());
        tools.addTool(tool);
    }

    /**
     * Ajoute un item (arme ou outil) à l'inventaire.
     * 
     * @param item - L'item à ajouter
     * 
     * @see #addItem(Guns)
     * @see #addItem(Tools)
     * 
     * @since 1.1
     */
    public void addItem(Item item) {
        if (item instanceof Guns gun)
            addItem(gun);
        if (item instanceof Tools tool)
            addItem(tool);
    }

    /**
     * Recharge toutes les armes et outils de l'inventaire.
     * 
     * @see Arsenal#reloadAllGuns()
     * @see ArsenalTools#reloadTool(Tools)
     * 
     * @since 1.1
     */
    public void reloadAllItems() {
        guns.reloadAllGuns();
        tools.reloadAllTools();
    }
}
