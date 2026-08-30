package view.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import model.items.crates.Crate;
import model.items.crates.CrateManager;

/**
 * Classe CrateRenderer responsable du rendu des caisses sur le panneau de jeu.
 * 
 * @author MESNILDREY Valentin
 * 
 * @since 2.1
 * 
 * @version 2.1
 */
public class CrateRenderer {
    
    // ==================== CONSTANTS ====================
    
    /** Couleur de la caisse */
    private static final Color CRATE_COLOR = new Color(139, 69, 19);
    
    /** Couleur de la mise en évidence de la caisse */
    private static final Color CRATE_HIGHLIGHT = new Color(205, 133, 63);
    
    // ==================== PUBLIC METHODS ====================
    
    /**
     * Méthode pour rendre les caisses sur le panneau de jeu.
     * 
     * @param g - Le contexte graphique
     * @param crateManager - Le gestionnaire de caisses
     * @param cellSize - La taille d'une cellule de la carte
     * 
     * @since 2.1
     */
    public void render(Graphics2D g, CrateManager crateManager, int cellSize) {
        for (Crate crate : crateManager.getActiveCrates()) {
            renderCrate(g, crate, cellSize);
        }
    }
    
    // ==================== PRIVATE METHODS ====================
    
    /**
     * Méthode pour rend une caisse individuelle
     * 
     * @param g - Le contexte graphique
     * @param crate - La caisse à rendre
     * @param cellSize - La taille d'une cellule de la carte
     * 
     * @since 2.1
     */
    private void renderCrate(Graphics2D g, Crate crate, int cellSize) {
        int crateSize = cellSize;
        int halfCrateSize = crateSize / 2;
        int screenX = (int) (crate.getX() * cellSize);
        int screenY = (int)(crate.getY() * cellSize); 
        int topLeftX = screenX - halfCrateSize;
        int topLeftY = screenY - halfCrateSize;
        
        drawCrateBody(g, topLeftX, topLeftY, crateSize);
        drawCrateDetails(g, screenX, screenY, topLeftX, topLeftY, crateSize, halfCrateSize);
    }
    
    /**
     * Méthode qui dessine le corps de la caisse
     * 
     * @param g - Le contexte graphique
     * @param topLeftX - Position X du coin supérieur gauche
     * @param topLeftY - Position Y du coin supérieur gauche
     * @param crateSize - Taille de la crate
     * 
     * @since 2.1
     */
    private void drawCrateBody(Graphics2D g, int topLeftX, int topLeftY, int crateSize) {
        g.setColor(CRATE_COLOR);
        g.fillRect(topLeftX, topLeftY, crateSize, crateSize);
    }
    
    /**
     * Méthode qui dessine les détails de la caisse (bordure et croix)
     * 
     * @param g - Le contexte graphique
     * @param screenX - Position X du centre de la caisse
     * @param screenY - Position Y du centre de la caisse
     * @param topLeftX - Position X du coin supérieur gauche
     * @param topLeftY - Position Y du coin supérieur gauche
     * @param crateSize - Taille de la crate
     * @param halfCrateSize - moitié de la taille de la crate
     * 
     * @since 2.1
     */
    private void drawCrateDetails(Graphics2D g, int screenX, int screenY, int topLeftX, int topLeftY, int crateSize, int halfCrateSize) {
        g.setColor(CRATE_HIGHLIGHT);
        
        g.drawRect(topLeftX, topLeftY, crateSize, crateSize);
        
        int bottomRightX = screenX + halfCrateSize;
        int bottomRightY = screenY + halfCrateSize;
        
        g.drawLine(topLeftX, screenY, bottomRightX, screenY);
        g.drawLine(screenX, topLeftY, screenX, bottomRightY);
    }
}