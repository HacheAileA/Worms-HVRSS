package model.players;

import java.util.ArrayList;
import java.util.Collections;

import app.Launcher;
import app.Main;
import model.GameModel;
import model.Map;
import model.items.Arsenal;
import model.items.Inventory;
import model.items.guns.*;
import model.items.tools.Tools;
import view.console.AnsiColor;

/**
 * Classe Team implémentant le fonctionnement des équipes du jeu.
 * 
 * @author NESI Romain
 * 
 * @see ArrayList#getClass()
 * @see Arsenal#getClass()
 * @see Guns#getClass()
 * @see Main#getClass()
 * @see Worm#getClass()
 * @see Map#getClass()
 * 
 * @since 0.0
 * 
 * @version 1.1
 */
public class Team {
    // ========================Constants========================//
    /** Tableau des couleurs possibles pour les équipes */
    private static final String[] TEAM_COLORS = { "RED", "BLUE", "GREEN", "YELLOW" };
    /** Couleur par défaut si l'ID de l'équipe est invalide */
    private static final String DEFAULT_COLOR = "WHITE";

    // ========================Attributes========================//
    /** Indique si l'équipe est contrôlée par un bot */
    private boolean isBot = false;
    /** Le nom de l'équipe */
    private String name;
    /** La liste des worms de l'équipe */
    private ArrayList<Worm> worms;
    /** L'inventaire de l'équipe */
    private Inventory inventory;
    /** Le symbole représentant l'équipe */
    private final char symbol;
    /** L'identifiant de l'équipe */
    private int teamId;
    /** Le modèle du jeu */
    private GameModel model;

    // ========================Builders========================//

    /**
     * Constructeur de la classe Team. Il permet de créer un objet Team et remplit l'équipe de vers.
     * 
     * @see ArrayList#ArrayList(int)
     * @see Arsenal#Arsenal(GameModel)
     * @see Team#initWorms(int)
     * 
     * @param name - La chaine de caractères correspondant au nom de l'équipe
     * @param teamId - L'identifiant de l'équipe
     * @param nbWorms - représentant le nombre de worms dans l'équipe
     * @param model - Le modèle du jeu
     * 
     * @since 1.1
     * 
     * @version 2.1
     */
    public Team(String name, int teamId, int nbWorms, GameModel model) {
        this.name = name;
        this.teamId = teamId;
        this.symbol = (char) ('A' + teamId);
        this.worms = new ArrayList<>(nbWorms);
        this.initWorms(nbWorms);
        this.model = model;
        this.inventory = new Inventory(model);
        initializeInventoryTeamOwnership();
    }

    /**
     * Constructeur de la classe Team (avec une liste de worms déjà définie).
     * 
     * @see ArrayList#ArrayList(int)
     * @see Arsenal#Arsenal(GameModel)
     * 
     * @param name - La chaine de caractères correspondant au nom de l'équipe
     * @param teamId - L'identifiant de l'équipe
     * @param worms - La liste des worms de l'équipe
     * @param symbol - Le symbole représentant l'équipe
     * @param model - Le modèle du jeu
     * 
     * @since 1.1
     */
    public Team(String name, int teamId, ArrayList<Worm> worms, char symbol, GameModel model) {
        this.name = name;
        this.teamId = teamId;
        this.symbol = symbol;
        this.worms = worms;
        this.model = model;
        this.inventory = new Inventory(model);
    }

    // ========================Accessors========================//

    /**
     * Getter pour btenir le nom de l'équipe.
     * 
     * @return La chaîne de caractères correspondant au nom d'équipe
     * 
     * @since 1.0
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter pour définir le nom de l'équipe.
     * 
     * @param name - La chaîne de caractères correspondant au nom d'équipe
     * 
     * @since 1.1
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Méthode qui permet de définir si l'équipe est contrôlée par un bot.
     * 
     * @param isBot - true si l'équipe est un bot, false sinon
     * 
     * @since 1.1
     */
    public void setBot(boolean isBot) {
        this.isBot = isBot;
    }

    /**
     * Méthode qui permet de savoir si l'équipe est contrôlée par un bot.
     * 
     * @return true, si l'équipe est un bot, false sinon
     * 
     * @since 1.1
     */
    public boolean isBot() {
        return isBot;
    }

    /**
     * Getter du nom de l'équipe colorée.
     * 
     * @see AnsiColor#getColoredString(String, String)
     * 
     * @return La chaîne de caractères correspondant au nom d'équipe coloré
     * 
     * @since 1.1
     */
    public String getColoredName() {
        return AnsiColor.getColoredString(this.getColor(), this.name);
    }

    /**
     * La fonction permet d'obtenir le symbole de l'équipe.
     * 
     * @return Le caractère correspondant au symbole de l'équipe
     * 
     * @since 1.1
     */
    public char getSymbol() {
        return this.symbol;
    }

    /**
     * Getter de la couleur de l'équipe.
     * 
     * @return La chaîne de caractères correspondant à la couleur de l'équipe
     * 
     * @since 1.1
     */
    public String getColor() {
        return (teamId >= 0 && teamId < TEAM_COLORS.length) ? TEAM_COLORS[teamId] : DEFAULT_COLOR;
    }

    /**
     * Getter de la liste des worms de l'équipe.
     * 
     * @return L'ArrayList correspondant à la liste des vers de l'équipe
     * 
     * @since 1.0
     */
    public ArrayList<Worm> getWorms() {
        return this.worms;
    }

    /**
     * Getter pour renvoyer l'inventaire de l'équipe.
     * 
     * @see Inventory#getClass()
     * 
     * @return L'inventaire de l'équipe
     * 
     * @since 1.1
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Méthode pour obtenir l'identifiant de l'équipe.
     * 
     * @return L'entier correspondant à l'identifiant de l'équipe
     * 
     * @since 1.1
     */
    public int getTeamId() {
        return teamId;
    }

    /**
     * Méthode pour savoir si l'équipe contient encore des worms vivant.
     * 
     * @return true, si au moins un worm est vivant, false sinon
     * 
     * @see Worm#isDead()
     * 
     * @since 1.1
     */
    public boolean containsWormAlive() {
        for (Worm w : this.worms) {
            if (!w.isDead()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Getter pour obtenir le modèle de jeu.
     * 
     * @return Le modèle du jeu
     * 
     * @since 1.1
     */
    public GameModel getModel() {
        return model;
    }
    // ========================Methods========================//

    /**
     * Méthode de placement des vers de l'équipe sur la carte. ** (cherche des positions valides en surface et dans les cavités internes).
     * 
     * @param map - La carte sur laquelle placer les vers.
     */
    public void placeWormsOnMap(Map map) {
        ArrayList<int[]> validPositions = findValidWormPositions(map);

        if (validPositions.isEmpty())
            return;

        Collections.shuffle(validPositions);
        placeWormsAtPositions(validPositions, map);
    }

    /**
     * Méthode de création de worms dans l'équipe. La méthode joue le rôle de setter.
     * 
     * @param nbWorms - Le nom de worms à créer
     * 
     * @see ArrayList#add(Object)
     * @see Launcher#CONFIG
     * @see Config#getWormRandomName(String)
     * @see Worm#Worm(String)
     * 
     * @since 1.1
     */
    private void initWorms(int nbWorms) {
        for (int i = 0; i < nbWorms; i++) {
            String wormName = app.Launcher.CONFIG.getWormRandomName(this.name);
            Worm w = new Worm(this, wormName, (char) ('1' + i));

            this.worms.add(w);
        }
    }

    /**
     * Méthode pour initialiser l'appartenance de l'inventaire à une équipe.
     */
    private void initializeInventoryTeamOwnership() {
        if (this.inventory == null)
            return;

        try {
            setTeamOwnershipForGuns();
            setTeamOwnershipForTools();
        } catch (Exception e) {
        }
    }

    /**
     * Méthode de définition de l'appartenance des armes de l'inventaire à l'équipe.
     */
    private void setTeamOwnershipForGuns() {
        for (Guns g : this.inventory.getAvailableGuns()) {
            if (g != null) {
                g.setTeam(this);
            }
        }
    }

    /**
     * Méthode de définition de l'appartenance des outils de l'inventaire à l'équipe.
     * 
     * @since 1.1
     */
    private void setTeamOwnershipForTools() {
        for (Tools t : this.inventory.getAvailableTools()) {
            if (t != null) {
                t.setTeam(this);
            }
        }
    }

    /**
     * Méthode pour trouver des positions valides pour placer les worms sur la carte.
     * 
     * @param map - La map utilisée
     * 
     * @return Les positions valides pour placer les vers
     * 
     * @since 1.1
     */
    private ArrayList<int[]> findValidWormPositions(Map map) {
        int width = map.getWidth();
        int height = map.getHeight();
        ArrayList<int[]> validPositions = new ArrayList<>();

        // récupérer toutes les positions valides
        for (int x = 0; x < width - 1; x++) {
            for (int y = 0; y < height - 1; y++) {
                if (isValidWormPosition(map, x, y)) {
                    validPositions.add(new int[] { x, y });
                }
            }
        }

        return validPositions;
    }

    /**
     * Méthode pour vérifier si une position est valide pour placer un worm (avant le placement).
     * 
     * @param map - La map utilisée
     * @param x - La coordonnée x à vérifier
     * @param y - La coordonnée y à vérifier
     * 
     * @see Map#isEmpty(int, int)
     * @see Map#isGround(int, int)
     * 
     * @return true, si la position est valide, false sinon
     * 
     * @since 2.1
     */
    private boolean isValidWormPosition(Map map, int x, int y) {
        if (!map.isEmpty(x, y) || !map.isGround(x, y + 1))
            return false;

        return y <= 0 || map.isEmpty(x, y - 1);
    }

    private void placeWormsAtPositions(ArrayList<int[]> validPositions, Map map) {
        int index = 0;
        for (Worm w : this.worms) {
            if (index >= validPositions.size())
                break;

            int[] pos = validPositions.get(index++);
            w.setPosition(pos[0], pos[1]);
            map.setCell(pos[0], pos[1], w.getSymbol());

            model.notifyWormPlaced(w, map);
        }
    }

}
