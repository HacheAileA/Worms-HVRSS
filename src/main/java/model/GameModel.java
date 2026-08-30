package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.items.crates.CrateManager;
import model.physics.Projectile;
import model.physics.Wind;
import model.players.Team;
import model.players.Worm;
import view.GameView;

/**
 * Classe GameModel qui gère l'état global du jeu.
 * 
 * @author ARNAUD Hugo
 * @author MESNILDREY Valentin
 * @author SAMBA Seth-Ederik
 * 
 * @see ArrayList#getClass()
 * @see Map#getClass()
 * @see Team#getClass()
 * @see Worm#getClass()
 * 
 * @since 0.0
 * 
 * @version 2.0
 */
public class GameModel {

    // ========================Attributes========================//
    /** La liste des équipes dans le jeu */
    private ArrayList<Team> teams;
    /** La carte du jeu */
    private Map map;
    /** Le ver actuellement actif */
    private Worm currentWorm;
    /** L'équipe actuellement active */
    private Team currentTeam;
    /** Indique si le vent est activé */
    private boolean windEnabled = true;
    /** Le nombre d'équipes dans le jeu */
    private int nbTeams;
    /** Le nombre de vers par équipe */
    private int nbWormsPerTeam;
    /** La liste des projectiles sur le terrain */
    private ArrayList<Projectile> projectiles = new ArrayList<>();
    /** L'option des tirs alliés */
    private boolean friendlyFire;
    /** Le vent dans le jeu */
    private Wind wind = new Wind();
    /** Le gestionnaire de caisses */
    private CrateManager crateManager;
    /** Visualisation ou non du devMod */
    private boolean devMode = false;
    /** Liste des vues */
    private List<GameView> views = new ArrayList<>();

    // ========================Builders========================//

    /**
     * Constructeur pour créer un nouveau modèle vide.
     * 
     * @see ArrayList#ArrayList()
     * 
     * @since 1.1
     */
    public GameModel() {
        this.teams = new ArrayList<>();
        this.map = null;
        this.nbTeams = 0;
        this.nbWormsPerTeam = 0;
        this.friendlyFire = true;
        wind.enabled = windEnabled;
        this.crateManager = new CrateManager(nbWormsPerTeam, nbTeams);
    }

    /**
     * Constructeur pour créer un nouveau modèle à partir d'une liste d'équipes et
     * d'une carte (utile pour la sauvegarde).
     * 
     * @param teams        - Les équipes à utiliser
     * @param map          - La carte à utiliser
     * @param friendlyFire - L'option des tirs alliés
     * 
     * @see ArrayList#getClass()
     * @see Map#getClass()
     * @see Team#getClass()
     * @see Worm#getClass()
     * 
     * @since 1.1
     */
    public GameModel(ArrayList<Team> teams, Map map, boolean friendlyFire) {
        this.teams = teams;
        this.map = map;
        this.friendlyFire = friendlyFire;
        wind.enabled = windEnabled;
        initializeCurrentTeamAndWorm();
        this.crateManager = new CrateManager(nbWormsPerTeam, nbTeams);
    }

    // ========================Accessors========================//

    /**
     * Getter pour récupérer la carte.
     * 
     * @return La carte actuelle
     * 
     * @since 1.1
     */
    public Map getMap() {
        return this.map;
    }

    /**
     * Setter pour définir une nouvelle carte.
     * 
     * @param map - La nouvelle carte
     * 
     * @since 1.1
     */
    public void setMap(Map map) {
        this.map = map;
    }

    /**
     * Setter pour définir un nouveau worm actuel.
     * 
     * @param newCurrentWorm - Le nouveau worm actuel
     * 
     * @since 1.1
     */
    public void setCurrentWorm(Worm newCurrentWorm) {
        this.currentWorm = newCurrentWorm;
    }

    /**
     * Getter pour récupérer le worm actuel.
     * 
     * @return Le worm actuel
     * 
     * @since 1.1
     */
    public Worm getCurrentWorm() {
        return currentWorm;
    }

    /**
     * Getter pour récupérer l'équipe actuelle.
     * 
     * @return L'équipe actuelle
     * 
     * @since 1.1
     */
    public Team getCurrentTeam() {
        return currentTeam;
    }

    /**
     * Setter pour définir une nouvelle équipe actuelle.
     * 
     * @param currentTeam - La nouvelle équipe actuelle
     * 
     * @since 1.1
     */
    public void setCurrentTeam(Team currentTeam) {
        this.currentTeam = currentTeam;
    }

    /**
     * Getter pour récupérer la liste des équipes.
     * 
     * @return La liste des équipes
     * 
     * @since 1.1
     */
    public ArrayList<Team> getTeams() {
        return teams;
    }

    /**
     * Setter pour définir la liste des équipes.
     * 
     * @param teams - La nouvelle liste des équipes
     * 
     * @since 1.1
     */
    public void setTeams(ArrayList<Team> teams) {
        this.teams = teams;
    }

    /**
     * Setter pour définir si le vent est activé.
     * 
     * @param windEnabled - true pour activer le vent, false pour le désactiver
     * 
     * @since 2.0
     */
    public void setWindEnabled(boolean windEnabled) {
        this.windEnabled = windEnabled;
    }

    /**
     * Getter pour savoir si le vent est activé.
     * 
     * @return true si le vent est activé, false sinon
     * 
     * @since 2.0
     */
    public boolean isWindEnabled() {
        return windEnabled;
    }

    /**
     * Getter pour récupérer l'option des tirs alliés.
     * 
     * @return L'option des tirs alliés
     * 
     * @since 2.0
     */
    public boolean getFriendlyFire() {
        return this.friendlyFire;
    }

    /**
     * Getter pour récupérer l'équipe gagnante.
     * 
     * @return L'équipe gagnante, ou null
     * 
     * @see ArrayList#get(int)
     * @see ArrayList#size()
     * @see Team#containsWormAlive()
     * @see GameModel#isGameOver()
     * 
     * @since 1.0
     */
    public Team getWinningTeam() {
        if (this.teams.isEmpty() || this.teams.size() < 2) {
            return null;
        }

        Team team1 = this.teams.get(0);
        Team team2 = this.teams.get(1);

        boolean team1Alive = team1.containsWormAlive();
        boolean team2Alive = team2.containsWormAlive();

        if (team1Alive && !team2Alive)
            return team1;
        if (team2Alive && !team1Alive)
            return team2;

        return null;
    }

    /**
     * Trouve un ver par son nom.
     * 
     * @param name - Le nom du ver à trouver
     * 
     * @see String#equals(Object)
     * @see Team#getWorms()
     * @see Worm#getName()
     * @see ArrayList#isEmpty()
     * @see ArrayList#get(int)
     * @see ArrayList#size()
     * 
     * @return Le Worm trouvé, ou null
     * 
     * @since 1.1
     */
    public Worm getWormByName(String name) {
        for (Team t : this.teams) {
            for (Worm w : t.getWorms()) {
                if (w.getName() != null && w.getName().equals(name)) {
                    return w;
                }
            }
        }
        return null;
    }

    /**
     * Méthode pour vérifier si la partie est finie.
     * 
     * @return true, si le nombre d'équipe avec des vers vivants est inférieur ou
     *         égal, false sinon
     * 
     * @since 1.0
     */

    public boolean isGameOver() {
        int aliveTeams = 0;
        for (Team t : this.teams) {
            if (t.containsWormAlive()) {
                aliveTeams++;
            }
        }
        return aliveTeams <= 1;
    }

    /**
     * Getter pour obtenir la liste des projectiles sur le terrain
     * 
     * @see ArrayList#getClass()
     * 
     * @return l'ArrayListe des projectiles
     * 
     * @since 2.0
     */
    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    /**
     * Getter pour obtenir le vent
     * 
     * @return le Wind
     * 
     * @since 2.1
     */
    public Wind getWind() {
        return wind;
    }

    /**
     * Getter pour obtenir le gestionnaire de caisses
     * 
     * @return le CrateManager
     * 
     * @since 2.1
     */
    public CrateManager getCrateManager() {
        return crateManager;
    }

    /**
     * Méthode qui permet de savoir si le mode développeur est activé
     * 
     * @return true si le mode développeur est activé, false sinon
     * 
     * @since 2.1
     */
    public boolean isDevMode() {
        return devMode;
    }

    /**
     * Méthode qui permet de basculer le mode développeur
     * 
     * @since 2.1
     */
    public void toggleDevMode() {
        devMode = !devMode;
    }

    // ========================Methods========================//

    /**
     * Méthode d'initialisation du modèle du jeu avec des équipes et une carte.
     * 
     * @param teams        - La liste des équipes
     * @param map          - La carte à utiliser
     * @param friendlyFire - L'option des tirs alliés
     * 
     * @see ArrayList#getClass()
     * @see Map#getClass()
     * @see Team#getClass()
     * @see Worm#getClass()
     * 
     * @since 1.1
     */
    public void init(ArrayList<Team> teams, Map map, boolean friendlyFire) {
        this.teams = teams;
        this.map = map;
        this.friendlyFire = friendlyFire;

        if (!teams.isEmpty()) {
            this.currentTeam = teams.get(0);
            if (!currentTeam.getWorms().isEmpty()) {
                this.currentWorm = currentTeam.getWorms().get(0);
            } else {
                this.currentWorm = null;
            }
        } else {
            this.currentTeam = null;
            this.currentWorm = null;
        }
    }

    /**
     * Méthode de mise à jour de l'ordre des Worms.
     * 
     * @see ArrayList#add(Object)
     * @see ArrayList#remove(int)
     * @see Team#getWorms()
     * 
     * @since 1.0
     */
    public void update() {
        Worm first = this.currentWorm;
        this.currentTeam.getWorms().add(first);
        this.currentTeam.getWorms().remove(0);
    }

    /**
     * Méthode qui permet de passer au tour suivant en désignant le prochain ver et
     * la prochaine équipe.
     * 
     * @see ArrayList#get(int)
     * @see ArrayList#isEmpty()
     * @see GameModel#update()
     * @see Team#getWorms()
     * @see Worm#isDead()
     * @see GameModel#isGameOver()
     * @see GameModel#removeDeadWorms()
     * @see GameModel#removeEmptyTeams()
     * @see GameModel#ensureCurrentTeamIsValid()
     * @see GameModel#rotateCurrentTeam()
     * @see GameModel#updateCurrentWorm()
     * @see GameModel#handleTurnEvents()
     * 
     * @since 1.1
     */
    public void nextTurn() {
        currentWorm.setSelectedItem(null);
        if (isGameOver())
            return;

        removeDeadWorms();
        removeEmptyTeams();
        ensureCurrentTeamIsValid();
        rotateCurrentTeam();
        updateCurrentWorm();
        handleTurnEvents();
    }

    /**
     * Méthode de génération d'un nouveau vent au début du tour, si l'option est
     * activée.
     * 
     * @see Wind#generateRandom()
     * 
     * @since 2.0
     */
    public void newTurnWind() {
        if (wind.enabled) {
            wind.generateRandom();
        }
    }

    /**
     * Méthode de suppression d'un ver du jeu.
     * 
     * @param worm - Le ver à supprimer.
     * 
     * @see ArrayList#get(int)
     * @see ArrayList#remove(int)
     * @see ArrayList#size()
     * @see Team#getWorms()
     * 
     * @since 1.1
     */
    public void deleteWorm(Worm worm) {
        for (Team team : this.teams) {
            if (team.getWorms().remove(worm)) {
                return;
            }
        }
    }

    /**
     * Méthode de mise à jour de la position des projectiles et de gestion des
     * collisions avec les worms et le terrain.
     * 
     * @param dt - correspondant à la durée d'update des projectiles
     * 
     * @see ArrayList#iterator()
     * @see Iterator#hasNext()
     * @see Iterator#next()
     * @see Iterator#remove()
     * @see GameModel#getWormAt(double, double)
     * @see GameModel#deleteWorm(Worm)
     * @see Projectile#update(double, int, int)
     * @see Projectile#getShooter()
     * @see Projectile#getX()
     * @see Projectile#getY()
     * @see Projectile#getDamage()
     * @see Projectile#shouldBeDestroyed(Map)
     * @see Projectile#isActive()
     * @see Map#isGround(int, int)
     * @see Map#createExplosion(int, int, int)
     * @see GameModel#applyAreaDamage(Projectile, Worm)
     * @see GameModel#updateTerrainAfterDestruction()
     * 
     * @since 2.0
     */
    public void updateProjectiles(double dt) {
        Iterator<Projectile> it = projectiles.iterator();
        while (it.hasNext()) {
            Projectile p = it.next();

            if (!p.isActive()) {
                it.remove();
                continue;
            }

            p.update(dt, p.getShooter().getX(), p.getShooter().getY());

            if (!p.isActive()) {
                it.remove();
                continue;
            }

            if (handleWormCollision(p, it))
                continue;
            if (handleTerrainCollision(p, it))
                continue;
        }

        updateTerrainAfterDestruction();
    }

    /**
     * Méthode de mise à jour du terrain, après une destruction et de gestiondes
     * chutes des worms.
     * 
     * @see GameModel#teams
     * @see Team#getWorms()
     * @see Map#isGround(int, int)
     * @see Map#isWater(int, int)
     * @see Worm#getX()
     * @see Worm#getY()
     * @see Worm#setY(double)
     * @see Worm#getHp()
     * @see Worm#isDead()
     * @see GameModel#deleteWorm(Worm)
     * 
     * @return true, si tous les worms sont toujours en vie après la mise à jour,
     *         false si un worm est tombé dans l'eau et a été supprimé
     * 
     * @since 2.0
     */
    public boolean updateTerrainAfterDestruction() {
        for (Team team : teams) {
            Iterator<Worm> wormIterator = team.getWorms().iterator();
            while (wormIterator.hasNext()) {
                Worm worm = wormIterator.next();
                if (!processWormFall(worm, wormIterator)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Méthode d'application des dégâts de zone
     * 
     * @param p      - Le projectile
     * @param target - Le worm qui a déjà été touché (ou null)
     * 
     * @see ArrayList#add(Object)
     * @see ArrayList#ArrayList()
     * @see GameModel#deleteWorm(Worm)
     * @see Projectile#getDamage()
     * @see Projectile#getExplosionRadius()
     * @see Projectile#getMapX()
     * @see Projectile#getMapY()
     * @see Projectile#getShooter()
     * @see Team#getWorms()
     * @see Worm#getX()
     * @see Worm#isDead()
     * @see Worm#getY()
     * @see Worm#getHp()
     * @see Worm#setHp(int)
     * 
     * @since 2.0
     */
    public void applyAreaDamage(Projectile p, Worm target) {
        double projectileX = p.getMapX();
        double projectileY = p.getMapY();
        Worm shooter = p.getShooter();
        int radius = p.getExplosionRadius();
        int damage = p.getDamage();
        ArrayList<Worm> wormsToDelete = new ArrayList<>();

        for (Team team : this.teams) {
            for (Worm worm : team.getWorms()) {
                if (shouldApplyDamageToWorm(worm, target, shooter, projectileX, projectileY, radius)) {
                    worm.setHp(worm.getHp() - damage);
                    if (worm.isDead()) {
                        wormsToDelete.add(worm);
                    }
                }
            }
        }

        for (Worm w : wormsToDelete) {
            deleteWorm(w);
        }
    }

    /**
     * Méthode pour ajouter une vue à la liste des vues
     * @param view La vue que l'on souhaite ajouter
     */
    public void addView(GameView view) {
        views.add(view);
    }

    /**
     * Méthode pour correctement supprimer le bug de collision dans la vue GUI
     * @param worm Le worm à supprimer (du bug de collision)
     * @param map La map sur laquelle le worm se situe.
     */
    public void notifyWormPlaced(Worm worm, Map map) {
        for (GameView view : views) {
            view.onWormPlaced(worm, map);
        }
    }

    /**
     * Méthode d'initialisation de l'équipe et du worm actuels.
     * 
     * @see ArrayList#get(int)
     * @see ArrayList#isEmpty()
     * @see Team#getWorms()
     * @see Worm#getClass()
     * 
     * @since 1.1
     */
    private void initializeCurrentTeamAndWorm() {
        if (!teams.isEmpty()) {
            this.currentTeam = teams.get(0);
            this.currentWorm = !currentTeam.getWorms().isEmpty() ? currentTeam.getWorms().get(0) : null;
        } else {
            this.currentTeam = null;
            this.currentWorm = null;
        }
    }

    /**
     * Méthode de suppression des worms morts.
     * 
     * @see Team#getWorms()
     * @see Worm#isDead()
     * @see ArrayList#removeIf(java.util.function.Predicate)
     * 
     * @since 1.1
     */
    private void removeDeadWorms() {
        for (Team team : teams) {
            team.getWorms().removeIf(Worm::isDead);
        }
    }

    /**
     * Méthode de suppression des équipes sans effectif.
     * 
     * @see Team#getWorms()
     * @see ArrayList#removeIf(java.util.function.Predicate)
     * 
     * @since 1.1
     */
    private void removeEmptyTeams() {
        teams.removeIf(team -> team.getWorms().isEmpty());
    }

    /**
     * Méthode pour s'assurer que l'équipe actuelle est valide. Si l'équipe actuelle
     * n'est plus dans la liste des équipes, elle est réinitialisée à la première
     * équipe.
     * 
     * @see ArrayList#contains(Object)
     * @see ArrayList#get(int)
     * @see ArrayList#isEmpty()
     * @see ArrayList#size()
     * @see Team#getClass()
     * 
     * @since 1.1
     */
    private void ensureCurrentTeamIsValid() {
        if (!teams.contains(currentTeam)) {
            currentTeam = teams.get(0);
        }
    }

    /**
     * Méthode pour faire passer l'équipe actuelle vers la prochaine équipe dans la
     * liste.
     * 
     * @see ArrayList#indexOf(Object)
     * @see ArrayList#get(int)
     * @see ArrayList#size()
     * @see Team#getWorms()
     * @see ArrayList#isEmpty()
     * @see ArrayList#add(Object)
     * @see ArrayList#remove(int)
     * 
     * @since 1.1
     */
    private void rotateCurrentTeam() {
        if (!currentTeam.getWorms().isEmpty()) {
            currentTeam.getWorms().add(currentTeam.getWorms().remove(0));
        }
        int currentIndex = teams.indexOf(currentTeam);
        currentTeam = teams.get((currentIndex + 1) % teams.size());
    }

    /**
     * Méthode de mise à jour du worm actuel en fonction de l'équipe actuelle.
     * 
     * @see Team#getWorms()
     * 
     * @since 1.1
     */
    private void updateCurrentWorm() {
        currentWorm = currentTeam.getWorms().get(0);
    }

    /**
     * Méthode de gestion des événements (vent, caisses).
     */
    private void handleTurnEvents() {
        if (Math.random() * 10 > 7.5) {
            newTurnWind();
            crateManager.onTurnEnd(map, this);
        }
    }

    /**
     * Méthode pour obtenir le ver à une position donnée.
     * 
     * @param px - La position x
     * @param py - La position x
     * 
     * @see Team#getWorms()
     * @see Worm#getX()
     * @see Worm#getY()
     * @see GameModel#isWormAtPosition(Worm, double, double)
     * 
     * @return Le Worm à la position (px, py), ou null s'il n'y en a pas
     * 
     * @since 2.0
     */
    private Worm getWormAt(double px, double py) {
        for (Team t : teams) {
            for (Worm w : t.getWorms()) {
                if (isWormAtPosition(w, px, py)) {
                    return w;
                }
            }
        }
        return null;
    }

    /**
     * Méthode pour vérifier si un ver est à une position donnée.
     * 
     * @param w  - Le ver à tester
     * @param px - La position x
     * @param py - La position y
     * 
     * @return true si le ver est à la position (px, py), false sinon
     * 
     * @since 2.0
     */
    private boolean isWormAtPosition(Worm w, double px, double py) {
        double wx = w.getX();
        double wy = w.getY();
        return px >= wx && px <= wx + 1 && py >= wy && py <= wy + 1;
    }

    /**
     * Méthode de gestion de la collision du projectile avec un worm
     * 
     * @param p  - Le projectile a analyser
     * @param it - L'itérateur des projectiles
     * 
     * @see GameModel#getWormAt(double, double)
     * @see Projectile#getMapX()
     * @see Projectile#getMapY()
     * @see Projectile#getShooter()
     * @see Worm#getTeam()
     * @see Worm#getHp()
     * @see Worm#setHp(int)
     * @see Map#createExplosion(int, int, int)
     * @see GameModel#applyAreaDamage(Projectile, Worm)
     * @see Projectile#getDamage()
     * @see Projectile#getExplosionRadius()
     * @see Iterator#remove()
     * 
     * @return true, si une collision a eu lieu et le projectile a été détruit,
     *         false sinon
     * 
     * @since 2.0
     */
    private boolean handleWormCollision(Projectile p, Iterator<Projectile> it) {
        Worm hitWorm = getWormAt(p.getMapX(), p.getMapY());

        if (hitWorm == null)
            return false;
        if (hitWorm == p.getShooter() && !p.hasLeftShooterTile())
            return false;
        if (!friendlyFire && hitWorm.getTeam() == p.getShooter().getTeam())
            return false;

        hitWorm.setHp(hitWorm.getHp() - p.getDamage());
        if (hitWorm.getHp() <= 0) {
            deleteWorm(hitWorm);
        }
        map.createExplosion(p.getMapX(), p.getMapY(), p.getExplosionRadius());
        applyAreaDamage(p, hitWorm);
        it.remove();
        return true;
    }

    /**
     * Méthode de gestion de la collision du projectile avec le terrain
     * 
     * @param p  - Le projectile
     * @param it - L'itérateur des projectiles
     * 
     * @see Map#isGround(int, int)
     * @see Map#createExplosion(int, int, int)
     * @see GameModel#applyAreaDamage(Projectile, Worm)
     * @see Projectile#getMapX()
     * @see Projectile#getMapY()
     * @see Projectile#getExplosionRadius()
     * @see Projectile#shouldBeDestroyed(Map)
     * @see Iterator#remove()
     * 
     * @return true, si une collision a eu lieu et le projectile a été détruit,
     *         false sinon
     * 
     * @since 2.0
     */
    private boolean handleTerrainCollision(Projectile p, Iterator<Projectile> it) {
        if (!p.shouldBeDestroyed(map))
            return false;

        if (map.isGround(p.getMapX(), p.getMapY())) {
            map.createExplosion(p.getMapX(), p.getMapY(), p.getExplosionRadius());
            applyAreaDamage(p, null);
        }
        it.remove();
        return true;
    }

    /**
     * Méthode de traitement de la chute d'un worm
     * 
     * @param worm         - Worm à traiter
     * @param wormIterator - Itérateur des worms de l'équipe
     * 
     * @return true si le worm est toujours en vie après la chute, false s'il est
     *         tombé dans l'eau et a été supprimé
     * 
     * @since 2.0
     */
    private boolean processWormFall(Worm worm, Iterator<Worm> wormIterator) {

        double startY = worm.getY();
        double currentY = startY;

        double x = worm.getX();
        int maxIterations = map.getHeight();

        for (int i = 0; i < maxIterations; i++) {

            double nextY = currentY;

            if (nextY >= map.getHeight()) {
                worm.setHp(0);
                wormIterator.remove();
                return false;
            }

            int tileX = (int) Math.floor(x);
            int tileY = (int) Math.floor(nextY);

            if (map.isWater(tileX, tileY)) {
                worm.setHp(0);
                wormIterator.remove();
                return false;
            }

            if (map.isGround(tileX, tileY)) {
                break;
            }

            currentY = nextY;
            worm.setY(currentY);
        }

        double fallDistance = currentY - startY;

        if (fallDistance > 1.0) {
            int damage = (int) ((fallDistance - 1) * 10);
            worm.setHp(worm.getHp() - damage);

            if (worm.getHp() <= 0) {
                wormIterator.remove();
                return false;
            }
        }
        return true;
    }

    /**
     * Méthode pour vérifier si des dégâts doivent être appliqués à un ver donné
     * dans une zone d'explosion.
     * 
     * @param worm        - Le ver à vérifier
     * @param target      - Le ver ciblé
     * @param shooter     - Le ver qui tire le projectile
     * @param projectileX - La position x du projectile
     * @param projectileY - La position y du projectile
     * @param radius      - Le rayon de l'explosion
     * 
     * @return true si des dégâts doivent être appliqués, false sinon
     * 
     * @since 2.0
     */
    private boolean shouldApplyDamageToWorm(Worm worm, Worm target, Worm shooter, double projectileX,
            double projectileY, int radius) {
        if (worm == target)
            return false;
        if (!friendlyFire && shooter != null && worm.getTeam() == shooter.getTeam() && worm != shooter)
            return false;

        double distance = Math.sqrt(Math.pow(worm.getX() - projectileX, 2) + Math.pow(worm.getY() - projectileY, 2));
        return distance <= radius;
    }
}