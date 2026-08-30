package model.physics;

import model.Map;
import model.players.Worm;

/**
 * Classe qui représente un projectile dans le jeu.
 * 
 * @author MESNILDREY Valentin
 * 
 * @see Worm#getClass()
 * @see Map#getClass()
 * @see Wind#getClass()
 * 
 * @since 2.0
 * @version 2.1
 */
public class Projectile {

    // ==================== CONSTANTS ====================

    /** Multiplicateur de gravité pour la simulation */
    private static final double GRAVITY_MULTIPLIER = 15.0;
    /** Valeur indiquant qu'il n'y a pas de limite de distance */
    private static final double NO_DISTANCE_LIMIT = -1;

    // ==================== FIELDS ====================

    /** Position actuelle X du projectile */
    private double x;
    /** Position actuelle Y du projectile */
    private double y;
    /** Vélocité horizontale */
    private double vx;
    /** Vélocité verticale */
    private double vy;
    /** Gravité appliquée au projectile */
    private final double gravity;
    /** Dégâts infligés par le projectile */
    private final int damage;
    /** Rayon d'explosion du projectile */
    private final int explosionRadius;
    /** Le worm qui a tiré ce projectile */
    private final Worm shooter;
    /** Indique si le projectile est actif */
    private boolean active;
    /** Indique si le projectile a quitté la case du tireur */
    private boolean hasLeftShooterTile;
    /** Référence au vent (peut être null) */
    private Wind wind;
    /** Position X de départ du projectile */
    private final double startX;
    /** Position Y de départ du projectile */
    private final double startY;
    /** Distance maximale en unités (-1 pour illimité) */
    private final double maxDistance;
    /** Indique si le projectile a une limite de distance */
    private final boolean hasDistanceLimit;

    // ==================== CONSTRUCTORS ====================

    /**
     * Constructeur de Projectile sans limite de distance
     * 
     * @param startX - la position X de départ
     * @param startY - la position Y de départ
     * @param angleRad - l'angle de tir en radians
     * @param speed - la vitesse initiale
     * @param gravity - la gravité appliquée au projectile
     * @param damage - les dégâts infligés par le projectile
     * @param explosionRadius - le rayon d'explosion du projectile
     * @param shooter - le Worm qui a tiré le projectile
     * 
     * @since 2.0
     */
    public Projectile(
            double startX,
            double startY,
            double angleRad,
            double speed,
            double gravity,
            int damage,
            int explosionRadius,
            Worm shooter) {
        this(startX, startY, angleRad, speed, gravity, damage, explosionRadius, shooter, NO_DISTANCE_LIMIT);
    }

    /**
     * Constructeur avec limite de distance optionnelle
     * 
     * @param startX - Position X de départ
     * @param startY - Position Y de départ
     * @param angleRad - Angle de tir en radians
     * @param speed - Vitesse du projectile
     * @param gravity - Gravité appliquée
     * @param damage - Dégâts infligés
     * @param explosionRadius - Rayon d'explosion
     * @param shooter - Le worm qui a tiré
     * @param maxDistance - Distance maximale en unités (-1 pour illimité)
     * 
     * @since 2.1
     */
    public Projectile(
            double startX,
            double startY,
            double angleRad,
            double speed,
            double gravity,
            int damage,
            int explosionRadius,
            Worm shooter,
            double maxDistance) {

        this.x = startX;
        this.y = startY;
        this.startX = startX;
        this.startY = startY;
        this.vx = Math.sin(angleRad) * speed;
        this.vy = Math.cos(angleRad) * speed;
        this.gravity = gravity;
        this.damage = damage;
        this.explosionRadius = explosionRadius;
        this.shooter = shooter;
        this.maxDistance = maxDistance;
        this.hasDistanceLimit = maxDistance > 0;
        this.active = true;
        this.hasLeftShooterTile = false;
    }

    // ==================== PUBLIC METHODS ====================

    /**
     * Setter pour le vent
     * 
     * @param wind - le Wind à définir
     * 
     * @since 2.1
     */
    public void setWind(Wind wind) {
        this.wind = wind;
    }

    /**
     * Méthode qui met à jour la position du projectile en fonction du temps écoulé et de la position du tireur.
     * 
     * @param time - le temps écoulé depuis la dernière mise à jour
     * @param shooterTileX - la coordonnée X de la tuile du tireur
     * @param shooterTileY - la coordonnée Y de la tuile du tireur
     * 
     * @since 2.0
     */
    public void update(double time, double shooterTileX, double shooterTileY) {
        if (!active) {
            return;
        }

        applyPhysics(time);
        updateShooterTileStatus(shooterTileX, shooterTileY);

        if (hasLeftShooterTile) {
            checkDistanceLimit();
        }
    }

    /**
     * Indicateur de l'activité du projectile.
     * 
     * @return true, si le projectile est actif, false sinon
     * 
     * @since 2.0
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Méthode qui retourne la distance parcourue depuis le point de départ
     * 
     * @return Distance parcourue en unités
     * 
     * @since 2.1
     */
    public double getDistanceTraveled() {
        return calculateDistance(x - startX, y - startY);
    }

    /**
     * Getter pour les dégâts du projectile.
     * 
     * @return les dégâts du projectile
     * 
     * @since 2.0
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Getter pour le rayon d'explosion du projectile.
     * 
     * @return le rayon d'explosion du projectile
     * 
     * @since 2.0
     */
    public int getExplosionRadius() {
        return explosionRadius;
    }

    /**
     * Getter pour le tireur du projectile.
     * 
     * @return le Worm qui a tiré le projectile
     * 
     * @since 2.0
     */
    public Worm getShooter() {
        return shooter;
    }

    /**
     * Getter pour la coordonnée X du projectile sur la carte.
     * 
     * @return la coordonnée X du projectile
     * 
     * @since 2.0
     */
    public double getMapX() {
        return x;
    }

    /**
     * Getter pour la coordonnée Y du projectile sur la carte.
     * 
     * @return la coordonnée Y du projectile
     * 
     * @since 2.0
     */
    public double getMapY() {
        return y;
    }

    /**
     * Getter pour la coordonnée X du projectile.
     * 
     * @return la coordonnée X du projectile
     * 
     * @since 2.0
     */
    public double getX() {
        return x;
    }

    /**
     * Getter pour la coordonnée Y du projectile.
     * 
     * @return la coordonnée Y du projectile
     * 
     * @since 2.0
     */
    public double getY() {
        return y;
    }

    /**
     * Méthode qui indique si le projectile a quitté la tuile du tireur.
     * 
     * @return true si le projectile a quitté la tuile du tireur, false sinon
     * 
     * @since 2.0
     */
    public boolean hasLeftShooterTile() {
        return hasLeftShooterTile;
    }

    /**
     * Détermine si le projectile doit être détruit en fonction de sa position sur la carte.
     * 
     * @param map - La carte du jeu
     * 
     * @return true si le projectile doit être détruit, false sinon
     * 
     * @since 2.0
     */
    public boolean shouldBeDestroyed(Map map) {
        double mapX = getMapX();
        double mapY = getMapY();

        if (isOutOfBounds(mapX, mapY, map)) {
            return true;
        }

        return map.isGround(mapX, mapY) || map.isWater(mapX, mapY);
    }

    // ==================== PRIVATE METHODS ====================

    /**
     * Méthode qui applique la physique au projectile en fonction du temps écoulé.
     * 
     * @param time - Le temps écoulé depuis la dernière mise à jour
     */
    private void applyPhysics(double time) {
        if (wind != null && wind.enabled) {
            this.vx += wind.getForce() * time;
        }
        this.vy -= gravity * GRAVITY_MULTIPLIER * time;
        this.x += vx * time;
        this.y -= vy * time;
    }

    /**
     * Méthode qui mise à jour du statut de sortie de la case du tireur
     * 
     * @param shooterTileX - la coordonnée X de la tuile du tireur
     * @param shooterTileY - la coordonnée Y de la tuile du tireur
     */
    private void updateShooterTileStatus(double shooterTileX, double shooterTileY) {
        if (hasLeftShooterTile)
            return;

        double distance = Math.sqrt((x - shooterTileX) * (x - shooterTileX) + (y - shooterTileY) * (y - shooterTileY));
        if (distance > 0.5) {
            hasLeftShooterTile = true;
        }
    }

    /**
     * Méthode qui vérifie si le projectile a dépassé sa distance maximale.
     * Si c'est le cas, désactive le projectile.
     */
    private void checkDistanceLimit() {
        if (!hasDistanceLimit)
            return;

        double dx = x - startX;
        double dy = y - startY;

        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance >= maxDistance) {
            this.active = false;
        }
    }

    /**
     * Calcule la distance euclidienne
     * 
     * @param deltaX - Différence en X
     * @param deltaY - Différence en Y
     * 
     * @return La distance calculée
     */
    private double calculateDistance(double deltaX, double deltaY) {
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    /**
     * Méthode qui de vérification des conformités des coordonnées (en/hors carte).
     * 
     * @param mapX - Coordonnée X sur la carte
     * @param mapY - Coordonnée Y sur la carte
     * @param map - La carte du jeu
     * 
     * @return true, si hors limites
     */
    private boolean isOutOfBounds(double mapX, double mapY, Map map) {
        return mapX < 0 || mapY < 0 || mapX >= map.getWidth() || mapY >= map.getHeight();
    }
}