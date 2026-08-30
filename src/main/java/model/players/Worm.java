package model.players;

import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.Timer;

import app.Main;
import model.Config;
import model.GameModel;
import model.Map;
import model.items.Item;
import model.items.guns.Guns;
import model.items.tools.AirStrike;
import model.items.tools.Tools;
import model.physics.Projectile;
import view.gui.GuiView;
import view.gui.MapRenderer;
import view.gui.Particle;
import java.awt.Color;

/**
 * Classe Worm qui implémente les worms du jeu.
 * 
 * @author SANE Souleymane
 * 
 * @see ArrayList#getClass()
 * @see Math#getClass()
 * @see Config#getClass()
 * @see Main#getClass()
 * @see Map#getClass()
 * 
 * @since 0.0
 * @version 1.1
 */
public class Worm {

    // ==================== CONSTANTS ====================
    /** Vitesse de déplacement du worm */
    private static final double MOVE_SPEED = 0.2;
    /** Force de saut du worm */
    private static final double JUMP_FORCE = -0.3;
    /** Gravité vers le haut */
    private static final double GRAVITY_UP = 0.2;
    /** Gravité vers le bas */
    private static final double GRAVITY_DOWN = 0.02;
    /** Seuil de dégâts en cas de chute */
    private static final int FALL_DAMAGE_THRESHOLD = 1;
    /** Multiplicateur de dégâts en cas de chute */
    private static final int FALL_DAMAGE_MULTIPLIER = 5;

    // ==================== FIELDS ====================
    /** Points de vie initiaux */
    private static int hpInit = view.gui.SettingsPanel.getNbHpPerWorm();// app.Launcher.CONFIG.getIntParameter("HP");

    /** L'équipe à laquelle appartient le worm */
    private Team team;
    /** Le nom du worm */
    private String name;
    /** Les points de vie du worm */
    private int hp;
    /** La position du worm */
    private double x, y;
    /** La largeur du worm */
    private double width = 0.9;
    /** Le symbole du worm */
    private char symbol;
    /** L'identifiant de l'équipe du worm */
    private int teamId;
    /** L'angle de visée du worm */
    private double aimAngle = -Math.PI / 2;
    /** L'item sélectionné du worm */
    private Item selectedItem;
    /** La vélocité verticale du worm */
    private double verticalVelocity = 0;
    /** La vélocité horizontale du worm */
    private double horizontalVelocity = 0;
    /** Indique si le worm est en train de sauter */
    private boolean jumping = false;
    /** Indique si le worm fait face à l'ouest */
    private boolean facingWest = true;
    /** La position Y de départ du saut */
    private double jumpStartY = 0;
    /** La hauteur maximale atteinte pendant le saut */
    private double maxJumpHeight = 0;

    // ========================Builders========================//

    /**
     * Constructeur pour créer un Worm à partir du nom de son équipe et d'un nom.
     * 
     * @param team      L'équipe à laquelle appartient le worms
     * @param name      Une chaîne de caractère qui représentera le nom du Worm
     * @param symbol    Un caractère de l'équipe
     * 
     * @see ArrayList#isEmpty()
     * 
     * @since 1.1
     */
    public Worm(Team team, String name, char symbol) {
        this.team = team;
        if (name == null)
            this.name = app.Launcher.CONFIG.getWormRandomName(team.getName());
        this.name = name;
        this.hp = hpInit;
        this.teamId = team.getTeamId();
        this.symbol = symbol;
    }

    /**
     * Constructeur pour créer un Worm à partir d'un nom et d'un nombre de points de vie.
     * 
     * @param name      Une chaîne de caractère qui représentera le nom du Worm
     * @param hp        Un entier qui initialisera les points de vie du Worm
     * @param symbol    Un caractère représentant le symbole du Worm
     * @param teamId    L'identifiant de l'équipe du worm
     * 
     * @since 1.0
     */
    public Worm(String name, int hp, char symbol, int teamId) {
        this.name = name;
        this.hp = hp;
        this.symbol = symbol;
        this.teamId = teamId;
    }

    // ========================Accessors========================//

    /**
     * Getter pour obtenir le symbole du Worm.
     * 
     * @return Le caractère correspondant au symbole du Worm
     * 
     * @since 1.1
     */
    public char getSymbol() {
        return this.symbol;
    }

    /**
     * Getter pour obtenir l'identifiant de l'équipe du Worm.
     * 
     * @return Un entier correspondant à l'identifiant de l'équipe du Worm
     * 
     * @since 1.0
     */
    public int getTeamId() {
        return this.teamId;
    }

    /**
     * Getter pour obtenir la position X du Worm.
     * 
     * @return Un entier correspondant à la position X du Worm
     * 
     * @since 1.1
     */
    public double getX() {
        return x;
    }

    /**
     * Getter pour obtenir la position Y du Worm.
     * 
     * @return Un entier correspondant à la position Y du Worm
     * 
     * @since 1.1
     */
    public double getY() {
        return y;
    }

    /**
     * Setter pour définir la position X du Worm.
     * 
     * @param y La position Y à définir
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Getter pour obtenir l'équipe du Worm.
     * 
     * @return L'équipe du Worm
     * 
     * @since 1.1
     */
    public Team getTeam() {
        return team;
    }

    /**
     * Setter pour définir l'équipe du Worm.
     * 
     * @param team L'équipe à définir
     * 
     * @since 1.1
     */
    public void setTeam(Team team) {
        this.team = team;
    }

    /**
     * Getter pour obtenir le nom du Worm.
     * 
     * @return Une chaîne de caractère correspondant au nom du Worm
     * 
     * @since 1.1
     */
    public String getName() {
        return name;
    }

    /**
     * Setter pour définir le nom du Worm.
     * 
     * @param name Le nom à définir
     * 
     * @since 1.1
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter pour obtenir les points de vie du Worm.
     * 
     * @return Un entier correspondant aux points de vie du Worm
     * 
     * @since 1.1
     */
    public int getHp() {
        return hp;
    }

    /**
     * Méthode pour avoir le nombre d'hp initialisé
     * @return le nombre d'hp initialisé
     */
    public int getHpInit() {
        return hpInit;
    }

    /**
     * Setter pour définir les points de vie du Worm.
     * 
     * @param hp Le nombre de points de vie à définir
     * 
     * @since 1.1
     */
    public void setHp(int hp) {
        this.hp = hp;
    }

    /**
     * Setter pour définir la position du Worm.
     * 
     * @param x La position X à définir
     * @param y La position Y à définir
     * 
     * @since 1.1
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Getter permettant d'obtenir l'item sélectionnée (sert pour la version graphique)
     * 
     * @return selectedItem, l'item sélectionné
     * 
     * @since 2.0
     */
    public Item getSelectedItem() {
        return selectedItem;
    }

    /**
     * Setter permettant de définir l'arme sélectionnée (sert pour la version graphique)
     * 
     * @param selectedItem l'arme que l'on souhaite sélectionner
     * 
     * @since 2.0
     */
    public void setSelectedItem(Item selectedItem) {
        this.selectedItem = selectedItem;
    }

    /**
     * Getter pour obtenir l'angle de visée du Worm
     * 
     * @return un double correspondant à l'angle en radiant de visée du worms
     * 
     * @since 2.0
     */
    public double getAimAngle() {
        return aimAngle;
    }

    /**
     * Getter permettant de savoir si le worm fait face à l'ouest
     * 
     * @return true si le worm fait face à l'ouest, false sinon
     * 
     * @since 2.0
     */
    public boolean isFacingWest() {
        return facingWest;
    }

    /**
     * Getter permettant d'obtenir la hitbox du worm
     * 
     * @return la hitbox du worm
     * 
     * @since 2.0
     */
    public Rectangle getHitBox() {
        return new Rectangle((int) x, (int) y, 1, 1);
    }

    // ========================Methods========================//

    /**
     * Méthode permettant de savoir si un worm est encore en vie ou non. Le worm meurt à partir du moment où ses points de vie sont égaux à 0.
     * 
     * @return true le worm est mort, false sinon
     * 
     * @since 1.0
     */
    public boolean isDead() {
        return this.hp <= 0;
    }

    /**
     * Méthode pour faire sauter le worm.
     * 
     * @param toLeft   true pour sauter à gauche, false pour sauter à droite
     * @param map      La map où se trouve le worm
     * @param model    Le modèle du jeu
     * 
     * @return true, si le saut a pu être effectué, false sinon
     * 
     * @see Map#getWidth()
     * @see Map#isEmpty(int, int)
     * @see Map#setCell(int, int, char)
     * @see Worm#applyGravity(Map)
     * 
     * @since 1.0
     */
    public boolean jump(Map map, boolean toLeft, GameModel model) {
        int dx = toLeft ? -1 : 1;

        int newX = (int) this.x + dx;
        int newY = (int) this.y - 1;

        if (newX < 0 || newX >= map.getWidth() || newY < 0)
            return false;

        Worm wormOnDest = map.isWorm(newX, newY) ? getWormAt(map, newX, newY, model) : null;

        map.setCell((int) this.x, (int) this.y, ' ');
        this.x = newX;
        this.y = newY;

        if (wormOnDest != null) {
            map.setCell(newX, newY, wormOnDest.getSymbol());
        } else {
            map.setCell(newX, newY, this.symbol);
        }

        applyGravity(map);

        return true;
    }

    /**
     * Méthode pour faire sauter le worm verticalement.
     * 
     * @param map La map où se trouve le worm
     * 
     * @see Timer#getClass()
     * @see Map#isGround(int, int)
     * 
     * @since 2.0
     */
    public void jumpSmooth(Map map) {
        if (jumping || !isOnGround(map))
            return;

        jumping = true;
        verticalVelocity = JUMP_FORCE;

        jumpStartY = y;
        maxJumpHeight = y;
    }

    /**
     * Méthode pour faire sauter le worm vers la gauche.
     * 
     * @param map La map où se trouve le worm
     * @param model Le modèle du jeu
     * 
     * @return true, si le saut a pu être effectué, false sinon
     * 
     * @since 1.1
     */
    public boolean jumpLeft(Map map, GameModel model) {
        return jump(map, true, model);
    }

    /**
     * Méthode pour faire sauter le worm vers la droite.
     * 
     * @param map   La map où se trouve le worm
     * @param model Le modèle du jeu
     * 
     * @return true, si le saut a pu être effectué, false sinon
     * 
     * @since 1.1
     */
    public boolean jumpRight(Map map, GameModel model) {
        return jump(map, false, model);
    }

    /**
     * Méthode pour démarrer le déplacement vers la gauche du worm.
     * 
     * @since 2.0
     */
    public void startMoveLeft() {
        horizontalVelocity = -MOVE_SPEED;
        facingWest = true;
        if (aimAngle > 0) {
            aimAngle = -aimAngle;
        }
    }

    /**
     * Méthode pour démarrer le déplacement vers la droite du worm.
     * 
     * @since 2.0
     */
    public void startMoveRight() {
        horizontalVelocity = MOVE_SPEED;
        facingWest = false;
        if (aimAngle < 0) {
            aimAngle = -aimAngle;
        }
    }

    /**
     * Méthode pour arrêter le déplacement du worm.
     * 
     * @since 2.0
     */
    public void stopMove() {
        horizontalVelocity = 0;
    }

    /**
     * Méthode pour mettre à jour la position du worm, en fonction de sa vélocité horizontale et de la gravité du jeu.
     * 
     * @param map La map où se trouve le worm
     * @param view La vue graphique actuelle
     * 
     * @see Map#getWidth()
     * @see Map#isEmpty(int, int)
     * @see Map#isGround(int, int)
     * 
     * @since 2.0
     */
    public void update(Map map, GuiView view) {
        updateHorizontalPosition(map);
        updateVerticalPosition(map);

        if (checkWaterDeath(map, view))
            return;

        handleLanding(map, view);
    }

    /**
     * Méthode pour faire déplacer le worm vers la gauche.
     * 
     * @param map La map où se trouve le worm
     * 
     * @return true, si le déplacement a pu être effectué, false sinon
     * 
     * @since 1.1
     */
    public boolean moveLeft(Map map) {
        return moveHorizontal(map, -1);
    }

    /**
     * Méthode pour faire déplacer le worm vers la droite.
     * 
     * @param map La map où se trouve le worm
     * 
     * @return true, si le déplacement a pu être effectué, false sinon
     * 
     * @since 1.1
     */
    public boolean moveRight(Map map) {
        return moveHorizontal(map, 1);
    }

    /**
     * Méthode pour tirer sur un autre worm.
     * 
     * @param map La map où se trouve le worm
     * @param mouseX La position X de la souris lors du clic
     * 
     * @return le Projectile qui doit être tiré
     * 
     * @since 1.1
     */
    public Projectile shoot(Map map, double mouseX) {
        if (selectedItem instanceof Guns gun) {
            return gun.hasAmmo() ? gun.createProjectile(this) : null;
        }

        if (selectedItem instanceof Tools tool) {
            if (tool instanceof AirStrike airStrike) {
                return airStrike.useTool(this, (int) mouseX);
            }
            tool.useTool(this);
        }

        return null;
    }

    /**
     * Méthode permettant de faire "tourner" l'angle de visée du worms
     * 
     * @param delta Le mouvement de l'angle
     * @since 2.0
     */
    public void rotateAim(double delta) {
        aimAngle += delta;
    }

    /**
     * Méthode pour placer le worm à une position donnée sur la map.
     * 
     * @param x La position X où placer le worm
     * @param y La position Y où placer le worm
     * @param map La map où placer le worm
     * 
     * @see Map#canWormBePlaced(int, int)
     * @see Map#setCell(int, int, char)
     * @see Worm#getSymbol()
     * 
     * @since 1.1
     */
    public void placeOnMapAt(int x, int y, Map map) {
        if (map.canWormBePlaced(x, y)) {
            this.x = x;
            this.y = y;
            map.setCell(x, y, this.getSymbol());
        }
    }

    /**
     * Méthode pour savoir si le worm est sur le sol.
     * 
     * @param map La map où se trouve le worm
     * 
     * @see Map#isGround(int, int)
     * 
     * @return true, si le worm est sur le sol, false sinon
     * 
     * @since 2.0
     */
    public boolean isOnGround(Map map) {
        int footY = (int) Math.floor(y + 1);
        return map.isGround((int) Math.floor(x), footY) || map.isGround((int) Math.floor(x + 0.9), footY)
                || map.isWorm((int) Math.floor(x), footY) || map.isWorm((int) Math.floor(x + 0.9), footY);
    }



    /**
     * Méthode pour collecter une caisse.
     * 
     * @param crateItem L'item contenu dans la caisse
     * 
     * @see Team#getInventory()
     * 
     * @since 2.1
     */
    public void collectCrate(Item crateItem) {
        if (crateItem instanceof Guns gun) {
            collectGun(gun);
        } else if (crateItem instanceof Tools tool) {
            collectTool(tool);
        } else {
            team.getInventory().addItem(crateItem);
        }
    }

    /**
     * Méthode pour mettre à jour la position du worm sur la map (horizontalement)
     * 
     * @param map La map dans laquelle se trouve le worm
     * 
     * @since 2.0
     */
    private void updateHorizontalPosition(Map map) {
        double nextX = x + horizontalVelocity;
        if (canMoveHorizontally(map, nextX)) {
            x = nextX;
        }
    }

    /**
     * Méthode pou savoir si un worm peut se mouvoir (gauche/droite) dans les limite de la map, qui permet aussi de détecter un obstacle
     * 
     * @param map La map dans laquelle se trouve le worm
     * @param nextX La position X ciblée
     * 
     * @return true, si on peut se déplacer, false sinon
     */
    private boolean canMoveHorizontally(Map map, double nextX) {
        double right = nextX + width;

        if (!(map.isEmpty(nextX, y) || map.isWorm(nextX, y)))
            return false;
        if (!(map.isEmpty(nextX, y + 0.9) || map.isWorm(nextX, y + 0.9)))
            return false;
        if (!(map.isEmpty(right, y) || map.isWorm(right, y)))
            return false;
        if (!(map.isEmpty(right, y + 0.9) || map.isWorm(right, y + 0.9)))
            return false;

        return true;
    }

    /**
     * Méthode pour détecter la présence d'eau et tuer le worm
     * 
     * @param map La map dans laquelle le worm se trouve
     * @param view La vue graphique actuelle
     * 
     * @return true, si le worm meurt (on passe le tour), false sinon
     */
    private boolean checkWaterDeath(Map map, GuiView view) {
        if ((map.isWater((int) x, (int) y + 2)) && (map.isWater((int) x, (int) y + 1))) {
            hp = 0;
            map.setCell((int) x, (int) y, 'W');
            if (!view.model.isGameOver()) {
                view.soundPlayer.playSoundEffect("/sounds/sounds_effects/water.wav");
                for (int i = 0; i < 15; i++) {
                    double angle = Math.random() * Math.PI - Math.PI / 2;
                    double speed = Math.random() * 2 + 1;
                    double dx = Math.cos(angle) * speed;
                    double dy = Math.sin(angle) * speed;
                    view.addParticles(new Particle(
                            (view.model.getCurrentWorm().getX() + 1.0 / 2) * MapRenderer.TILE_SIZE,
                            (view.model.getCurrentWorm().getY() + 1.0 / 2) * MapRenderer.TILE_SIZE,
                            dx, dy,
                            new Color(123, 218, 237),
                            40));
                }
                view.startParticleTimer();
            }
            view.handleTurnEnd();
            return true;
        }
        return false;
    }

    /**
     * Méthode pour mettre à jour la position du worm sur la map (verticalement)
     * 
     * @param map La map dans laquelle se trouve le worm
     * 
     * @since 2.0
     */
    private void updateVerticalPosition(Map map) {
        double left = x;
        double right = x + width;
        boolean onGround = map.isGround((int) left, (int) (y + 1)) && map.isGround((int) (right), (int) (y + 1));

        if (!onGround || verticalVelocity < 0) {
            verticalVelocity += GRAVITY_DOWN;
            double nextY = y + verticalVelocity;

            if (verticalVelocity < 0) {
                moveUpWhilePossible(map, nextY);
            } else {
                moveDownWhilePossible(map, nextY);
            }
        }
    }

    /**
     * Méthode qui permet de déplacer le worm vers le haut tant que cela st possible. Hauteur de saut maximale atteinte puis redescente sur le sol (avec la gravité)
     * 
     * @param map La map où se trouve le worm
     * @param targetY La position Y ciblée
     */
    private void moveUpWhilePossible(Map map, double targetY) {
        double right = x + width;
        while (y > targetY && y - 1 >= 0 &&
                map.isEmpty(x, y - 1) &&
                map.isEmpty(right, y - 1)) {
            y -= GRAVITY_UP;
            maxJumpHeight = Math.min(maxJumpHeight, y);
        }
    }

    /**
     * Méthode qui permet de déplacer le worm vers le bas (descnedre d'une tuile) tant que cela st possible.
     * 
     * @param map La map où se trouve le worm
     * @param targetY La position Y ciblée
     */
    private void moveDownWhilePossible(Map map, double targetY) {
        double left = x;
        double right = x + width;
        while (y < targetY &&
                map.isEmpty((int) left, (int) (y + 1)) &&
                map.isEmpty((int) (right), (int) (y + 1))) {
            y += GRAVITY_DOWN;
        }
    }

    /**
     * Méthode pour gérer l'atterrissage du worm et les potentiels dégâts de chute.
     * 
     * @param map La map où se trouve le worm
     * @param view la vue graphique actuelle
     */
    private void handleLanding(Map map, GuiView view) {
        if (isOnGround(map) && verticalVelocity >= 0) {
            if (jumping) {
                handleFallDamage(map, view);
            }
            verticalVelocity = 0;
            jumping = false;
        }
    }

    /**
     * Méthode pour gérer les dégâts de chute en fonction de la hauteur de saut.
     * 
     * @param map La map où se trouve le worm
     * @param view La vue graphique actuelle
     */
    private void handleFallDamage(Map map, GuiView view) {
        double jumpHeight = jumpStartY - maxJumpHeight;
        double totalFall = y - maxJumpHeight;
        double extraFall = totalFall - jumpHeight;

        if (extraFall > FALL_DAMAGE_THRESHOLD) {
            int damage = (int) (extraFall - FALL_DAMAGE_THRESHOLD) * FALL_DAMAGE_MULTIPLIER;
            hp -= damage;

            if (isDead()) {
                map.setCell((int) x, (int) y, ' ');
                view.handleTurnEnd();
            }
        }
    }

    /**
     * Méthode pour le déplacement horizontal du worm.
     * 
     * @param map La map dans laquelle se trouve le worm
     * @param direction horizontal (gauche : -1, droite : 1)
     * 
     * @return true, si le déplacement a pu être effectué, false sinon
     */
    private boolean moveHorizontal(Map map, int direction) {
        double newX = this.x + direction;

        if (newX < 0 || newX >= map.getWidth())
            return false;
        if (!map.isEmpty(newX, this.y))
            return false;

        map.setCell((int) this.x, (int) this.y, ' ');
        this.x = newX;
        map.setCell((int) this.x, (int) this.y, this.symbol);
        this.applyGravity(map);
        return true;
    }

    /**
     * Méthode pour appliquer la gravité au worm. Le fait tomber s'il n'y a rien en dessous de lui/meurt dans l'eau.
     * 
     * @param map La map dans laquelle se trouve le worm
     */
    private void applyGravity(Map map) {
        int height = map.getHeight();
        int fallDistance = 0;

        while (canFallFurther(map, height)) {
            if (map.isWater((int) x, (int) (y + 1)) || map.isWater((int) x, (int) y)) {
                this.hp = 0;
                return;
            }

            map.setCell((int) x, (int) y, ' ');
            y++;
            map.setCell((int) x, (int) y, this.symbol);
            fallDistance++;
        }

        applyFallDamage(fallDistance);
    }

    /**
     * Méthode pour vérifier si le worm peut continuer à tomber.
     * 
     * @param map La map dans laquelle se trouve le worm
     * @param height La hauteur de la map
     * 
     * @return true, si le worm peut continuer à tomber, false sinon
     */
    private boolean canFallFurther(Map map, int height) {
        return this.y + 1 < height && (map.isEmpty((int) x, (int) (y + 1)) || map.isWater((int) x, (int) (y + 1)));
    }

    /**
     * Méthode pour appliquer les dégâts de chute au worm en fonction de la hauteur de chute.
     * 
     * @param fallDistance La distance de chute
     */
    private void applyFallDamage(int fallDistance) {
        if (fallDistance > FALL_DAMAGE_THRESHOLD) {
            int damage = (fallDistance - FALL_DAMAGE_THRESHOLD) * FALL_DAMAGE_MULTIPLIER;
            this.hp = Math.max(0, this.hp - damage);
        }
    }

    /**
     * Méthode pour collecter (ajouter) une arme à feu (à l'inventaire de l'équipe).
     * 
     * @param gun L'arme à collecter
     */
    private void collectGun(Guns gun) {
        Guns ownedGun = team.getInventory().getGun(gun.getClass());
        if (ownedGun != null) {
            ownedGun.setAmmo(ownedGun.getAmmo() + gun.getAmmo());
        } else {
            team.getInventory().addItem(gun);
        }
    }

    /**
     * Méthode pour collecter (ajouter) un outil (à l'inventaire de l'équipe).
     * 
     * @param tool L'outil à collecter
     */
    private void collectTool(Tools tool) {
        Tools ownedTool = team.getInventory().getTool(tool.getClass());
        if (ownedTool != null) {
            ownedTool.setAmmo(ownedTool.getAmmo() + tool.getAmmo());
        } else {
            team.getInventory().addItem(tool);
        }
    }

    /**
     * Méthode pour obtenir le worm à une position (coordonnées) donnée sur la map.
     * 
     * @param map La map dans laquelle se trouve le worm
     * @param x La position X ciblée
     * @param y La position Y ciblée
     * @param model Le modèle du jeu
     * 
     * @return Le worm à la position donnée, null sinon
     */
    private Worm getWormAt(Map map, int x, int y, GameModel model) {
        for (Team team : model.getTeams()) {
            for (Worm w : team.getWorms()) {
                if ((int) w.getX() == x && (int) w.getY() == y)
                    return w;
            }
        }
        return null;
    }
}