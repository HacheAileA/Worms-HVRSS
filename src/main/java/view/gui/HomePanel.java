package view.gui;

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Classe HomePanel affichant le panel du menu Home.
 * 
 * @author ARNAUD Hugo
 * 
 * @since 2.0
 * 
 * @version 2.1
 */
public class HomePanel extends JPanel {

    // ==================== CONSTANTS ====================
    
    /** Largeur des boutons */
    private static final int BUTTON_WIDTH = 200;
    
    /** Hauteur des boutons */
    private static final int BUTTON_HEIGHT = 50;
    
    /** Marge gauche pour les boutons */
    private static final int BUTTON_LEFT_MARGIN = 50;
    
    /** Marge haut pour le premier bouton */
    private static final int BUTTON_TOP_MARGIN = 150;
    
    /** Espacement vertical entre les boutons */
    private static final int BUTTON_VERTICAL_SPACING = 20;
    
    // ==================== FIELDS ====================
    
    /** La vue à utiliser */
    private GuiView view;

    /** L'image de fond */
    private BufferedImage backgroundImage;
    
    /** Bouton Nouvelle Partie */
    private JButton newGameButton;
    
    /** Bouton Paramètres */
    private JButton settingsButton;
    
    /** Bouton Charger */
    private JButton loadButton;
    
    /** Bouton Quitter */
    private JButton exitButton;

    // ==================== CONSTRUCTOR ====================

    /**
     * Constructeur de HomePanel permettant de créer le menu principal.
     * 
     * @param view - La vue utilisée
     * 
     * @since 2.1
     */
    public HomePanel(GuiView view) {
        this.view = view;
        
        loadImages();
        initializeComponents();
        setupLayout();
        setupMenu();
    }

    // ==================== INITIALIZATION METHODS ====================

    /**
     * Méthode qui charge les images nécessaires.
     * 
     * @since 2.1
     */
    private void loadImages() {
        try {
            this.backgroundImage = ImageIO.read(getClass().getResource(
                    "/assets/menu_background.png"));
        } catch (IOException | NullPointerException e) {
            System.out.println("Impossible de charger l'image de fond pour le menu principal");
        }
    }

    /**
     * Méthode d'initialisation des composants (boutons).
     * 
     * @since 2.1
     */
    private void initializeComponents() {
        newGameButton = new JButton("Nouvelle Partie");
        newGameButton.addActionListener(e -> eventNewGameButton());
        
        settingsButton = new JButton("Paramètres");
        settingsButton.addActionListener(e -> eventSettingsButton());
        
        loadButton = new JButton("Charger");
        loadButton.addActionListener(e -> eventLoadButton());
        
        exitButton = new JButton("Quitter");
        exitButton.addActionListener(e -> eventExitButton());
    }

    /**
     * Méthode de configuration du layout avec les boutons alignés à gauche.
     * 
     * @since 2.1
     */
    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = BUTTON_WIDTH;
        gbc.ipady = BUTTON_HEIGHT - newGameButton.getPreferredSize().height;
        
        gbc.gridy = 0;
        gbc.insets = new Insets(BUTTON_TOP_MARGIN, BUTTON_LEFT_MARGIN, BUTTON_VERTICAL_SPACING, 0);
        add(newGameButton, gbc);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(0, BUTTON_LEFT_MARGIN, BUTTON_VERTICAL_SPACING, 0);
        add(settingsButton, gbc);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(0, BUTTON_LEFT_MARGIN, BUTTON_VERTICAL_SPACING, 0);
        add(loadButton, gbc);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(0, BUTTON_LEFT_MARGIN, 0, 0);
        add(exitButton, gbc);
    }

    // ==================== EVENT HANDLERS ====================

    /**
     * Gère l'événement du bouton "Nouvelle Partie". Lance directement la partie avec les paramètres actuels du SettingsPanel.
     * 
     * @since 2.1
     */
    private void eventNewGameButton() {
        this.view.gameMenuBar.disableMenu(this.view.gameMenuBar.paramGame);
        this.view.gameSettingsPanel.eventStartGameButton();
    }

    /**
     * Gère l'événement du bouton "Paramètres". Ouvre le panneau de configuration.
     * 
     * @since 2.1
     */
    private void eventSettingsButton() {
        this.view.setContentPane(this.view.gameSettingsPanel);
        this.view.gameSettingsPanel.setupMenu();
        this.view.refresh();
    }

    /**
     * Gère l'événement du bouton "Charger". Ouvre le panneau de chargement.
     * 
     * @since 2.1
     */
    private void eventLoadButton() {
        this.view.setContentPane(this.view.savePanel);
        this.view.savePanel.setupMenu();
        this.view.refresh();
    }

    /**
     * Gère l'événement du bouton "Quitter". Ferme l'application.
     * 
     * @since 2.1
     */
    private void eventExitButton() {
        System.exit(0);
    }

    // ==================== MENU SETUP ====================

    /**
     * Méthode pour définir les boutons à utiliser dans la barre de menu.
     * 
     * @since 2.0
     */
    protected void setupMenu() {
        GameMenu menuBar = this.view.gameMenuBar;
        menuBar.enableMenu(menuBar.newGame);
        menuBar.enableMenu(menuBar.loadGame);
        menuBar.disableMenu(menuBar.saveGame);
        menuBar.enableMenu(menuBar.paramGame);
    }

    // ==================== PAINTING ====================

    /**
     * Redéfinition de paintComponent pour dessiner l'image de fond et le logo.
     * 
     * @param g - Le contexte graphique
     * 
     * @since 2.1
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.backgroundImage != null) {
            g.drawImage(this.backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }
}