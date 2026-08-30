package model.items;

import java.util.ArrayList;

import model.items.tools.HealthPack;
import model.items.tools.Tools;
import model.players.Team;
import model.GameModel;

/**
 * Interface Item qui permet de représenter les différents items du jeu.
 * 
 * @author SAMBA Seth-Ederik
 * 
 * @see Tools#getClass()
 * @see HealthPack#getClass()
 * 
 * @since 1.1
 * 
 * @version 2.1
 */
public class ArsenalTools {
    
    /** */private ArrayList<Tools> availableTools = new ArrayList<Tools>();
    /** */protected Team team;

    // ========================Builders========================//
    /**
     * Constructeur de la classe ArsenalTools.
     * 
     * @param model - Le modèle du jeu
     * 
     * @see ArrayList#isEmpty()
     * 
     * @since 1.1
     */
    protected ArsenalTools(GameModel model){ 
        availableTools.add(new HealthPack(40, model));
    }

    // ========================Accessors========================//

    /**
     * Méthode qui nous indique les différents outils disponibles sur le jeu.
     * 
     * @return Renvoie la liste des différents outils disponibles sur le jeu
     * 
     * @since 1.1
     */
    protected ArrayList<Tools> getAvailableTools() {
        return availableTools;
    }
    // ========================Methods========================//

    /**
     * Méthode qui supprime les outils sans munitions de l'arsenal.
     * 
     * @see Tools#hasAmmo()
     * @see ArrayList#remove(int)
     * @see ArrayList#get(int)
     * @see ArrayList#size()
     * 
     * @since 1.1
     */
    protected void removeToolNoAmmo() {
        for (int i = 0; i < availableTools.size(); i++) {
            if (!availableTools.get(i).hasAmmo()) {
                availableTools.remove(i);
                i--;
            }
        }
    }
    
    /**
     * Ajoute un outil à l'arsenal, mais, le recharge s'il est déjà présent.
     * 
     * @param tool - L'outil à ajouter
     * 
     * @see Tools#getName()
     * @see ArsenalTools#reloadTool(Tools)
     * @see ArrayList#add(Object)
     * @see HealthPack#getHealAmount()
     * @see Tools#setAmmo(int)
     * 
     * @since 1.1
     */
    protected void addTool(Tools tool) {

        if(tool == null){
            return;
        }

        // Cas particulier des HealthPack
        if(tool.getClass() == HealthPack.class){
            for (Tools t : availableTools) {
                if ((t.getName().equals(tool.getName())) && (((HealthPack) t).getHealAmount() == ((HealthPack) tool).getHealAmount())) {
                    t.setAmmo(t.getAmmo() + 1);
                    return;
                }
            }
            availableTools.add(tool);
            return;
        }

        // Cas général pour les autres outils
        for (Tools t : availableTools) {
            if (t.getName().equals(tool.getName())) {
                reloadTool(t);
                return;
            }
        }
        availableTools.add(tool);
    }

    /**
     * Recharge un outil de l'arsenal.
     * 
     * @param tool - L'outil à recharger
     * 
     * @see Tools#setAmmo(int)
     * @see Tools#getMaxAmmo()
     * 
     * @since 1.1
     */
    protected void reloadTool(Tools tool) {
        if(tool == null){
            return;
        }
       tool.setAmmo(tool.getMaxAmmo());
    }
 
    /**
     * Recharge tous les outils de l'arsenal.
     * 
     * @see Tools#setAmmo(int)
     * @see Tools#getMaxAmmo()
     * 
     * @since 1.1
     */
    protected void reloadAllTools() {
        for (Tools t : availableTools) {
            t.setAmmo(t.getMaxAmmo());
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
        availableTools.clear();
    }
}
