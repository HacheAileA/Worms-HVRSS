package view.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import model.GameModel;
import model.players.Team;
import model.players.Worm;

/**
 * Classe WormRendererqui permet d'afficher les worms sur la map en version graphique.
 * 
 * @author MESNILDREY Valentin
 * 
 * @see Worm#getClass()
 * @see Team#getClass()
 * @see GuiView#getClass()
 * @see Graphics2D#getClass()
 * @see BufferedImage#getClass()
 * @see ImageIO#getClass()
 * @see IOException#getClass()
 * 
 * @since 2.0
 * 
 * @version 2.0
 */
public class WormRenderer {
    
    private static final int HUD_HEIGHT = 12;
    private static final int HUD_Y_OFFSET = 20;
    private static final int GLOW_EXTRA_SIZE = 10;
    private static final float HUD_FONT_SIZE = 10f;
    
    /**
     * Stockage de l'image du worm
     */
    private final Map<String, BufferedImage> wormSprites = new HashMap<>();

    /**
     * Constructeur permettant de charger le chemin vers le sprite du worm
     * 
     * @since 2.0
     */
    public WormRenderer() {
        for (int teamId = 0; teamId <= 3; teamId++) {
            wormSprites.put(teamId + "_east", load("/assets/worms/worm_east" + (teamId + 1) + ".png"));
            wormSprites.put(teamId + "_west", load("/assets/worms/worm_west" + (teamId + 1) + ".png"));
        }
    }

    /**
     * La méthode render permet d'afficher les worms
     * 
     * @param g - un graphics2D pour afficher les worms
     * @param model - un GameModel pour savoir ce qu'on doit afficher
     * 
     * @see #load(String)
     * @see #drawHUD(Graphics2D, Worm, int, int)
     * 
     * @since 2.0
     */
    public void render(Graphics2D g, GameModel model) {
        for (Team team : model.getTeams()) {
            int teamId = team.getTeamId();
            for (Worm worm : team.getWorms()) {
                int x = (int) Math.floor(worm.getX() * MapRenderer.TILE_SIZE);
                int y = (int) Math.floor(worm.getY() * MapRenderer.TILE_SIZE);

                if (worm == model.getCurrentWorm()) {
                    drawGlow(g, x, y);
                }

                String direction = worm.isFacingWest() ? "west" : "east";
                g.drawImage(wormSprites.get(teamId + "_" + direction), x, y, MapRenderer.TILE_SIZE, MapRenderer.TILE_SIZE, null);

                drawHUD(g, worm, x, y);
            }
        }
    }

    // -------------------- PRIVATE METHODS --------------------

    /**
     * La méthode load permet de charger le sprite des worms (sert pour la méthode render())
     * 
     * @param path - le chemin où sont les sprites
     * 
     * @see #render()
     * 
     * @return L'image du worms
     * 
     * @since 2.0
     */
    private BufferedImage load(String path) {
        try {
            return ImageIO.read(getClass().getResource(path));
        } catch (IOException e) {
            throw new RuntimeException("Impossible de charger l'image " + path, e);
        }
    }

    /**
     * Méthode qui permet d'afficher l'HUD des worms (points de vie et nom) sert pour la méthode render()
     * 
     * @param g - le Graphics2D pour afficher l'HUD
     * @param worm - Le worms qui doit "recevoir" l'HUD
     * @param x - La position X du worm
     * @param y - La position Y du worm
     * 
     * @see #render()
     * 
     * @since 2.0
     */
    private void drawHUD(Graphics2D g, Worm worm, int x, int y) {
        int hudWidth = MapRenderer.getTileSize();
        int hudHeight = HUD_HEIGHT;

        int left = x;
        int top = y - HUD_Y_OFFSET;

        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(left, top, hudWidth, hudHeight, 5, 5);

        double hpPercent = worm.getHp() / (double) worm.getHpInit();

        g.setColor(Color.RED);
        g.fillRect(left, top, hudWidth, hudHeight);

        if (hpPercent > 0.5) {
            g.setColor(Color.GREEN);
        } else if (hpPercent > 0.25) {
            g.setColor(Color.ORANGE);
        } else {
            g.setColor(Color.RED);
        }
        
        g.fillRect(left, top, (int) (hudWidth * hpPercent), hudHeight);

        g.setColor(Color.BLACK);
        g.setFont(g.getFont().deriveFont(HUD_FONT_SIZE));
        g.drawString(worm.getName(), left, top - 2);
    }

    /**
     * Méthode qui permet d'afficher un effet de brillance (glow) autour du worm courant
     * @param g - Le Graphics2D pour le glow
     * @param x - La position x du worm
     * @param y - La position y du worm
     */
    private void drawGlow(Graphics2D g, int x, int y) {
        int size = MapRenderer.TILE_SIZE + GLOW_EXTRA_SIZE;
        long time = System.currentTimeMillis();
        int alpha = (int) (120 + 40 * Math.sin(time * 0.005));

        g.setColor(new Color(255, 255, 0, alpha));
        g.fillOval(x - 5, y - 5, size, size);
    }
}
