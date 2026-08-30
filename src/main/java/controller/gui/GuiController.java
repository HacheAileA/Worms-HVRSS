package controller.gui;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;

import controller.GameController;
import controller.GameInitializationService;
import model.GameModel;
import model.items.Inventory;
import model.items.tools.Tools;
import model.persistence.LoadManager;
import model.persistence.LoadManager.LoadResult;
import model.persistence.SaveManager;
import model.players.Team;
import model.players.Worm;
import view.gui.GuiView;

/**
 * Classe pour définir le controlleur pour le mode graphique.
 * 
 * @author ARNAUD Hugo
 * 
 * @see ArrayList#getClass()
 * @see InputStream#close()
 * @see Scanner#getClass()
 * @see GameInitializationService#getClass()
 * @see GameModel#getClass()
 * @see Inventory#getClass()
 * @see LoadManager#getClass()
 * @see LoadResult#getClass()
 * @see SaveManager#save(GameModel, String, Team)
 * @see Team#getClass()
 * @see Tools#getClass()
 * @see Worm#getClass()
 * 
 * @since 1.1
 * 
 * @version 2.0
 */
public class GuiController implements GameController {

    /**
     * Le modèle à utiliser.
     */
    public GameModel model;

    /**
     * La vue à utiliser.
     */
    public GuiView view;

    /**
     * Boolean qui représente le devMode.
     */
    public boolean devMode;

    /**
     * Constructeur pour créer un controlleur à partir d'un modèle et d'une vue.
     * 
     * @param model - Le modèle à utiliser
     * @param view - La vue à utiliser
     * 
     * @since 2.0
     */
    public GuiController(GameModel model, GuiView view) {
        this.model = model;
        this.view = view;
        view.setController(this);
    }

    /**
     * Méthode de sauvegarde de la partie en cours.
     * 
     * @see File#exists()
     * @see JOptionPane#showConfirmDialog(java.awt.Component, Object, String, int, int)
     * @see JOptionPane#showInputDialog(java.awt.Component, Object, String, int)
     * @see JOptionPane#showMessageDialog(java.awt.Component, Object, String, int)
     * @see SaveManager#save(GameModel, String, Team)
     * 
     * @since 1.2
     */
    public void saveGame() {
        if (model == null || model.getCurrentTeam() == null) {
            JOptionPane.showMessageDialog(view, "Aucune partie en cours à sauvegarder.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String filename = JOptionPane.showInputDialog(
                view,
                "Nom de la sauvegarde :",
                "Sauvegarder la partie",
                JOptionPane.PLAIN_MESSAGE);

        if (filename == null || filename.trim().isEmpty()) {
            return;
        }

        if (!filename.endsWith(".txt")) {
            filename += ".txt";
        }

        File file = new File("../saves/" + filename);

        if (file.exists()) {
            int choice = JOptionPane.showConfirmDialog(
                    view,
                    "Ce fichier existe déjà.\nVoulez-vous l'écraser ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }
        SaveManager.save(model, filename, model.getCurrentTeam());

        JOptionPane.showMessageDialog(
                view,
                "Savegarde effectuée avec succès.",
                "Sauvegarde",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Méthode pour savoir si le jeu est en mode développeur ou non.
     * 
     * @param devMode - Un boolean pour mettre le devMode à true ou false
     * 
     * @since 2.0
     */
    @Override
    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }
}
