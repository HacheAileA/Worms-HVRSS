package model.physics;

import java.util.ArrayList;

/**
 * Classe qui permet de calculer la trajectoire qu'aura le projectile (en version graphique).
 * 
 * @author MESNILDREY Valentin
 * 
 * @see ArrayList#getClass()
 * 
 * @since 2.0
 * 
 * @version 2.0
 */
public class TrajectoryPredictor {
    
    // ==================== PUBLIC METHODS ====================
    
    /**
     * Méthode permettant de "prédir" la trajectoire du projectile (sert à la prévisualisation du tir)
     * 
     * @param startX - Coordonnée X de départ du projectile
     * @param startY - Coordonnée Y de départ du projectile
     * @param maxSteps - Correspond à la distance de prévisualisation
     * @param angleRad - L'angle en radian du worm
     * @param speed - La vitesse du projectile
     * @param gravity - La gravité que subira le projectile
     * @param timeStep - Simulation du temps pour correspondre à la réelle trajectoire du projectile
     * 
     * @return Une ArrayList de points à afficher pour la prévisualisation
     * 
     * @since 2.0
     */
    public static ArrayList<double[]> predict(
            double startX, 
            double startY,
            int maxSteps,
            double angleRad,
            double speed,
            double gravity,
            double timeStep) {
        
        ArrayList<double[]> points = new ArrayList<>(maxSteps);
        
        TrajectoryState state = initializeTrajectory(startX, startY, angleRad, speed);
        
        for (int i = 0; i < maxSteps; i++) {
            updateTrajectoryState(state, gravity, timeStep);
            points.add(new double[]{state.x, state.y});
            
            if (hasReachedGround(state)) {
                break;
            }
        }
        
        return points;
    }
    
    // ==================== PRIVATE METHODS ====================
    
    /**
     * Méthode d'initialisation de l'état de la trajectoire
     * 
     * @param startX - Position X de départ
     * @param startY - Position Y de départ
     * @param angleRad - Angle en radians
     * @param speed - Vitesse initiale
     * 
     * @return L'état initial de la trajectoire
     */
    private static TrajectoryState initializeTrajectory(
            double startX, 
            double startY, 
            double angleRad, 
            double speed) {
        
        return new TrajectoryState(
            startX,
            startY,
            Math.sin(angleRad) * speed,
            Math.cos(angleRad) * speed
        );
    }
    
    /**
     * Méthode de mise à jour de l'état de la trajectoire
     * 
     * @param state - L'état de la trajectoire
     * @param gravity - La gravité appliquée
     * @param timeStep - Le pas de temps
     */
    private static void updateTrajectoryState(TrajectoryState state, double gravity, double timeStep) {
        state.x += state.vx * timeStep;
        state.y -= state.vy * timeStep;
        state.vy -= gravity * timeStep;
    }
    
    /**
     * Méthode qui vérifie si la trajectoire a atteint le sol
     * 
     * @param state - L'état de la trajectoire
     * 
     * @return true, si la trajectoire a atteint le sol
     */
    private static boolean hasReachedGround(TrajectoryState state) {
        return state.y < 0;
    }
    
    // ==================== INNER CLASS ====================
    
    /**
     * Classe interne représentant l'état d'une trajectoire
     */
    private static class TrajectoryState {
        double x;
        double y;
        double vx;
        double vy;
        
        TrajectoryState(double x, double y, double vx, double vy) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
        }
    }
}