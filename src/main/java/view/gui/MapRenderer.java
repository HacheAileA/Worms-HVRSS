package view.gui;

import model.Map;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Classe permettant de dessiner la map.
 * 
 * @author MESNILDREY Valentin
 * @author SANE Souleymane
 * 
 * @see Graphics2D#getClass()
 * @see BufferedImage#getClass()
 * @see Map#getClass()
 * 
 * @since 2.0
 * 
 * @version 2.0
 */
public class MapRenderer {

    /** La taille par défaut des tuiles */
    public static final int DEFAULT_TILE_SIZE = 32;

    /** La taille des tuiles */
    public static int TILE_SIZE = 32;

    private final BufferedImage dirt;
    private final BufferedImage grass;
    private final BufferedImage water;
    private final BufferedImage waterNothingAbove;
    private final BufferedImage rock;
    private final BufferedImage plant;
    private final BufferedImage signLeft;
    private final BufferedImage signRight;
    private final BufferedImage signExit;
    private final BufferedImage map_cave_bg;
    private final BufferedImage map_islandAndBridge_bg;
    private final BufferedImage stone;
    private final BufferedImage stoneTop;
    private final BufferedImage rockDirt;
    private final BufferedImage rockDirtTop;
    private final BufferedImage box;
    private final BufferedImage boxCrossed;
    private final BufferedImage weight;
    private final BufferedImage switchLeft;
    private final BufferedImage switchRight;
    private final BufferedImage chain;
    private final BufferedImage rope;
    private final BufferedImage nothing;

    /**
     * Constructeur permettant de charger les images des tiles
     * 
     * @since 2.0
     */
    public MapRenderer() {
        dirt = load("/assets/tiles/island/dirt.png");
        grass = load("/assets/tiles/island/grass.png");
        water = load("/assets/tiles/water.png");
        waterNothingAbove = load("/assets/tiles/waterTop.png");
        rock = load("/assets/tiles/decorations/rock.png");
        plant = load("/assets/tiles/decorations/plant.png");
        signLeft = load("/assets/tiles/decorations/signLeft.png");
        signRight = load("/assets/tiles/decorations/signRight.png");
        signExit = load("/assets/tiles/decorations/signExit.png");
        map_cave_bg = load("/assets/map_cave_bg.png");
        map_islandAndBridge_bg = load("/assets/map_sky_bg.jpg");
        stone = load("/assets/tiles/bridge/stone.png");
        stoneTop = load("/assets/tiles/bridge/stoneTop.png");
        rockDirt = load("/assets/tiles/cave/rockDirt.png");
        rockDirtTop = load("/assets/tiles/cave/rockDirtTop.png");
        box = load("/assets/tiles/decorations/box.png");
        boxCrossed = load("/assets/tiles/decorations/boxAlt.png");
        weight = load("/assets/tiles/decorations/weight.png");
        switchLeft = load("/assets/tiles/decorations/switchLeft.png");
        switchRight = load("/assets/tiles/decorations/switchRight.png");
        chain = load("/assets/tiles/decorations/chain.png");
        rope = load("/assets/tiles/decorations/rope.png");
        nothing = load("/assets/tiles/nothing.png");

    }

    /**
     * Méthode qui retourne la taille des tuiles
     * 
     * @return La taille des tuiles
     * 
     * @since 2.1
     */
    public static int getTileSize() {
        return TILE_SIZE;
    }

    /**
     * Méthode qui réinitialise le zoom à la taille par défaut
     * 
     * @since 2.1
     */
    public static void resetZoom() {
        TILE_SIZE = DEFAULT_TILE_SIZE;
    }

    /**
     * Méthode qui définit la taille des tuiles avec des limites pour le zoom
     * 
     * @param tileSize - La nouvelle taille des tuiles
     * 
     * @since 2.1
     */
    public static void setTileSize(int tileSize) {
        TILE_SIZE = Math.max(8, Math.min(tileSize, 128));
    }

    /**
     * Adapte la taille des tuiles pour que la carte rentre dans les dimensions données.
     * 
     * @param width     Largeur disponible sur l'écran
     * @param height    Hauteur disponible sur l'écran
     * @param mapWidth  Largeur de la carte
     * @param mapHeight Hauteur de la carte
     * 
     * @since 2.1
     */
    public static void fitToDimensions(int width, int height, int mapWidth, int mapHeight) {
        int optimalSize = Math.min(width / mapWidth, height / mapHeight);
        setTileSize(optimalSize);
    }

    /**
     * Méthode de chargement d'une image
     * 
     * @param path - Le chemin de l'image à charger
     * 
     * @return L'image chargée
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
     * La méthode render permet d'afficher la map
     * 
     * @param g - un graphics2D pour afficher la map
     * @param map - une Map à afficher
     * 
     * @since 2.0
     */
    public void render(Graphics2D g, Map map) {
        if (map.getMapType().equals("islands") || map.getMapType().equals("bridge")) {
            g.drawImage(
                    map_islandAndBridge_bg,
                    0,
                    0,
                    map.getWidth() * TILE_SIZE,
                    map.getHeight() * TILE_SIZE,
                    null);
        } else if (map.getMapType().equals("cave")) {
            g.drawImage(
                    map_cave_bg,
                    0,
                    0,
                    map.getWidth() * TILE_SIZE,
                    map.getHeight() * TILE_SIZE,
                    null);
        }
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                char cell = map.getCell(x, y);
                BufferedImage img = switch (cell) {
                    case '#' -> dirt;
                    case '~' -> water;
                    case 'W' -> waterNothingAbove;
                    case 'G' -> grass;
                    case 'R' -> rock;
                    case 'P' -> plant;
                    case 'I' -> signRight;
                    case 'L' -> signLeft;
                    case 'E' -> signExit;
                    case 's' -> stone;
                    case 'S' -> stoneTop;
                    case 't' -> rockDirt;
                    case 'T' -> rockDirtTop;
                    case 'b' -> box;
                    case 'B' -> boxCrossed;
                    case 'w' -> weight;
                    case 'h' -> switchLeft;
                    case 'H' -> switchRight;
                    case 'c' -> chain;
                    case 'r' -> rope;
                    default -> nothing;
                };
                g.drawImage(img, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
            }
        }
    }
}
