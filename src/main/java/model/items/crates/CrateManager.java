package model.items.crates;

import model.GameModel;
import model.Map;
import model.items.Item;
import model.items.Inventory;
import model.items.guns.Guns;
import model.items.tools.Tools;
import model.players.Team;
import model.players.Worm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Classe CrateManager qui gère la génération, la mise à jour et la collecte des caisses dans le jeu.
 * 
 * @author MESNILDREY Valentin
 * 
 * @since 2.1
 * 
 * @version 2.1
 */
public class CrateManager {
    private ArrayList<Crate> activeCrates;
    private Random random;
    private int turnsUntilNextCrate;
    private int turnsPerCycle;

    /**
     * Constructeur de la classe CrateManager.
     * 
     * @param nbWormsPerTeam - Le nombre de vers par équipe
     * @param nbTeams - Le nombre d'équipes dans le jeu
     * 
     * @since 2.1
     */
    public CrateManager(int nbWormsPerTeam, int nbTeams) {
        this.activeCrates = new ArrayList<>();
        this.random = new Random();
        this.turnsPerCycle = nbWormsPerTeam * nbTeams;
        this.turnsUntilNextCrate = turnsPerCycle;
    }

    /**
     * Méthode de gestion du cycle de génération des caisses (appelée à la fin de chaque tour).
     * 
     * @param map - La carte du jeu
     * @param model - Le modèle de jeu
     * 
     * @since 2.1
     */
    public void onTurnEnd(Map map, GameModel model) {
        turnsUntilNextCrate--;

        if (turnsUntilNextCrate <= 0) {
            spawnCrate(map, model);
            turnsUntilNextCrate = turnsPerCycle;
        }
    }

    /**
     * Méthode de générer d'une nouvelle caisse à une position aléatoire sur la carte.
     * 
     * @param map - La carte du jeu
     * @param model - Le modèle de jeu
     * 
     * @since 2.1
     */
    private void spawnCrate(Map map, GameModel model) {
        int width = map.getWidth();
        int spawnX = 5 + random.nextInt(width - 1);

        CrateContentType type = CrateContentType.getRandomType(random);
        ArrayList<Item> contents = type.createContents(model);

        Crate crate = new Crate(spawnX, contents);
        activeCrates.add(crate);
    }

    /**
     * Méthode de  mise à jour de l'état des caisses, de gestion de leur chute et leur collecte.
     * 
     * @param dt - Le delta time depuis la dernière mise à jour
     * @param map - La carte du jeu
     * @param game - Le modèle de jeu
     * 
     * @since 2.1
     */
    public void updateCrates(double dt, Map map, GameModel game) {
        Iterator<Crate> it = activeCrates.iterator();

        while (it.hasNext()) {
            Crate crate = it.next();

            if (crate.isCollected()) {
                it.remove();
                continue;
            }

            crate.update(dt);

            int mapX = crate.getMapX();
            int mapY = crate.getMapY();

            if (mapX < 0 || mapX >= map.getWidth() || mapY >= map.getHeight()) {
                it.remove();
                continue;
            }

            if (mapY >= 0 && map.isWater(mapX, mapY)) {
                it.remove();
                continue;
            }

            if (mapY >= 0 && !crate.isOnGround() && isGroundBelow(map, mapX, mapY)) {
                crate.setOnGround(true);
                crate.setY(mapY);
            }

            Worm worm = findWormNearCrate(game, mapX, mapY);
            if (worm != null) {
                collectCrate(crate, worm.getTeam());
                it.remove();
            }
        }
    }

    /**
     * Méthode pour vérifier s'il y a un sol sous la caisse.
     * 
     * @param map - La carte du jeu
     * @param x - La position X de la caisse
     * @param y - La position Y de la caisse
     * 
     * @return true s'il y a un sol sous la caisse, false sinon
     * 
     * @since 2.1
     */
    private boolean isGroundBelow(Map map, int x, int y) {
        if (y + 1 >= map.getHeight()) {
            return true;
        }
        
        return map.isGround(x, y+1);
    }

    /**
     * Méthode pour collecter une caisse et ajouter son contenu à l'inventaire de l'équipe.
     * 
     * @param crate - L'objet Crate à collecter
     * @param team - L'équipe qui collecte la caisse
     * 
     * @since 2.1
     */
    private void collectCrate(Crate crate, Team team) {
        Inventory inventory = team.getInventory();
        
        for (Item item : crate.getContents()) {
            if (item instanceof Guns gun) {
                Guns existingGun = inventory.getGun(gun.getClass());
                if (existingGun != null) {
                    existingGun.setAmmo(existingGun.getAmmo() + gun.getAmmo());
                } else {
                    inventory.addItem(gun);
                }
            } else if (item instanceof Tools tool) {
                Tools existingTool = inventory.getTool(tool.getClass());
                if (existingTool != null) {
                    existingTool.setAmmo(existingTool.getAmmo() + tool.getAmmo());
                } else {
                    inventory.addItem(tool);
                }
            }
        }
    }

    /**
     * Méthode pour trouver un ver proche de la caisse.
     * 
     * @param game - Le modèle de jeu
     * @param crateX - La position X de la caisse
     * @param crateY - La position Y de la caisse
     * 
     * @return Le ver proche de la caisse, ou null s'il n'y en a pas
     * 
     * @since 2.1
     */
    private Worm findWormNearCrate(GameModel game, int crateX, int crateY) {
        for (Team team : game.getTeams()) {
            for (Worm worm : team.getWorms()) {
                int wx = (int) Math.round(worm.getX());
                int wy = (int) Math.round(worm.getY());
                
                int dx = Math.abs(wx - crateX);
                int dy = Math.abs(wy - crateY);
                
                if (dx <= 1 && dy <= 1) {
                    return worm;
                }
            }
        }
        return null;
    }

    /**
     * Méthode pour obtenir la liste des caisses actives.
     * 
     * @return La liste des caisses actives
     * 
     * @since 2.1
     */
    public ArrayList<Crate> getActiveCrates() {
        return activeCrates;
    }

    /**
     * Méthode pour réinitialiser le gestionnaire de caisses.
     * 
     * @since 2.1
     */
    public void reset() {
        activeCrates.clear();
        turnsUntilNextCrate = turnsPerCycle;
    }
}