package controller.gui;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.awt.event.MouseWheelEvent;

import model.GameModel;
import model.items.Item;
import model.players.Team;
import model.players.Worm;
import view.gui.GamePanel;
import view.gui.GuiView;
import view.gui.MapRenderer;
import view.gui.Particle;

/**
 * Classe InputController qui implémente les déplacements du vers ainsi que la gestion de la souris et du clavier.
 * 
 * @author NESI Romain
 * 
 * @since 2.0
 * 
 * @version 2.0
 */
public class InputController implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    private GameModel model;
    private GuiView view;
    private GamePanel panel;
    private int mouseX;
    private int mouseY;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    /**
     * Constructeur de la classe InputController.
     * 
     * @param model - Le modèle du jeu
     * @param panel - Le panneau de jeu
     * @param view - La vue du jeu
     * 
     * @since 2.0
     * 
     * @version 2.1
     */
    public InputController(GameModel model, GamePanel panel, GuiView view) {
        this.model = model;
        this.view = view;
        this.panel = panel;
    }

    /**
     * Méthode qui permet de récupérer la position X de la souris
     * 
     * @return La position X de la souris
     */
    public int getMouseX() {
        return mouseX;
    }

    /**
     * Méthode qui permet de récupérer la position Y de la souris
     * 
     * @return La position Y de la souris
     */
    public int getMouseY() {
        return mouseY;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Worm worm = model.getCurrentWorm();
        if (worm == null) {
            return;
        }

        Team team = worm.getTeam();
        if (team == null) {
            return;
        }
        ArrayList<Item> items = team.getInventory().getAvailableItems(team);

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                leftPressed = true;
                worm.startMoveLeft();
                this.view.soundPlayer.playRunSound();
                break;
            case KeyEvent.VK_RIGHT:
                rightPressed = true;
                worm.startMoveRight();
                this.view.soundPlayer.playRunSound();
                break;
            case KeyEvent.VK_SPACE:
                Worm w = worm;
                if (w.isOnGround(model.getMap())) {
                    w.jumpSmooth(model.getMap());
                    this.view.soundPlayer.playSoundEffect("/sounds/sounds_effects/jump.wav");

                    for (int i = 0; i < 5; i++) {
                        double angle = Math.random() * Math.PI - Math.PI / 2;
                        double speed = Math.random() * 2 + 1;
                        double dx = Math.cos(angle) * speed;
                        double dy = Math.sin(angle) * speed;
                        view.addParticles(new Particle(
                                (w.getX() + 1) * MapRenderer.TILE_SIZE,
                                (w.getY() + 1) * MapRenderer.TILE_SIZE,
                                dx, dy,
                                Color.GREEN,
                                20));
                    }
                    view.startParticleTimer();
                }
                break;
            case KeyEvent.VK_D:
                model.toggleDevMode();
                break;
            case KeyEvent.VK_ENTER:
                this.model.nextTurn();
                break;
            case KeyEvent.VK_1:
                if (items.isEmpty() || items.get(0) == null) {
                    break;
                }
                worm.setSelectedItem(items.get(0));
                this.view.getInventoryPanel().refresh();
                break;
            case KeyEvent.VK_2:
                if (items.size() < 2 || items.get(1) == null) {
                    break;
                }
                worm.setSelectedItem(items.get(1));
                this.view.getInventoryPanel().refresh();
                break;
            case KeyEvent.VK_3:
                if (items.size() < 3 || items.get(2) == null) {
                    break;
                }
                worm.setSelectedItem(items.get(2));
                this.view.getInventoryPanel().refresh();
                break;
            case KeyEvent.VK_4:
                if (items.size() < 4 || items.get(3) == null) {
                    break;
                }
                worm.setSelectedItem(items.get(3));
                this.view.getInventoryPanel().refresh();
                break;
            case KeyEvent.VK_5:
                if (items.size() < 5 || items.get(4) == null) {
                    break;
                }
                worm.setSelectedItem(items.get(4));
                this.view.getInventoryPanel().refresh();
                break;
            case KeyEvent.VK_6:
                if (items.size() < 6 || items.get(5) == null) {
                    break;
                }
                worm.setSelectedItem(items.get(5));
                this.view.getInventoryPanel().refresh();
                break;
            case KeyEvent.VK_7:
                if (items.size() < 7 || items.get(6) == null) {
                    break;
                }
                worm.setSelectedItem(items.get(6));
                this.view.getInventoryPanel().refresh();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Worm worm = model.getCurrentWorm();
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                leftPressed = false;
                if (!rightPressed) {
                    worm.stopMove();
                    this.view.soundPlayer.stopRunSound();
                } else
                    worm.startMoveRight();
                break;
            case KeyEvent.VK_RIGHT:
                rightPressed = false;
                if (!leftPressed) {
                    worm.stopMove();
                    this.view.soundPlayer.stopRunSound();
                } else
                    worm.startMoveLeft();
                break;
        }
    }

    /**
     * Méthode pour l'implémentation de l'interface KeyListener.
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Méthode de récupération des coordonnées de la souris, lors d'un clic.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    /**
     * Méthode de récupération des coordonnées de la souris, lors du relâchement du clic.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    /**
     * Méthode de récupération des coordonnées de la souris, lors du déplacement de celle-ci.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    /**
     * Méthode de récupération des coordonnées de la souris, lors du maintient-déplacement.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    /**
     * Méthode pour l'implémentation de l'interface MouseListener.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Méthode pour l'implémentation de l'interface MouseListener.
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Méthode pour l'implémentation de l'interface MouseListener.
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Méthode de gestion du zoom, avec les mouvement de la molette.
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            panel.zoomIn(e.getPoint());
        } else {
            panel.zoomOut(e.getPoint());        
        }
    }
}
