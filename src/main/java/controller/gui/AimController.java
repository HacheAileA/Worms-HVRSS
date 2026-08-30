package controller.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import model.GameModel;
import model.players.Worm;

/**
 * Classe qui permet de gérer la visée du worm actuel en version graphique
 * 
 * @author MESNILDREY Valentin
 * 
 * @see AbstractAction#getClass()
 * @see ActionMap#getClass()
 * @see InputMap#getClass()
 * @see JPanel#getClass()
 * @see KeyStroke#getClass()
 * @see GameModel#getClass()
 * @see Worm#getClass()
 * 
 * @since 2.0
 * 
 * @version 2.0
 */
public class AimController {

    /**
     * Le model à utilisé
     */
    private GameModel model;

    /**
     * Angle de visée
     */
    private final double delta = 0.03;

    /**
     * Constructeur qui permet d'initialiser le AimController
     * 
     * @param model - Le GameModel utilisé
     * 
     * @since 2.0
     */
    public AimController(GameModel model) {
        this.model = model;
    }

    /**
     * Méthode qui permet de monter l'angle de visée du worm actuel
     * 
     * @see Worm#rotateAim(double)
     * 
     * @since 2.0
     */
    public void aimUp() {
        Worm w = model.getCurrentWorm();
        if (w != null) {
            if (w.isFacingWest() && w.getAimAngle() > 0) {
                w.rotateAim(-w.getAimAngle() * 2);
            } else if (!w.isFacingWest() && w.getAimAngle() < 0) {
                w.rotateAim(-w.getAimAngle() * 2);
            }

            if (w.isFacingWest()) {
                if (w.getAimAngle() + delta <= 0) {
                    w.rotateAim(delta);
                } else {
                    w.rotateAim(-w.getAimAngle());
                }
            } else {
                if (w.getAimAngle() - delta >= 0) {
                    w.rotateAim(-delta);
                } else {
                    w.rotateAim(-w.getAimAngle());
                }
            }
        }
    }

    /**
     * Méthode qui permet de baisser l'angle de visée du worm actuel
     * 
     * @see Worm#rotateAim(double)
     * 
     * @since 2.0
     */
    public void aimDown() {
        Worm w = model.getCurrentWorm();
        if (w != null) {
            if (w.isFacingWest() && w.getAimAngle() > 0) {
                w.rotateAim(-w.getAimAngle() * 2);
            } else if (!w.isFacingWest() && w.getAimAngle() < 0) {
                w.rotateAim(-w.getAimAngle() * 2);
            }

            if (w.isFacingWest()) {
                if (w.getAimAngle() - delta >= -Math.PI) {
                    w.rotateAim(-delta);
                } else {
                    w.rotateAim(-Math.PI - w.getAimAngle());
                }
            } else {
                if (w.getAimAngle() + delta <= Math.PI) {
                    w.rotateAim(delta);
                } else {
                    w.rotateAim(Math.PI - w.getAimAngle());
                }
            }
        }
    }

    /**
     * Méthode de liaison entre les touches UP et DOWN et les actions d'aimUp et aimDown
     * 
     * @param panel - Le JPanel sur lequel lier les touches
     * 
     * @see AimController#bindKeysTo(JPanel, JPanel)
     * 
     * @since 2.0
     */
    public void bindKeysTo(JPanel panel) {
        bindKeysTo(panel, panel);
    }

    /**
     * Méthode de liaison entre les touches UP et DOWN et les actions d'aimUp et aimDown (Surcharge)
     * 
     * @param inputPanel - Le panel d'entrée
     * @param repaintPanel - Le panel de sortie
     * 
     * @see AbstractAction#AbstractAction()
     * @see ActionMap#put(Object, Action)
     * @see InputMap#put(KeyStroke, Object)
     * @see JPanel#getActionMap()
     * @see JPanel#getInputMap(int)
     * @see JPanel#repaint()
     * @see KeyStroke#getKeyStroke(String)
     * @see AimController#aimDown()
     * @see AimController#aimUp()
     * 
     * @since 2.0
     */
    public void bindKeysTo(JPanel inputPanel, JPanel repaintPanel) {
        InputMap im = inputPanel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = inputPanel.getActionMap();

        im.put(KeyStroke.getKeyStroke("UP"), "aimUp");
        im.put(KeyStroke.getKeyStroke("DOWN"), "aimDown");

        am.put("aimUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aimUp();
                repaintPanel.repaint();
            }
        });

        am.put("aimDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aimDown();
                repaintPanel.repaint();
            }
        });
    }
}
