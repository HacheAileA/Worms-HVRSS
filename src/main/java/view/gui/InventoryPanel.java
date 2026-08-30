package view.gui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import model.GameModel;
import model.items.Item;
import model.players.Team;
import model.players.Worm;

/**
 * Classe InventoryPanel permettant d'afficher l'inventaire en version graphique.
 * 
 * @author MESNILDREY Valentin
 * 
 * @see Worm#getClass()
 * @see Team#getClass()
 * @see GuiView#getClass()
 * @see JPanel#getClass()
 * @see Graphics2D#getClass()
 * @see BufferedImage#getClass()
 * 
 * @since 2.0
 * 
 * @version 2.0
 */
public class InventoryPanel extends JPanel {
    /** Le model utilisé */
    private final GameModel model;
    /** La vue utilisée */
    private final GuiView guiView;
    /** Le renderer du vent */
    private WindRenderer windRenderer;

    /**
     * Constructeur permettant d'initialiser le panel d'inventaire
     * 
     * @param model - Le GameModel utilisé
     * @param guiView - La GuiView utilisée
     * 
     * @since 2.0
     */
    public InventoryPanel(GameModel model, GuiView guiView) {
        this.model = model;
        this.guiView = guiView;
        setOpaque(false);
        init();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        if (windRenderer != null) {
            int windAreaWidth = 150;
            g2d.translate(getWidth() - windAreaWidth -40, 20);
            windRenderer.render(g2d, windAreaWidth, getHeight() - 20);
            g2d.translate(-(getWidth() - windAreaWidth -10), -10);
        }
        g2d.dispose();
    }

    /**
     * Setter pour le WindRenderer
     * 
     * @param windRenderer - le WindRenderer à définir
     * 
     * @since 2.1
     */
    public void setWindRenderer(WindRenderer windRenderer) {
        this.windRenderer = windRenderer;
    }

    /**
     * Méthode d'initialisation du panel
     * 
     * @since 2.0
     */
    private void init() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        refresh();
    }

    /**
     * Méthode qui permet de rafraîchir l'inventaire affiché
     * 
     * @since 2.0
     */
    public void refresh() {
        this.removeAll();

        Worm currentWorm = model.getCurrentWorm();
        if (currentWorm == null)
            return;

        Team team = currentWorm.getTeam();
        if (team == null)
            return;

        for (Item item : team.getInventory().getAvailableItems(team)) {
            String buttonText = item.getName();
            buttonText += " (" + item.getAmmo() + ")";

            JButton button = new JButton(buttonText);
            button.setToolTipText(item.getName());
            button.setFocusable(false);

            try {
                BufferedImage img = ImageIO.read(getClass().getResource("/assets/items/" + item.getName() + ".png"));
                Image scaledImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(scaledImg));
            } catch (IOException | IllegalArgumentException e) {
                System.out.println("[HUD] Impossible de charger l'image pour " + item.getName());
            }

            button.addActionListener(e -> {
                Worm wormNow = model.getCurrentWorm();
                if (wormNow != null) {
                    wormNow.setSelectedItem(item);
                    repaint();
                    if (guiView != null) {
                        guiView.getTrajectoryLayer().repaint();
                        guiView.getGamePanel().requestFocusInWindow();
                    }
                }
            });

            this.add(button);
        }

        this.revalidate();
        this.repaint();
    }
}
