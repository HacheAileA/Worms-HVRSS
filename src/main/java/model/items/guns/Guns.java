package model.items.guns;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.GameModel;
import model.items.Item;
import model.physics.Projectile;
import model.players.Team;
import model.players.Worm;

/**
 * Cette classe implémente les méthodes communes aux différentes armes du jeu.
 * 
 * @author SAMBA Seth-Ederik
 * 
 * @see Math#getClass()
 * @see Random#getClass()
 * @see Worm#getClass()
 * 
 * @since 1.0
 * 
 * @version 2.1
 */
public abstract class Guns implements Item {

    private static final Random RANDOM = new Random();
    private final String name;

    /**
     * Le nombre de munitions.
     */
    protected int ammo;

    /**
     * Le nombre maximum de munitions.
     */
    protected int maxAmmo;

    /**
     * Les dégâts par balle.
     */
    protected int damagesPerBullets;

    /**
     * La vitesse du projectile
     */
    protected double projectileSpeed;
    /**
     * La gravitée subit par le projectile
     */
    protected double gravity = 4;

    /**
     * Le rayon d'explosion du projectile
     */
    protected int explosionRadius = 0;

    /**
     * La capacité de destruction de l'arme
     */
    protected boolean canDestruct = false;

    /**
     * L'équipe à laquelle appartient l'arme
     */
    protected Team team;

    /**
     * Le modèle du jeu
     */
    protected GameModel model;
    // ========================Builders========================//

    /**
     * Constructeur de la classe Guns.
     * 
     * @param name - Nom de l'arme
     * @param maxAmmo - Nombre maximum de munitions
     * @param damages - Dégâts par balle
     * @param team - L'équipe à laquelle appartient l'arme
     * 
     * @since 1.0
     */
    public Guns(String name, int maxAmmo, int damages, Team team) {
        this.ammo = maxAmmo;
        this.name = name;
        this.maxAmmo = maxAmmo;
        this.damagesPerBullets = damages;
        setTeam(team);
    }

    // ========================Accessors========================//

    /**
     * Getter pour obtenir l'équipe à laquelle appartient l'arme.
     * 
     * @return L'équipe de l'arme
     * 
     * @since 1.1
     */
    public Team getTeam() {
        return team;
    }

    /**
     * Setter pour initialiser l'équipe à laquelle appartient l'arme.
     * 
     * @param team - L'équipe de l'arme
     * 
     * @since 1.1
     */
    public void setTeam(Team team) {
        this.team = team;
    }

    /**
     * Getter pour obtenir le nom de l'arme.
     * 
     * @return Une chaîne caractères correspondant au nom de l'arme
     * 
     * @since 1.0
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter pour initialiser le modèle du jeu
     * 
     * @param model - Le modèle du jeu
     * 
     * @since 1.1
     */
    public void setModel(GameModel model) {
        this.model = model;
    }

    /**
     * Getter pour obtenir les dégâts par balle de l'arme.
     * 
     * @return Un entier correspondant aux dégâts par balle de l'arme
     * 
     * @since 1.0
     */
    public int getDamagesPerBullets() {
        return this.damagesPerBullets;
    }

    /**
     * Getter pour obtenir le nombre de munitions maximales de l'arme.
     * 
     * @return Un entier correspondant au nombre de munitions maximales de l'arme
     * 
     * @since 1.0
     */
    public int getMaxAmmo() {
        return this.maxAmmo;
    }

    /**
     * Getter pour obtenir le nombre de munitions actuelles de l'arme.
     * 
     * @return Un entier correspondant au nombre de munitions actuelles de l'arme
     * 
     * @since 1.0
     */
    @Override
    public int getAmmo() {
        return this.ammo;
    }

    /**
     * Setter pour initialiser le nombre de munitions actuelles de l'arme.
     * 
     * @param ammo - Un entier correspondant au nombre de munitions
     * 
     * @since 1.0
     * 
     * @version 1.1
     */
    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }

    /**
     * Setter pour initialiser les dégâts par balle de l'arme.
     * 
     * @param newDamage - Un entier représentant les nouveaux dégâts
     * 
     * @since 1.0
     */
    protected void setDamagesPerBullets(int newDamage) {
        this.damagesPerBullets = newDamage;
    }

    /**
     * Méthode permettant de savoir si une arme possède encore des munitions.
     * 
     * @return true si l'arme possède encore des munitions, false sinon
     * 
     * @since 1.0
     */
    public boolean hasAmmo() {
        return this.getAmmo() > 0;
    }

    /**
     * Setter pour initialiser la vitesse de l'arme
     * 
     * @param speed - la vitesse des projectiles
     */
    public void setSpeed(double speed) {
        this.projectileSpeed = speed;
    }

    /**
     * Setter pour initialiser la capacité de destruction de l'arme
     * 
     * @param r - le rayon d'explosion
     * 
     * @since 2.0
     */
    public void setDestructFunctionEnable(int r) {
        if (r != 0) {
            this.canDestruct = true;
        }
        this.explosionRadius = r;
    }

    /**
     * Retourne la vitesse du projectile
     * 
     * @return un double correspondant à la vitesse du projectile
     * 
     * @since 2.0
     */
    public double getProjectileSpeed() {
        return projectileSpeed;
    }

    /**
     * Retourne la gravitée du projectile
     * 
     * @return un double correspondant à la gravitée subie par la projectile
     * 
     * @since Z.0
     */
    public double getGravity() {
        return gravity;
    }

    /**
     * Retourne le rayon d'explosion du projectile
     * 
     * @return un entier correspondant au rayon d'explosion
     * 
     * @since 2.1
     */
    public boolean isCanDestruct() {
        return canDestruct;
    }

    /**
     * Setter pour initialiser la gravitée du projectile
     * 
     * @param gravity - la gravitée subie par le projectile
     */
    public void setGravity(double gravity) {
        this.gravity = gravity;
    }

    // ========================Methods========================//
    /**
     * Méthode abstraite pour copier une arme.
     * 
     * @return Une nouvelle instance de l'arme copiée
     * 
     * @since 1.0
     */
    public abstract Guns copy();

    /**
     * Méthode pour tirer aléatoirement avec 50% de chance de toucher sa cible en vue console.
     * 
     * @return true, si le tir a touché, false sinon
     * 
     * @see Math#random()
     * 
     * @since 1.0
     */
    protected boolean randomHitConsole() {
        return RANDOM.nextBoolean();
    }

    /**
     * Tire sur un worm avec une arme désignée et lui enlève des PV s'il est touché (version console).
     * 
     * @param gun - Arme utilisée pour effectuer le tir
     * @param opponent - Worm sur lequel on tire
     * 
     * @see Guns#getAmmo()
     * @see Guns#getDamagesPerBullets()
     * @see Guns#hasAmmo()
     * @see Guns#randomHitConsole()
     * @see Guns#setAmmo(int)
     * 
     * @since 1.0
     */
    public static void shootConsole(Guns gun, Worm opponent) {
        if (gun.hasAmmo() && opponent != null) {
            if (gun.randomHitConsole()) {
                opponent.setHp(opponent.getHp() - gun.getDamagesPerBullets());
                System.out.println("Touché !");
            } else {
                System.out.println("Raté !");
            }
            gun.setAmmo(gun.getAmmo() - 1);
        } else {
            System.out.println("Plus de munitions disponible");
        }
    }

    /**
     * Méthode permettant de créer un projectile tiré par le worm
     * 
     * @param shooter - Le worm qui tire le projectile
     * 
     * @see Worm#getX()
     * @see Worm#getAimAngle()
     * 
     * @return Le projectile créé, ou null si pas assez de munitions
     * 
     * @since 2.0
     */
    public Projectile createProjectile(Worm shooter) {
        if (!hasAmmo())
            return null;

        setAmmo(getAmmo() - 1);

        return new Projectile(shooter.getX(), shooter.getY(), shooter.getAimAngle(), projectileSpeed, gravity,
                damagesPerBullets, explosionRadius, shooter);
    }

    /**
     * Méthode qui permet de créer plusieurs projectiles tirés par le worm. Par défaut, cette méthode crée un seul projectile.
     * Les armes spéciales (comme le ShotGun) peuvent override cette méthode, pour créer plusieurs projectiles.
     * 
     * @param shooter - Le worm qui tire les projectiles
     * 
     * @return Une liste de projectiles créés
     * 
     * @since 2.1
     */
    public List<Projectile> createProjectiles(Worm shooter) {
        List<Projectile> projectiles = new ArrayList<>();
        Projectile p = createProjectile(shooter);
        if (p != null) {
            projectiles.add(p);
        }
        return projectiles;
    }

}