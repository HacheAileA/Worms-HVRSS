package view.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import controller.gui.InputController;
import model.GameModel;
import model.physics.Projectile;
import model.players.Worm;

/**
 * Classe GamePanel représentant le panneau de jeu.
 * 
 * @author MESNILDREY Valentin
 * @author NESI Romain
 * @author ARNAUD Hugo
 * 
 * @see JPanel#getClass()
 * @see Graphics#getClass()
 * @see Graphics2D#getClass()
 * @see Dimension#getClass()
 * @see GameModel#getClass()
 * 
 * @see MapRenderer#getClass()
 * @see WormRenderer#getClass()
 * 
 * @since 2.0
 * 
 * @version 2.0
 */
public class GamePanel extends JPanel {

    /** Permet de gérer le déplacement de la caméra en X */
    private int cameraOffsetX = 0;
    /** Permet de gérer le déplacement de la caméra en Y */
    private int cameraOffsetY = 0;

    /** Le marge de suivi */
    private final int followMargin = 50;
    /** La vitesse de suivi */
    private final int followSpeed = 20;

    /** Le modèle de jeu */
    private final GameModel model;
    /** Le "dessin" de la map */
    private final MapRenderer mapRenderer = new MapRenderer();
    /** Le "dessin" des vers */
    private final WormRenderer wormRenderer = new WormRenderer();
    /** Le contrôleur d'entrée */
    private final InputController inputController;
    /** Le timer */
    private Timer timer;
    /** Les particules */
    private ArrayList<Particle> particles;

    /** Le renderer des caisses */
    private CrateRenderer crateRenderer;

    /** Le renderer du mode développeur */
    private DevModRenderer devModRenderer = new DevModRenderer();

    /**
     * Constructeur de la classe GamePanel.
     * 
     * @param model - Le modèle de jeu à afficher
     * @param view - La vue pour pouvoir update correctement le jeu lors des déplacements
     * 
     * @see JPanel#setPreferredSize(Dimension)
     * @see JPanel#setDoubleBuffered(boolean)
     * @see Dimension#Dimension(int, int)
     * @see GameModel#getMap()
     * 
     * @since 2.0
     */
    public GamePanel(GameModel model, GuiView view) {
        this.model = model;
        this.inputController = new InputController(model, this, view);
        this.crateRenderer = new CrateRenderer();
        addKeyListener(inputController);
        addMouseListener(inputController);
        addMouseMotionListener(inputController);
        addMouseWheelListener(inputController);
        setFocusable(true);
        requestFocusInWindow();
        SwingUtilities.invokeLater(() -> {
            this.requestFocusInWindow();
        });

        int width = model.getMap().getWidth() * MapRenderer.TILE_SIZE;
        int height = model.getMap().getHeight() * MapRenderer.TILE_SIZE;

        setPreferredSize(new Dimension(width, height));
        setDoubleBuffered(true);
        timer = new Timer(1000 / 30, e -> {
            Worm worm = model.getCurrentWorm();
            if (worm != null) {
                worm.update(model.getMap(), view);
            }

            model.getCrateManager().updateCrates(0.1, model.getMap(), model);

            // Obtenir la position de la souris dans le viewport visible
            Point mouseOnScreen = view.getMousePosition();
            if (mouseOnScreen != null) {
                int dx = 0, dy = 0;

                if (mouseOnScreen.x < followMargin)
                    dx = followSpeed;
                else if (mouseOnScreen.x > getWidth() - followMargin)
                    dx = -followSpeed;

                if (mouseOnScreen.y < followMargin)
                    dy = followSpeed;
                else if (mouseOnScreen.y > getHeight() - followMargin)
                    dy = -followSpeed;

                if (dx != 0 || dy != 0)
                    moveCamera(dx, dy);
            }

            repaint();
        });
        timer.start();
    }

    /**
     * Méthode qui retourne le contrôleur d'entrée associé à ce panneau de jeu.
     * 
     * @return L'InputController associé
     * 
     * @since 2.0
     */
    public InputController getInputController() {
        return inputController;
    }

    /**
     * Méthode qui permet de récupérer l'offset X de la caméra
     * 
     * @return L'offset X de la caméra
     * 
     * @since 2.0
     */
    public int getCameraOffsetX() {
        return cameraOffsetX;
    }

    /**
     * Méthode qui permet de récupérer l'offset Y de la caméra
     * 
     * @return L'offset Y de la caméra
     * 
     * @since 2.0
     */
    public int getCameraOffsetY() {
        return cameraOffsetY;
    }

    /**
     * Méthode pour arrêter le timer de mouvement
     * 
     * @since 2.0
     */
    public void stopMovementTimer() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }

    /**
     * Méthode pour démarrer le timer de mouvement
     * 
     * @since 2.0
     */
    public void startMovementTimer() {
        if (timer != null && !timer.isRunning()) {
            timer.start();
        }
    }

    /**
     * Setter pour définir la liste des particules à afficher
     * 
     * @param particles - La liste des particules
     * 
     * @since 2.1
     */
    public void setParticlesList(ArrayList<Particle> particles) {
        this.particles = particles;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getToolkit().getScreenSize());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(cameraOffsetX, cameraOffsetY);
        mapRenderer.render(g2d, model.getMap());
        wormRenderer.render(g2d, model);

        if (crateRenderer != null) {
            crateRenderer.render(g2d, model.getCrateManager(), MapRenderer.TILE_SIZE);
        }

        for (Projectile projectile : model.getProjectiles()) {
            int px = (int) (projectile.getX() * MapRenderer.getTileSize());
            int py = (int) (projectile.getY() * MapRenderer.getTileSize());
            int size = Math.max(2, MapRenderer.getTileSize() / 3);
            g2d.setColor(Color.BLACK);
            g2d.fillOval(px - size / 2, py - size / 2, size, size);
        }

        if (particles != null)
            for (Particle p : particles) {
                g2d.setColor(p.color);
                g2d.fillOval((int) p.x, (int) p.y, 4, 4);
            }

        if (model.isDevMode()) {
            devModRenderer.render(g2d, model);
        }
    }

    /**
     * Permet de zoomer en avant
     * 
     * @param mouse - Position de la souris pour centrer le zoom
     * 
     * @since 2.1
     */
    public void zoomIn(Point mouse) {
        int oldTileSize = MapRenderer.getTileSize();
        MapRenderer.setTileSize(oldTileSize + 2);

        // Ajuster la caméra pour centrer sur le curseur
        cameraOffsetX -= (mouse.x * (MapRenderer.getTileSize() - oldTileSize)) / MapRenderer.getTileSize();
        cameraOffsetY -= (mouse.y * (MapRenderer.getTileSize() - oldTileSize)) / MapRenderer.getTileSize();

        limitCamera();
        revalidate();
        repaint();
    }

    /**
     * Permet de zoomer en arrière avec des limites pour ne pas dépasser la taille de la fenêtre
     * 
     * @param mouse - Position de la souris pour centrer le zoom
     * 
     * @since 2.1
     */
    public void zoomOut(Point mouse) {
        int oldTileSize = MapRenderer.getTileSize();

        // Taille minimale = la map occupe exactement sa taille réelle
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int mapWidth = model.getMap().getWidth();
        int mapHeight = model.getMap().getHeight();
        int minTileSizeW = panelWidth / mapWidth;
        int minTileSizeH = panelHeight / mapHeight;
        int minTileSize = Math.max(27, Math.min(minTileSizeW, minTileSizeH));

        int newSize = Math.max(MapRenderer.getTileSize() - 2, minTileSize);
        MapRenderer.setTileSize(newSize);

        // Ajuster la caméra pour centrer sur le curseur
        cameraOffsetX -= (mouse.x * (newSize - oldTileSize)) / oldTileSize;
        cameraOffsetY -= (mouse.y * (newSize - oldTileSize)) / oldTileSize;

        limitCamera();
        revalidate();
        repaint();
    }

    /**
     * Limite les offsets de la caméra pour ne pas dépasser les bords de la map
     * 
     * @since 2.1
     */
    private void limitCamera() {
        int mapWidth = model.getMap().getWidth() * MapRenderer.getTileSize();
        int mapHeight = model.getMap().getHeight() * MapRenderer.getTileSize();

        // Limites selon la taille du panel visible
        int maxOffsetX = 0;
        int minOffsetX = getWidth() - mapWidth;
        int maxOffsetY = 0;
        int minOffsetY = getHeight() - mapHeight;

        // Si la map est plus petite que le panel, centrer
        if (mapWidth < getWidth())
            minOffsetX = maxOffsetX = (getWidth() - mapWidth) / 2;
        if (mapHeight < getHeight())
            minOffsetY = maxOffsetY = (getHeight() - mapHeight) / 2;

        cameraOffsetX = Math.max(minOffsetX, Math.min(maxOffsetX, cameraOffsetX));
        cameraOffsetY = Math.max(minOffsetY, Math.min(maxOffsetY, cameraOffsetY));
    }

    /**
     * Permet de déplacer la caméra
     * 
     * @param dx - Déplacement en X
     * @param dy - Déplacement en Y
     * 
     * @since 2.1
     */
    public void moveCamera(int dx, int dy) {
        cameraOffsetX += dx;
        cameraOffsetY += dy;
        limitCamera();
        repaint();
    }

    /**
     * Centre la caméra sur le point (worldX, worldY) en pixels
     * 
     * @param worldX - Position X dans le monde (pixels)
     * @param worldY - Position Y dans le monde (pixels)
     * 
     * @since 2.1
     */
    public void centerCameraOn(int worldX, int worldY) {
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Calculer l'offset pour que le point soit centré
        cameraOffsetX = panelWidth / 2 - worldX;
        cameraOffsetY = panelHeight / 2 - worldY;

        limitCamera();
        repaint();
    }

    /**
     * Centre la caméra sur le worm actif
     * 
     * @since 2.1
     */
    public void centerCameraOnCurrentWorm() {
        Worm worm = model.getCurrentWorm();
        if (worm == null)
            return;

        int worldX = (int) (worm.getX() * MapRenderer.TILE_SIZE);
        int worldY = (int) (worm.getY() * MapRenderer.TILE_SIZE);

        centerCameraOn(worldX, worldY);
    }

}