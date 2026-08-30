package model.physics;

import java.util.Random;

/**
 * Classe qui représente le vent, dans le jeu.
 * 
 * @author MESNILDREY Valentin
 * 
 * @since 2.1
 * 
 * @version 2.1
 */
public class Wind {
    
    // ==================== CONSTANTS ====================
    
    /** Direction du vent vers la droite */
    private static final int DIRECTION_RIGHT = 1;
    /** Direction du vent vers la gauche */
    private static final int DIRECTION_LEFT = -1;
    /** Force minimale du vent */
    private static final double MIN_STRENGTH = 5.0;
    /** Plage de force du vent (ajoutée au minimum) */
    private static final double STRENGTH_RANGE = 15.0;
    
    // ==================== FIELDS ====================
    
    /** Indique si le vent est activé */
    public boolean enabled;
    /** Force du vent */
    private double strength;
    /** Direction du vent (1 pour droite, -1 pour gauche) */
    private int direction;
    /** Générateur de nombres aléatoires */
    private final Random random;
    
    // ==================== CONSTRUCTOR ====================
    
    /**
     * Constructeur de Wind
     * 
     * @since 2.1
     */
    public Wind() {
        this.random = new Random();
        this.enabled = false;
        this.strength = 0.0;
        this.direction = DIRECTION_RIGHT;
    }
    
    // ==================== PUBLIC METHODS ====================
    
    /**
     * Méthode de génération des valeurs aléatoires pour la direction et la force du vent.
     * 
     * @since 2.1
     */
    public void generateRandom() {
        this.direction = random.nextBoolean() ? DIRECTION_RIGHT : DIRECTION_LEFT;
        this.strength = MIN_STRENGTH + random.nextDouble() * STRENGTH_RANGE;
    }
    
    /**
     * Getter pour la force du vent.
     * 
     * @return La force du vent
     * 
     * @since 2.1
     */
    public double getStrength() {
        return strength;
    }
    
    /**
     * Getter pour la direction du vent.
     * 
     * @return La direction du vent (1 pour droite, -1 pour gauche)
     * 
     * @since 2.1
     */
    public int getDirection() {
        return direction;
    }
    
    /**
     * Méthode de calcul de la force effective du vent (en tenant compte de son activation).
     * 
     * @return La force effective du vent (0 si désactivé)
     * 
     * @since 2.1
     */
    public double getForce() {
        return enabled ? strength * direction : 0.0;
    }
}