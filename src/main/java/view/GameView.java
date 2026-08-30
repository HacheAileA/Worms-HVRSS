package view;

import java.util.ArrayList;

import model.GameModel;
import model.items.Arsenal;
import model.items.Inventory;
import model.players.*;
import view.console.ConsoleView;
import model.Map;

/**
 * Interface GameView qui gère les différentes méthodes de l'affichage.
 * 
 * @author ARNAUD Hugo
 * 
 * @see ArrayList#getClass()
 * @see Arsenal#getClass()
 * @see ConsoleView#getClass()
 * @see GameModel#getClass()
 * @see Inventory#getClass()
 * @see Map#getClass()
 * @see Team#getClass()
 * @see Worm#getClass()
 * 
 * @since 1.1
 * 
 * @version 2.0
 */
public interface GameView {

    /**
     * Méthode abstraite de définition du modèle à utiliser.
     * 
     * @param model - Un GameModel pour définir le modèle
     * 
     * @see GameModel#getClass()
     * 
     * @since 1.1
     */
    public void setModel(GameModel model);

    /**
     * Méthode abstraite pour définir le devMode.
     * 
     * @param devMode - Un boolean pour définir le devMode
     * 
     * @since 1.1
     */
    public void setDevMode(boolean devMode);

    /**
     * Méthode abstraite pour afficher la map du jeu.
     * 
     * @since 1.1
     */
    public void showMap();

    /**
     * Méthode pour retirer le bug de bloc invisible en version graphique
     * @param worm Le worm qui doit être retiré
     * @param map La map sur laquelle il est affiché
     */
    void onWormPlaced (Worm worm, Map map);
}
