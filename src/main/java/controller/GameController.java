package controller;

import model.items.Inventory;

/**
 * Interface qui sert à définir les méthodes à utiliser dans les controlleurs.
 * 
 * @author ARNAUD Hugo
 * 
 * @see Inventory#getClass()
 * 
 * @since 1.1
 * 
 * @version 2.0
 */
public interface GameController {

  /**
   * Méthode pour savoir si le jeu est en mode développeur ou non.
   * 
   * @param devMode - Un boolean pour mettre le devMode à true, ou false
   * 
   * @since 1.1
   */
  public void setDevMode(boolean devMode);
}
