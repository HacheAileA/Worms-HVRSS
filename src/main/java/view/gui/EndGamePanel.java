package view.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.GameModel;

/**
 * Classe HomePanel affichant le panel du menu Home.
 * 
 * @author ARNAUD Hugo
 * 
 * @see Dimension#getClass()
 * @see GridBagConstraints#getClass()
 * @see GridBagLayout#getClass()
 * @see Insets#getClass()
 * @see JButton#getClass()
 * @see JLabel#getClass()
 * @see JPanel#getClass()
 * @see GameMenu#getClass()
 * 
 * @since 2.0
 * 
 * @version 2.0
 */
public class EndGamePanel extends JPanel {
    /**
     * L'image à afficher
     */
    private BufferedImage image;

    /**
     * Les contraintes pour le layout
    */
    private GridBagConstraints gbc;
    /**
     * La taille des boutons
    */
    private Dimension buttonSize;

    /**
     * Le panel principal
    */
    private JPanel panel;

    /**
     * La vue utilisée
    */
    private GuiView view;

    /**
     * Constructeur de EndGamePanel permettant de créer une page avec un titre pour la fin du jeu.
     * 
     * @param view La vue utilisée
     * 
     * @see Dimension#Dimension(int, int)
     * @see GridBagConstraints#GridBagConstraints()
     * @see GridBagLayout#GridBagLayout()
     * @see Insets#Insets(int, int, int, int)
     * @see JLabel#JLabel(String)
     * @see JPanel#add(java.awt.Component, int)
     * @see EndGamePanel#createEndGameMenu()
     * 
     * @since 2.0
     */
    public EndGamePanel(GuiView view) {

        try {
            this.image = ImageIO.read(getClass().getResource(
                    "/assets/menu_end.png"));
        } catch (IOException | NullPointerException e) {
            System.out.println("Impossible de charger l'image pour le menu principal");
        }

        this.view = view;

        this.panel = new JPanel(new GridBagLayout());

        this.gbc = new GridBagConstraints();
        this.gbc.gridx = 0;
        this.gbc.gridy = GridBagConstraints.RELATIVE;
        this.gbc.fill = GridBagConstraints.HORIZONTAL;
        this.gbc.insets = new Insets(10, 0, 10, 0);

        this.buttonSize = new Dimension(300, 40);

        this.panel.setOpaque(false);
        this.setLayout(new GridBagLayout());
        GridBagConstraints rootGbc = new GridBagConstraints();
        rootGbc.gridx = 0;
        rootGbc.gridy = 0;
        rootGbc.weightx = 1.0;
        rootGbc.weighty = 1.0;
        rootGbc.fill = GridBagConstraints.BOTH;

        this.add(panel, rootGbc);

        this.createEndGameMenu();
    }

    /**
     * Méthode pour définir les boutons à utiliser dans la barre de menu.
     * 
     * @see GameMenu#disableMenu(javax.swing.JMenuItem)
     * @see GameMenu#enableMenu(javax.swing.JMenuItem)
     * 
     * @since 2.0
     */
    protected void setupMenu() {
        GameMenu menuBAr = this.view.gameMenuBar;
        menuBAr.enableMenu(menuBAr.newGame);
        menuBAr.disableMenu(menuBAr.loadGame);
        menuBAr.disableMenu(menuBAr.saveGame);
        menuBAr.disableMenu(menuBAr.paramGame);
    }

    /**
     * Méthode privée pour créer le menu de fin du jeu.
     * 
     * @see JButton#addActionListener(java.awt.event.ActionListener)
     * @see JButton#JButton(String)
     * @see JButton#setPreferredSize(Dimension)
     * @see JLabel#JLabel(String, int)
     * @see JPanel#add(java.awt.Component)
     * @see JPanel#add(java.awt.Component, Object)
     * @see EndGamePanel#eventBackHomeButton()
     * 
     * @since 2.0
     */
    private void createEndGameMenu() {

        JButton backHomeButton = new JButton("Revenir au menu principal");
        backHomeButton.setPreferredSize(this.buttonSize);
        backHomeButton.addActionListener(e -> this.eventBackHomeButton());

        backHomeButton.setContentAreaFilled(false);
        backHomeButton.setBorderPainted(false);
        backHomeButton.setFocusPainted(false);
        backHomeButton.setOpaque(false);
        backHomeButton.setFont(new Font("Arial", Font.BOLD, 30));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 50, 20, 0);
        panel.add(backHomeButton, gbc);

        String winningTeam = view.model.getWinningTeam().getName();
        JLabel winnerLabel = new JLabel("Victoire de l'équipe : " + winningTeam, JLabel.CENTER);
        winnerLabel.setForeground(Color.BLACK);
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 30));

        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.insets = new Insets(0, 0, 30, 0);
        panel.add(winnerLabel, gbc);
    }

    /**
     * Méthode privée pour gérer l'événement du bouton de retour au menu principal.
     * 
     * @see JFrame#setContentPane(java.awt.Container)
     * @see GuiView#refresh()
     * @see HomePanel#setupMenu()
     * 
     * @since 2.0
     */
    private void eventBackHomeButton() {
        this.view.setContentPane(this.view.homePanel);
        this.view.homePanel.setupMenu();
        this.view.refresh();
        view.dispose(); // Supprime la fenêtre (le GuiView)
        view = new GuiView(new GameModel()); // Reconstruit la fenêtre (le GuiView aussi)
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.image != null) {
            g.drawImage(this.image, 0, 0, this.getWidth(), this.getHeight(), this);
        }
        view.soundPlayer.stopMusic();
    }
}
