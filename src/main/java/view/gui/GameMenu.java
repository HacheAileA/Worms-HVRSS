package view.gui;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import model.GameModel;

/**
 * Classe GameMenu représentant la barre de menu du jeu.
 * 
 * @author ARNAUD Hugo
 * 
 * @see JFrame#getClass()
 * @see JMenu#getClass()
 * @see JMenuBar#getClass()
 * @see JMenuItem#getClass()
 * @see GuiView#getClass()
 * @see SavePanel#getClass()
 * @see SettingsPanel#getClass()
 * 
 * @since 2.0
 * @version 2.1
 */
public class GameMenu extends JMenuBar {

    // ==================== FIELDS ====================

    /** La vue principale du jeu */
    private GuiView view;

    /** Menu principal */
    private JMenu gameMenu;

    /** Item de menu : Nouveau */
    protected JMenuItem newGame;

    /** Item de menu : Charger */
    protected JMenuItem loadGame;

    /** Item de menu : Sauvegarder */
    protected JMenuItem saveGame;

    /** Item de menu : Menu principal */
    protected JMenuItem mainMenuGame;

    /** Item de menu : Paramètres */
    protected JMenuItem paramGame;

    /** Item de menu : Quitter */
    protected JMenuItem quitGame;

    // ==================== CONSTRUCTOR ====================

    /**
     * Constructeur de la classe GameMenu.
     * 
     * @param view La vue principale du jeu
     * 
     * @see GameMenu#createFileMenu()
     * 
     * @since 2.0
     */
    public GameMenu(GuiView view) {
        this.view = view;
        this.createFileMenu();
    }

    // ==================== INITIALIZATION ====================

    /**
     * Méthode privée pour créer le menu.
     * 
     * @see JMenu#add(JMenuItem)
     * @see JMenu#addSeparator()
     * @see JMenu#JMenu(String)
     * @see JMenuBar#add(JMenu)
     * @see JMenuItem#addActionListener(java.awt.event.ActionListener)
     * @see JMenuItem#JMenuItem(String)
     * 
     * @since 2.0
     */
    private void createFileMenu() {
        this.gameMenu = new JMenu("Fichier");

        this.newGame = new JMenuItem("Nouveau");
        this.newGame.addActionListener(e -> this.eventStartNewGameButton());
        this.gameMenu.add(this.newGame);

        this.loadGame = new JMenuItem("Charger");
        this.loadGame.addActionListener(e -> this.eventLoadGameButton());
        this.gameMenu.add(this.loadGame);

        this.saveGame = new JMenuItem("Sauvegarder");
        this.saveGame.addActionListener(e -> this.eventSaveGameButton());
        this.gameMenu.add(this.saveGame);

        this.gameMenu.addSeparator();

        this.paramGame = new JMenuItem("Paramètres");
        this.paramGame.addActionListener(e -> this.eventOpenParametersButton());
        this.gameMenu.add(this.paramGame);

        this.gameMenu.addSeparator();
        this.gameMenu.addSeparator();

        this.mainMenuGame = new JMenuItem("Revenir au menu principal");
        this.mainMenuGame.addActionListener(e -> this.eventMainMenuButton());
        this.gameMenu.add(this.mainMenuGame);

        this.quitGame = new JMenuItem("Quitter");
        this.quitGame.addActionListener(e -> this.eventQuitGameButton());
        this.gameMenu.add(this.quitGame);

        this.add(this.gameMenu);
    }

    // ==================== EVENT HANDLERS ====================

    /**
     * Méthode pour démarrer une nouvelle partie. Lance directement la partie avec les paramètres du SettingsPanel.
     * 
     * @see JFrame#setContentPane(java.awt.Container)
     * @see GuiView#refresh()
     * @see SettingsPanel#eventStartGameButton()
     * 
     * @since 2.1
     */
    private void eventStartNewGameButton() {
        this.view.gameSettingsPanel.eventStartGameButton();
        this.disableMenu(this.paramGame);
    }

    /**
     * Méthode pour charger une partie.
     * 
     * @see JFrame#setContentPane(java.awt.Container)
     * @see GuiView#refresh()
     * @see SavePanel#setupMenu()
     * 
     * @since 2.0
     */
    private void eventLoadGameButton() {
        this.view.setContentPane(this.view.savePanel);
        this.view.savePanel.setupMenu();
        this.view.refresh();
    }

    /**
     * Méthode pour sauvegarder une partie.
     * 
     * @since 2.0
     */
    private void eventSaveGameButton() {
        if (this.view.getController() != null) {
            this.view.getController().saveGame();
        }
    }

    /**
     * Méthode pour revenir au menu principal. Sauvegarde la partie en cours et nettoie les ressources.
     * 
     * @see JFrame#setContentPane(java.awt.Container)
     * @see GuiView#refresh()
     * @see GameMenu#eventSaveGameButton()
     * @see HomePanel#setupMenu()
     * @see GuiView#setDefautDisplay()
     * @see GuiView#setMapShown(boolean)
     * 
     * @since 2.0
     */
    private void eventMainMenuButton() {
        this.eventSaveGameButton();

        view.dispose(); 
        view = new GuiView(new GameModel());

        this.view.refresh();
    }

    /**
     * Méthode pour accéder aux paramètres du jeu.
     * 
     * @see JFrame#setContentPane(java.awt.Container)
     * @see GuiView#refresh()
     * @see SettingsPanel#setupMenu()
     * 
     * @since 2.0
     */
    private void eventOpenParametersButton() {
        this.view.setContentPane(this.view.gameSettingsPanel);
        this.view.gameSettingsPanel.setupMenu();
        this.view.refresh();
    }

    /**
     * Méthode pour quitter la partie.
     * 
     * @since 2.0
     */
    private void eventQuitGameButton() {
        System.exit(0);
    }

    // ==================== MENU STATE MANAGEMENT ====================

    /**
     * Méthode pour désactiver un bouton de la barre de menu.
     * 
     * @param menuItem - Le menu à désactiver
     * 
     * @see JMenuItem#setEnabled(boolean)
     * 
     * @since 2.0
     */
    protected void disableMenu(JMenuItem menuItem) {
        menuItem.setEnabled(false);
    }

    /**
     * Méthode pour activer un bouton de la barre de menu.
     * 
     * @param menuItem - Le menu à activer
     * 
     * @see JMenuItem#setEnabled(boolean)
     * 
     * @since 2.0
     */
    protected void enableMenu(JMenuItem menuItem) {
        menuItem.setEnabled(true);
    }
}