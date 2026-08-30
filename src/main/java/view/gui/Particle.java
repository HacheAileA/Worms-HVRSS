package view.gui;

import java.awt.Color;

/**
 * Classe représentant une particule pour les effets visuels.
 * 
 * @author MESNILDREY Valentin
 * 
 * @see Color#getClass()
 * 
 * @since 2.1
 * 
 * @version 2.1
 */
public class Particle {
    
    // ==================== CONSTANTS ====================
    
    private static final double GRAVITY = 0.05;
    
    // ==================== FIELDS ====================
    
    /** Position initiale en x et y */
    public double x, y;
    
    /** Vitesse en x et y de la particule */
    public double vx, vy;
    
    /** Couleur de la particule */
    public Color color;
    
    /** Durée de vie de la particule en frames */
    public int lifetime;
    
    // ==================== CONSTRUCTOR ====================
    
    /**
     * Constructeur de la particule avec les paramètres spécifiés.
     * 
     * @param x - Position initiale en x
     * @param y - Position initiale en y
     * @param vx - Vélocité en x
     * @param vy - Vélocité en y
     * @param color - Couleur de la particule
     * @param lifetime - Durée de vie de la particule en frames
     * 
     * @since 2.1
     */
    public Particle(double x, double y, double vx, double vy, Color color, int lifetime) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.color = color;
        this.lifetime = lifetime;
    }
    
    // ==================== PUBLIC METHODS ====================
    
    /**
     * Méthode de mise à jour la position et la vélocité de la particule en fonction du temps.
     * 
     * @since 2.1
     */
    public void update() {
        x += vx;
        y += vy;
        vy += GRAVITY; // Applique la gravité
        lifetime--;
    }
    
    /**
     * Méthode vérifiant si la particule est toujours vivante (sa durée de vie est supérieure à zéro).
     * 
     * @return true, si la particule est vivante, false sinon
     * 
     * @since 2.1
     */
    public boolean isAlive() {
        return lifetime > 0;
    }
}