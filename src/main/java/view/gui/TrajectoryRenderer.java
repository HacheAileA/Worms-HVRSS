package view.gui;

import java.awt.*;
import java.util.ArrayList;
import model.Map;
import model.items.guns.Guns;
import model.physics.TrajectoryPredictor;
import model.players.Worm;

/**
 * Classe permettant d'afficher une prévisualisation de comment partira le projectile du worms
 * 
 * @author MESNILDREY Valentin
 * 
 * @see Worm#getClass()
 * @see Map#getClass()
 * @see MapRenderer#getClass()
 * @see Guns#getClass()
 * @see Graphics2D#getClass()
 * 
 * @since 2.0
 * 
 * @version 2.0
 */
public class TrajectoryRenderer {
    
    // ==================== CONSTANTS ====================
    
    private static final int TRAJECTORY_POINTS = 20;
    private static final double TIME_STEP = 0.2;
    private static final float STROKE_WIDTH = 2f;
    
    // ==================== PUBLIC METHODS ====================
    
    /**
     * Méthode permettant d'afficher la trajectoire de visée du worms
     * 
     * @param g - Graphics2D pour dessiner la trajectoire
     * @param worm - Le worm qui vise
     * @param map - La map pour afficher la visée
     * 
     * @since 2.0
     */
    public void render(Graphics2D g, Worm worm, Map map) {
        if (!canRenderTrajectory(worm)) {
            return;
        }
        
        Guns gun = (Guns) worm.getSelectedItem();
        ArrayList<double[]> points = predictTrajectory(worm, gun);
        
        drawTrajectoryPath(g, points);
    }
    
    // ==================== PRIVATE METHODS ====================
    
    /**
     * Méthode qui vérifie si la trajectoire peut être affichée
     * 
     * @param worm - Le worm à vérifier
     * 
     * @return true, si la trajectoire peut être affichée
     */
    private boolean canRenderTrajectory(Worm worm) {
        return worm != null && worm.getSelectedItem() instanceof Guns;
    }
    
    /**
     * Méthode de prédiction de la trajectoire du projectile
     * 
     * @param worm - Le worm qui tire
     * @param gun - L'arme utilisée
     * @return Liste des points de la trajectoire
     * 
     * @since 2.0
     */
    private ArrayList<double[]> predictTrajectory(Worm worm, Guns gun) {
        int tileSize = MapRenderer.getTileSize();
        double startX = worm.getX() * tileSize + tileSize / 2.0;
        double startY = worm.getY() * tileSize + tileSize / 2.0;
        
        return TrajectoryPredictor.predict(
            startX,
            startY,
            TRAJECTORY_POINTS,
            worm.getAimAngle(),
            gun.getProjectileSpeed(),
            gun.getGravity(),
            TIME_STEP
        );
    }
    
    /**
     * Méthode en charge de dessiner le chemin de la trajectoire
     * 
     * @param g - Graphics2D pour dessiner
     * @param points - Liste des points de la trajectoire
     * 
     * @since 2.0
     */
    private void drawTrajectoryPath(Graphics2D g, ArrayList<double[]> points) {
        if (points.size() < 2) {
            return;
        }
        
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(STROKE_WIDTH));
        
        for (int i = 1; i < points.size(); i++) {
            drawTrajectorySegment(g, points.get(i - 1), points.get(i));
        }
    }
    
    /**
     * Méthode qui dessine un segment de la trajectoire
     * 
     * @param g - Graphics2D pour dessiner
     * @param point1 - Point de départ
     * @param point2 - Point d'arrivée
     * 
     * @since 2.0
     */
    private void drawTrajectorySegment(Graphics2D g, double[] point1, double[] point2) {
        int x1 = (int) point1[0];
        int y1 = (int) point1[1];
        int x2 = (int) point2[0];
        int y2 = (int) point2[1];
        
        g.drawLine(x1, y1, x2, y2);
    }
}