package model.items.tools;

import java.util.ArrayList;
import java.util.List;

import model.GameModel;
import model.Map;
import model.players.Worm;

/**
 * Classe RandomTp qui représente un outil de téléportation aléatoire.
 * 
 * @author SAMBA Seth-Ederik
 * 
 * @see Tools#getClass()
 * @see Worm#getClass()
 * @see GameModel#getClass()
 * 
 * @since 2.1
 * 
 * @version 2.1
 */
public class RandomTp extends Tools {

    GameModel model;

    /**
     * Constructeur pour créer un nouvel outil de téléportation aléatoire.
     *
     * @param model - Le modèle du jeu
     * 
     * @since 1.0
     */
    public RandomTp(GameModel model) {
        super("RandomTP", 1, model.getCurrentTeam());
        this.model = model;
    }

    /**
     * Méthode pour utiliser l'outil de téléportation aléatoire.
     * 
     * @param user - Le worm qui utilise l'outil
     * 
     * @since 1.0
     */
    @Override
    public void useTool(Worm user) {
        if (user == null || model == null) {
            System.out.println("model est null");
            return;
        }

        Map map = model.getMap();
        if (map == null) {
            System.out.println("map nulle");
            return;
        }

        if (ammo <= 0) {
            System.out.println("plus de munitions");
            return;
        }

        int width = map.getWidth();
        int height = map.getHeight();

        List<int[]> freeList = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (y + 1 < height && map.isEmpty(x, y) && map.isGround(x, y + 1)) {
                    freeList.add(new int[] { x, y });
                }
            }
        }

        if (freeList.isEmpty()) {
            System.out.println("RandomTp: aucune position valide trouvée");
            return;
        }

        int[] pos = freeList.get((int) (Math.random() * freeList.size()));
        System.out.println("RandomTp pos=(" + pos[0] + "," + pos[1] + ")");

        int oldX = (int) user.getX();
        int oldY = (int) user.getY();
        if (oldX >= 0 && oldX < width && oldY >= 0 && oldY < height) {
            map.setCell(oldX, oldY, ' ');
        }

        user.setPosition(pos[0], pos[1]);
        map.setCell(pos[0], pos[1], user.getSymbol());
        ammo--;
    }
}
