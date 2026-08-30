package view.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import controller.gui.GuiController;
import model.GameModel;
import model.persistence.LoadManager;

/**
 * Classe SavePanel qui sert à afficher le panel du menu Save.
 * 
 * @author ARNAUD Hugo
 * 
 * @see Dimension#getClass()
 * @see File#getClass()
 * @see GridBagConstraints#getClass()
 * @see GridLayout#getClass()
 * @see Insets#Insets(int, int, int, int)
 * @see JButton#getClass()
 * @see JLabel#getClass()
 * @see JPanel#getClass()
 * @see GameMenu#getClass()
 * @see GuiView#getClass()
 * 
 * @since 2.0
 * 
 * @version 2.0
 */
public class SavePanel extends JPanel {

    /** GridBagConstraints pour le placement des composants */
    private GridBagConstraints gbc;
    /** Dimension des boutons */
    private Dimension buttonSize;
    /** La vue graphique à utiliser*/
    private GuiView view;

    /**
     * Constructeur de SavePanel permettant de créer une page pour le menu de sélection de la sauvegarde.
     * 
     * @param view - La vue utilisée
     * 
     * @see Dimension#Dimension(int, int)
     * @see GridBagConstraints#GridBagConstraints()
     * @see GridBagConstraints#HORIZONTAL
     * @see GridBagConstraints#RELATIVE
     * @see GridBagLayout#GridBagLayout()
     * @see JLabel#JLabel(String)
     * @see JPanel#add(java.awt.Component, Object)
     * @see JPanel#setLayout(java.awt.LayoutManager)
     * @see SavePanel#createSaveMenu()
     * 
     * @since 2.0
     */
    public SavePanel(GuiView view) {
        this.view = view;

        this.setLayout(new GridBagLayout());
        this.gbc = new GridBagConstraints();
        this.gbc.gridx = 0;
        this.gbc.gridy = GridBagConstraints.RELATIVE;
        this.gbc.fill = GridBagConstraints.HORIZONTAL;
        this.gbc.insets = new Insets(10, 0, 0, 0);

        this.buttonSize = new Dimension(100, 40);

        this.add(new JLabel("Choisir une partie à reprendre"), this.gbc);

        this.createSaveMenu();
    }

    /**
     * Méthode pour créer le menu de sélection de la sauvegarde.
     * 
     * @see File#File(String)
     * @see File#getName()
     * @see File#length()
     * @see File#listFiles()
     * @see GridLayout#GridLayout(int, int)
     * @see JButton#addActionListener(java.awt.event.ActionListener)
     * @see JButton#JButton(String)
     * @see JButton#setPreferredSize(Dimension)
     * @see JPanel#add(java.awt.Component, Object)
     * @see JPanel#JPanel(java.awt.LayoutManager)
     * @see SavePanel#eventCancelButton()
     * @see SavePanel#eventSelectSave(File)
     * 
     * @since 2.0
     */
    private void createSaveMenu() {
        File saveDir = new File("../saves/");
        if (!saveDir.exists() || !saveDir.isDirectory()) {
            saveDir.mkdirs();
        }

        File[] dir = saveDir.listFiles();
        if (dir == null)
            dir = new File[0];

        int nbSaves = 0;
        for (File file : dir) {
            if (file.length() != 0) {
                nbSaves++;
            }
        }

        JPanel savesListPanel = new JPanel(new GridLayout(nbSaves, 1));
        for (int i = 0; i < dir.length; i++) {
            File saveFile = dir[i];
            if (saveFile.length() != 0) {
                String saveFileName = saveFile.getName();
                JButton saveFileButton = new JButton(saveFileName.substring(0, saveFileName.length() - 4));
                saveFileButton.addActionListener(e -> this.eventSelectSave(saveFile));
                savesListPanel.add(saveFileButton);
            }
        }
        this.add(savesListPanel, this.gbc);

        JButton cancelButton = new JButton("Retour");
        cancelButton.setPreferredSize(this.buttonSize);
        cancelButton.addActionListener(e -> this.eventCancelButton());
        this.add(cancelButton, this.gbc);
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
        GameMenu menuBar = this.view.gameMenuBar;
        menuBar.enableMenu(menuBar.newGame);
        menuBar.disableMenu(menuBar.loadGame);
        menuBar.disableMenu(menuBar.saveGame);
        menuBar.disableMenu(menuBar.paramGame);
    }

    /**
     * Méthode privée pour sélectionner la sauvegarde.
     * 
     * @param file - Le fichier de sauvegarde
     * 
     * @see JFrame#setContentPane(java.awt.Container)
     * @see GuiView#setModel(GameModel)
     * @see GuiView#refresh()
     * @see LoadManager#load(File)
     * @see GuiView#showMap()
     * 
     * @since 2.0
     */
    private void eventSelectSave(File file) {
        LoadManager.LoadResult result = LoadManager.load(file);

        if (result != null) {
            GameModel model = result.model;
            // model.setTeams(model.getTeams());
            this.view.setModel(model);

            this.view.setController(new GuiController(model, this.view));

            this.view.showMap();

            this.view.gameMenuBar.enableMenu(this.view.gameMenuBar.saveGame);
            this.view.gameMenuBar.disableMenu(this.view.gameMenuBar.paramGame);

            this.view.refresh();
        }
    }

    /**
     * Méthode privée pour gérer l'événement du bouton de fermeture.
     * 
     * @see JFrame#setContentPane(java.awt.Container)
     * @see GuiView#refresh()
     * @see HomePanel#setupMenu()
     * 
     * @since 2.0
     */
    private void eventCancelButton() {
        this.view.setContentPane(this.view.homePanel);
        this.view.homePanel.setupMenu();
        this.view.refresh();
    }
}
