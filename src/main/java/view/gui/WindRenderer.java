package view.gui;

import java.awt.*;
import model.physics.Wind;

/**
 * Classe WindRenderer permet de dessiner les informations sur le vent.
 * 
 * @author MESNILDREY Valentin
 * 
 * @see Wind#getClass()
 * @see Graphics2D#getClass()
 * 
 * @since 2.1
 * 
 * @version 2.1
 */
public class WindRenderer {

    private Wind wind;

    private static final int WIDTH = 200;
    private static final int HEIGHT = 60;
    private static final int PADDING = 10;
    private static final int ARROW_LENGTH = 40;

    /**
     * Constructeur de WindRenderer
     * 
     * @param wind le Wind à utiliser
     * 
     * @since 2.1
     */
    public WindRenderer(Wind wind) {
        this.wind = wind;
    }

    /**
     * Setter pour le vent
     * 
     * @param wind le Wind à définir
     * 
     * @since 2.1
     */
    public void setWind(Wind wind) {
        this.wind = wind;
    }

    /**
     * Méthode pour dessiner le panneau d'affichage du vent.
     * 
     * @param g2d - le Graphics2D sur lequel dessiner
     * @param panelWidth - la largeur du panneau
     * @param panelHeight - la hauteur du panneau
     * 
     * @since 2.1
     */
    public void render(Graphics2D g2d, int panelWidth, int panelHeight) {
        int x = PADDING;
        int y = panelHeight - HEIGHT - PADDING;

        Graphics2D g = (Graphics2D) g2d.create();
        g.translate(x, y);

        if (wind == null || !wind.enabled) {
            drawDisabledWind(g);
        } else {
            drawWind(g);
        }
        g.dispose();
    }

    /**
     * Méthode pour dessiner l'affichage lorsque le vent est désactivé.
     * 
     * @param g - Le Graphics2D sur lequel dessiner
     * 
     * @since 2.1
     */
    private void drawDisabledWind(Graphics2D g) {
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Arial", Font.BOLD, 14));

        String text = "Wind : OFF";
        FontMetrics fm = g.getFontMetrics();
        int x = (WIDTH - fm.stringWidth(text)) / 2;
        int y = HEIGHT / 2 + fm.getAscent() / 2;

        g.drawString(text, x, y);
    }

    /**
     * Méthode pour dessiner les informations sur le vent.
     * 
     * @param g - Le Graphics2D sur lequel dessiner
     * 
     * @since 2.1
     */
    private void drawWind(Graphics2D g) {
        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2 - 5;

        int direction = wind.getDirection();
        double strength = wind.getStrength();

        drawArrow(g, centerX, centerY, direction, strength);
        drawText(g, centerX, centerY + 18, direction, strength);
    }

    /**
     * Méthode pour dessiner une flèche représentant le vent.
     * 
     * @param g - Le Graphics2D sur lequel dessiner
     * @param cx - Coordonnée x du centre de la flèche
     * @param cy - Coordonnée y du centre de la flèche
     * @param dir - Direction du vent (1 pour droite, -1 pour gauche)
     * @param strength - Force du vent
     * 
     * @since 2.1
     */
    private void drawArrow(Graphics2D g, int cx, int cy, int dir, double strength) {
        double normalized = Math.min(strength / 20.0, 1.0);
        int length = (int) (ARROW_LENGTH * (0.5 + normalized));

        g.setColor(getWindColor(strength));
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        int startX = cx - (length / 2) * dir;
        int endX = cx + (length / 2) * dir;

        g.drawLine(startX, cy, endX, cy);

        drawArrowHead(g, endX, cy, dir);
        drawWindLines(g, startX, cy, dir, normalized);
    }

    /**
     * Dessine la tête de la flèche
     * 
     * @param g - Le Graphics2D sur lequel dessiner
     * @param x - Coordonnée x de la pointe de la flèche
     * @param y - Coordonnée y de la pointe de la flèche
     * @param dir - Direction de la flèche (1 pour droite, -1 pour gauche)
     * 
     * @since 2.1
     */
    private void drawArrowHead(Graphics2D g, int x, int y, int dir) {
        int size = 10;

        Polygon head = new Polygon();
        head.addPoint(x, y);
        head.addPoint(x - dir * size, y - size / 2);
        head.addPoint(x - dir * size, y + size / 2);

        g.fillPolygon(head);
    }

    /**
     * Dessine des lignes de vent pour indiquer la force
     * 
     * @param g - Le Graphics2D sur lequel dessiner
     * @param startX - Coordonnée x du point de départ des lignes
     * @param y - Coordonnée y des lignes
     * @param dir - Direction des lignes (1 pour droite, -1 pour gauche)
     * @param strength - Force du vent
     * 
     * @since 2.1
     */
    private void drawWindLines(Graphics2D g, int startX, int y, int dir, double strength) {
        int lines = (int) (strength * 3) + 1;

        for (int i = 0; i < lines; i++) {
            int offset = (i + 1) * 8;
            int x1 = startX - offset * dir;
            int x2 = x1 + 5 * dir;
            g.drawLine(x1, y, x2, y);
        }
    }

    /**
     * Dessine le texte indiquant la direction et la force du vent
     * 
     * @param g - Le Graphics2D sur lequel dessiner
     * @param x - Coordonnée x du texte
     * @param y - Coordonnée y du texte
     * @param dir - Direction du vent (1 pour droite, -1 pour gauche)
     * @param strength - Force du vent
     * 
     * @since 2.1
     */
    private void drawText(Graphics2D g, int x, int y, int dir, double strength) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));

        String direction = dir > 0 ? "VENT : DROITE" : "VENT : GAUCHE";
        String text = String.format("%s  %.1f %s", direction, strength, "km/h");

        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, x - fm.stringWidth(text) / 2, y);
    }

    /**
     * Détermine la couleur en fonction de la force du vent
     * 
     * @param strength - La force du vent
     * 
     * @return La couleur correspondante
     * 
     * @since 2.1
     */
    private Color getWindColor(double strength) {
        if (strength < 8.0) {
            return new Color(120, 220, 120);
        } else if (strength < 15.0) {
            return new Color(255, 200, 80);
        } else {
            return new Color(255, 120, 120);
        }
    }
}
