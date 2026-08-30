package model.items;

/**
 * Interface Item qui permet de représenter les différents items du jeu.
 * 
 * @author ARNAUD Hugo
 * 
 * @since 1.1
 * 
 * @version 1.1
 */
public interface Item {
    

    //========================Accessors========================//
    
    /**
     * Méthode pour retourner le nom de l'item.
     * 
     * @return Une chaîne de caractère représentant le nom de l'item
     * 
     * @since 1.1
     */
    public String getName();

    /**
     * Méthode permettant de savoir si un item peut encore être utilisé.
     * 
     * @return true, si l'item peut être utilisé, false sinon
     * 
     * @since 1.1
     */
    public boolean hasAmmo();

    /**
     * Méthode pour obtenir le nombre de munitions restantes.
     * 
     * @return Le nombre de munitions restantes
     * 
     * @since 2.1
     */
    public int getAmmo();
}
