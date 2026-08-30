package view.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import model.GameModel;
import model.Map;
import model.players.Team;
import model.players.Worm;

/**
 * Classe permettant d'afficher un "dev mod", affichant notamment les collisions des worms et du terrain.
 * 
 * @author MESNILDREY Valentin
 * 
 * @since 2.1
 * @version 2.1
 */
public class DevModRenderer {

    /**
     * Méthode permettant de dessiner les hitbox des worms et des tuiles de la map.
     * 
     * @param g2d - Le Graphics2D sur lequel dessiner
     * @param model - Le contenant des informations de la partie
     * 
     * @since 2.1
     */
    public void render(Graphics2D g2d, GameModel model) {
        int tileSize = MapRenderer.getTileSize();
        for (Team t : model.getTeams()) {
            for (Worm w : t.getWorms()) {
                if (w == null)
                    continue;

                int hbX = (int) (w.getX() * tileSize);
                int hbY = (int) (w.getY() * tileSize);
                int hbWidth = tileSize;
                int hbHeight = tileSize;

                g2d.setColor(Color.RED);
                g2d.drawRect(hbX, hbY, hbWidth, hbHeight);
            }
        }

        Map map = model.getMap();
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                int px = x * tileSize;
                int py = y * tileSize;

                if (map.isWater(x, y)) {
                    g2d.setColor(new Color(0, 128, 255, 128));
                    g2d.drawRect(px, py, tileSize, tileSize);
                }

                if (map.isGround(x, y)) {
                    g2d.setColor(new Color(0, 255, 0, 128));
                    g2d.drawRect(px, py, tileSize, tileSize);
                }
            }
        }
    }
}
