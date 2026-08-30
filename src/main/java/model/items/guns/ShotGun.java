package model.items.guns;

import java.util.ArrayList;
import java.util.List;

import model.GameModel;
import model.physics.Projectile;
import model.players.Team;
import model.players.Worm;

/**
 * Cette classe implémente l'arme intitulée ShotGun.
 * 
 * Le ShotGun tire 3 projectiles à la fois avec un léger écart d'angle (spread). Chaque projectile disparaît après 5 unités de distance.
 * 
 * @author SAMBA Seth-Ederik
 * 
 * @see Math#getClass()
 * @see Guns#getClass()
 * 
 * @since 0.0
 * 
 * @version 2.0
 */
public class ShotGun extends Guns {
    private GameModel model;
    
    /** Nombre de projectiles tirés simultanément */
    private static final int PROJECTILE_COUNT = 3;
    
    /** Écart d'angle entre les projectiles (en radians) */
    private static final double SPREAD_ANGLE = 0.15; // environ 8.6 degrés
    
    /** Distance maximale que peut parcourir un projectile du shotgun (en unités/cases) */
    private static final double MAX_DISTANCE = 5.0;

    // ========================Builders========================//
    /**
     * Constructeur pour créer un nouveau ShotGun.
     * 
     * @param model - Le modèle du jeu
     * 
     * @see Guns#Guns(String, int, int, Team)
     * 
     * @since 1.0
     */
    public ShotGun(GameModel model) {
        super("ShotGun", 10, 30, model.getCurrentTeam());
        this.model = model;
        setSpeed(30);
        setGravity(0);
        setDestructFunctionEnable(0);
    }

    // ========================Methods========================//
    /**
     * Méthode pour avoir une chance aléatoire de toucher.
     * 
     * @return true, si le tir a touché, false sinon
     * 
     * @see Math#random()
     * 
     * @since 1.0
     */
    @Override
    protected boolean randomHitConsole() {
        return Math.random() < 0.75; // 75% de chance de toucher
    }

    /**
     * Crée un seul projectile (méthode héritée, non utilisée pour le shotgun).
     * Pour le shotgun, utilisez createProjectiles() à la place.
     * 
     * @param shooter - Le worm qui tire le projectile
     * 
     * @return null, car le shotgun utilise createProjectiles()
     * 
     * @since 2.0
     */
    @Override
    public Projectile createProjectile(Worm shooter) {
        // Le shotgun ne crée pas un seul projectile, il en crée plusieurs
        // Cette méthode ne devrait pas être appelée directement
        if (!hasAmmo())
            return null;
        
        setAmmo(getAmmo() - 1);
        
        return new Projectile(
            shooter.getX(), 
            shooter.getY(), 
            shooter.getAimAngle(), 
            projectileSpeed, 
            gravity,
            damagesPerBullets, 
            explosionRadius, 
            shooter,
            MAX_DISTANCE
        );
    }

    /**
     * Crée plusieurs projectiles tirés simultanément par le worm.
     * 
     * @param shooter - Le worm qui tire les projectiles
     * 
     * @return Une liste de 3 projectiles avec des angles légèrement différents
     * 
     * @since 2.1
     */
    @Override
    public List<Projectile> createProjectiles(Worm shooter) {
        List<Projectile> projectiles = new ArrayList<>();
        
        if (!hasAmmo())
            return projectiles;
        
        setAmmo(getAmmo() - 1);
        
        double baseAngle = shooter.getAimAngle();
        
        // Créer 3 projectiles avec des angles différents
        for (int i = 0; i < PROJECTILE_COUNT; i++) {
            // Calcul de l'angle pour chaque projectile
            // Le projectile central garde l'angle original
            // Les autres sont décalés de ±SPREAD_ANGLE
            double angleOffset = (i - 1) * SPREAD_ANGLE; // -SPREAD_ANGLE, 0, +SPREAD_ANGLE
            double projectileAngle = baseAngle + angleOffset;
            
            Projectile p = new Projectile(
                shooter.getX(), 
                shooter.getY(), 
                projectileAngle, 
                projectileSpeed, 
                gravity,
                damagesPerBullets, 
                explosionRadius, 
                shooter,
                MAX_DISTANCE
            );
            
            projectiles.add(p);
        }
        
        return projectiles;
    }

    /**
     * Méthode pour copier le ShotGun.
     */
    @Override
    public Guns copy() {
        ShotGun b = new ShotGun(model);
        b.setAmmo(this.ammo);
        return b;
    }
}