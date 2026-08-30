package view.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Image;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import controller.gui.GuiController;
import model.GameModel;
import model.Map;
import model.players.Team;

/**
 * Classe SettingsPanel représentant le panneau de configuration du jeu.
 * 
 * @author ARNAUD Hugo
 * 
 * @since 2.0
 * @version 2.1
 */
public class SettingsPanel extends JPanel {

    // ==================== LAYOUT CONSTANTS ====================

    /** Padding général autour du contenu principal */
    private static final int MAIN_PADDING = 30;

    /** Espacement vertical entre les sections principales */
    private static final int SECTION_VERTICAL_SPACING = 25;

    /** Espacement horizontal entre les sections */
    private static final int SECTION_HORIZONTAL_SPACING = 20;

    /** Padding interne des sections */
    private static final int SECTION_INTERNAL_PADDING = 15;

    /** Espacement vertical entre les composants */
    private static final int COMPONENT_VERTICAL_SPACING = 10;

    /** Espacement horizontal entre les composants */
    private static final int COMPONENT_HORIZONTAL_SPACING = 10;

    /** Largeur des boutons */
    private static final int BUTTON_WIDTH = 200;

    /** Hauteur des boutons */
    private static final int BUTTON_HEIGHT = 45;

    /** Hauteur de chaque ligne de saisie d'équipe */
    private static final int TEAM_INPUT_ROW_HEIGHT = 40;

    /** Taille de la police des titres de section */
    private static final int SECTION_TITLE_FONT_SIZE = 16;

    /** Taille de la police normale */
    private static final int NORMAL_FONT_SIZE = 13;

    /** Incrément de défilement vertical */
    private static final int SCROLL_UNIT_INCREMENT = 16;

    // ==================== COLOR CONSTANTS ====================

    /** Couleur de fond principale */
    private static final Color BACKGROUND_COLOR = new Color(240, 242, 245);

    /** Couleur de fond des sections */
    private static final Color SECTION_BACKGROUND_COLOR = new Color(255, 255, 255);

    /** Couleur des bordures */
    private static final Color BORDER_COLOR = new Color(200, 210, 220);

    /** Couleur d'accentuation */
    private static final Color ACCENT_COLOR = new Color(70, 130, 180);

    /** Couleur du texte des titres */
    private static final Color TITLE_TEXT_COLOR = new Color(50, 50, 50);

    // ==================== GAME SETTINGS CONSTANTS ====================

    /** Nombre minimum d'équipes */
    private static final int MIN_TEAMS = 2;

    /** Nombre maximum d'équipes */
    private static final int MAX_TEAMS = 4;

    /** Nombre d'équipes par défaut */
    private static final int DEFAULT_TEAMS = 2;

    /** Nombre minimum de worms par équipe */
    private static final int MIN_WORMS_PER_TEAM = 1;

    /** Nombre maximum de worms par équipe */
    private static final int MAX_WORMS_PER_TEAM = 4;

    /** Nombre de worms par équipe par défaut */
    private static final int DEFAULT_WORMS_PER_TEAM = 2;

    /** Nombre de points de vie de worms par défaut*/
    private static final int DEFAULT_WORMS_HP = 100;

    /** Nombre minimum de points de vie par worm  */
    private static final int MIN_WORMS_HP = 10;

    /** Nombre maximum de points de vie par worm */
    private static final int MAX_WORM_HP = 300;

    /** Difficulté minimale du bot */
    private static final int MIN_BOT_DIFFICULTY = 1;

    /** Difficulté maximale du bot */
    private static final int MAX_BOT_DIFFICULTY = 10;

    /** Difficulté du bot par défaut */
    private static final int DEFAULT_BOT_DIFFICULTY = 5;

    /** Espacement majeur pour les sliders */
    private static final int SLIDER_MAJOR_TICK_SPACING = 1;

    /** Largeur de la carte */
    private static final int MAP_WIDTH = 50;

    /** Hauteur de la carte */
    private static final int MAP_HEIGHT = 100;

    // ==================== UI COMPONENTS ====================

    /** */
    private JPanel mainPanel;
    /** */
    private JPanel contentPanel;
    /** */
    private JScrollPane scrollPane;

    // Section équipes
    /** */
    private JPanel teamSection;
    /** */
    private JLabel teamCountLabel;
    /** */
    private JSlider teamCountSlider;
    /** */
    private JPanel teamInputsPanel;

    // Section worms
    /** */
    private JPanel wormSection;
    /** */
    private JLabel wormCountLabel;
    /** */
    private JSlider wormCountSlider;
    /** */
    private JLabel wormHpLabel;
    /** */
    private JSlider wormHpSlider;

    // Section bot
    /** */
    private JPanel botSection;
    /** */
    private JLabel botDifficultyLabel;
    /** */
    private JSlider botDifficultySlider;

    // Section options de jeu
    /** */
    private JPanel gameOptionsSection;
    /** */
    private JCheckBox friendlyFireCheckBox;
    /** */
    private JCheckBox windCheckBox;
    /** */
    private JCheckBox teamNameCheckBox;
    /** */
    private ButtonGroup timerButtonGroup;
    /** */
    private JRadioButton timer10RadioButton;
    /** */
    private JRadioButton timer30RadioButton;
    /** */
    private JRadioButton timer45RadioButton;

    // Section carte
    /** */
    private JPanel mapSection;
    /** */
    private JComboBox<String> mapTypeComboBox;

    /** Aperçu de la map */
    private JLabel mapPreviewLabel;

    // Boutons d'action
    /** */
    private JPanel buttonPanel;
    /** */
    private JButton closeButton;

    // ==================== MODEL AND VIEW ====================

    /** */
    private GameModel model;
    /** */
    private GuiView view;

    // ==================== SETTINGS STATE ====================

    /** */
    private int nbTeams = DEFAULT_TEAMS;
    /** */
    private int nbWormsPerTeam = DEFAULT_WORMS_PER_TEAM;
    /** */
    private static int nbHpPerWorm = DEFAULT_WORMS_HP;
    /** */
    private int botDifficulty = DEFAULT_BOT_DIFFICULTY;
    /** */
    private boolean friendlyFire = false;
    /** */
    private boolean windEnabled = true;
    /** */
    private boolean showTeamNameOption = app.Launcher.CONFIG.getBoolParameter("TEAM_NAME_OPTION");
    /** */
    private String selectedMapType = "islands";

    // ==================== CONSTRUCTOR ====================

    /**
     * Constructeur de la classe SettingsPanel.
     * 
     * @param model Le modèle de jeu
     * @param view  La vue graphique
     * 
     * @since 2.1
     */
    public SettingsPanel(GameModel model, GuiView view) {
        this.model = model;
        this.view = view;

        setBackground(BACKGROUND_COLOR);
        initializeComponents();
        setupLayout();
    }

    // ==================== INITIALIZATION ====================

    /**
     * Initialise tous les composants de l'interface.
     * 
     * @since 2.1
     */
    private void initializeComponents() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(MAIN_PADDING, MAIN_PADDING, MAIN_PADDING, MAIN_PADDING));

        contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);

        createTeamSection();
        createWormSection();
        createBotSection();
        createGameOptionsSection();
        createMapSection();
        createButtonPanel();
    }

    /**
     * Crée une section stylisée.
     * 
     * @param title Le titre de la section
     * @return Le panel de la section
     * @since 2.1
     */
    private JPanel createStyledSection(String title) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(SECTION_BACKGROUND_COLOR);
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(SECTION_INTERNAL_PADDING, SECTION_INTERNAL_PADDING,
                        SECTION_INTERNAL_PADDING, SECTION_INTERNAL_PADDING)));

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, SECTION_TITLE_FONT_SIZE),
                ACCENT_COLOR);
        section.setBorder(BorderFactory.createCompoundBorder(
                section.getBorder(),
                titledBorder));

        return section;
    }

    /**
     * Crée un label stylisé.
     * 
     * @param text Le texte du label
     * @return Le label stylisé
     * @since 2.1
     */
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, NORMAL_FONT_SIZE));
        label.setForeground(TITLE_TEXT_COLOR);
        label.setAlignmentX(0.5f);
        return label;
    }

    /**
     * Crée la section de configuration des équipes.
     * 
     * @since 2.1
     */
    private void createTeamSection() {
        teamSection = createStyledSection("Configuration des Équipes");

        teamCountLabel = createStyledLabel("Nombre d'équipes : " + nbTeams);
        teamCountSlider = new JSlider(MIN_TEAMS, MAX_TEAMS, DEFAULT_TEAMS);
        teamCountSlider.setMajorTickSpacing(SLIDER_MAJOR_TICK_SPACING);
        teamCountSlider.setPaintTicks(true);
        teamCountSlider.setPaintLabels(true);
        teamCountSlider.setBackground(SECTION_BACKGROUND_COLOR);
        teamCountSlider.addChangeListener(e -> updateTeamCount());

        teamSection.add(teamCountLabel);
        teamSection.add(Box.createRigidArea(new Dimension(0, COMPONENT_VERTICAL_SPACING)));
        teamSection.add(teamCountSlider);
        teamSection.add(Box.createRigidArea(new Dimension(0, SECTION_INTERNAL_PADDING)));

        teamInputsPanel = new JPanel();
        teamInputsPanel.setLayout(new BoxLayout(teamInputsPanel, BoxLayout.Y_AXIS));
        teamInputsPanel.setBackground(SECTION_BACKGROUND_COLOR);
        createTeamInputRows();
        teamSection.add(teamInputsPanel);
    }

    /**
     * Crée les lignes de saisie pour chaque équipe.
     * 
     * @since 2.1
     */
    private void createTeamInputRows() {
        teamInputsPanel.removeAll();

        for (int i = 0; i < MAX_TEAMS; i++) {
            JPanel teamRow = new JPanel(new GridLayout(1, 3, COMPONENT_HORIZONTAL_SPACING, 0));
            teamRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEAM_INPUT_ROW_HEIGHT));
            teamRow.setBackground(SECTION_BACKGROUND_COLOR);

            JLabel label = new JLabel("Équipe " + (i + 1) + " :");
            label.setFont(new Font("SansSerif", Font.PLAIN, NORMAL_FONT_SIZE));
            JTextField nameField = new JTextField();
            nameField.setFont(new Font("SansSerif", Font.PLAIN, NORMAL_FONT_SIZE));
            JCheckBox botCheckBox = new JCheckBox("Bot");
            botCheckBox.setFont(new Font("SansSerif", Font.PLAIN, NORMAL_FONT_SIZE));
            botCheckBox.setBackground(SECTION_BACKGROUND_COLOR);

            teamRow.add(label);
            teamRow.add(nameField);
            teamRow.add(botCheckBox);

            if (i >= nbTeams) {
                label.setEnabled(false);
                nameField.setEnabled(false);
                botCheckBox.setEnabled(false);
            }

            teamInputsPanel.add(teamRow);
            if (i < MAX_TEAMS - 1) {
                teamInputsPanel.add(Box.createRigidArea(new Dimension(0, COMPONENT_VERTICAL_SPACING)));
            }
        }
    }

    /**
     * Crée la section de configuration des worms.
     * 
     * @since 2.1
     */
    private void createWormSection() {
        wormSection = createStyledSection("Configuration des worms");

        wormCountLabel = createStyledLabel("Nombre de worms par équipe : " + nbWormsPerTeam);
        wormCountSlider = new JSlider(MIN_WORMS_PER_TEAM, MAX_WORMS_PER_TEAM, DEFAULT_WORMS_PER_TEAM);
        wormCountSlider.setMajorTickSpacing(SLIDER_MAJOR_TICK_SPACING);
        wormCountSlider.setPaintTicks(true);
        wormCountSlider.setPaintLabels(true);
        wormCountSlider.setBackground(SECTION_BACKGROUND_COLOR);
        wormCountSlider.addChangeListener(e -> updateWormCount());

        wormSection.add(wormCountLabel);
        wormSection.add(Box.createRigidArea(new Dimension(0, COMPONENT_VERTICAL_SPACING)));
        wormSection.add(wormCountSlider);
        
        wormHpLabel = createStyledLabel("Nombre de points de vie par worm : " + nbHpPerWorm);
        wormHpSlider = new JSlider(MIN_WORMS_HP, MAX_WORM_HP, DEFAULT_WORMS_HP);
        wormHpSlider.setMajorTickSpacing(10);
        wormHpSlider.setPaintTicks(true);
        wormHpSlider.setPaintLabels(true);
        wormHpSlider.setBackground(SECTION_BACKGROUND_COLOR);
        wormHpSlider.addChangeListener(e -> updateWormHpCount());

        wormSection.add(wormHpLabel);
        wormSection.add(Box.createRigidArea(new Dimension(0, COMPONENT_VERTICAL_SPACING)));
        wormSection.add(wormHpSlider);
    }

    /**
     * Crée la section de configuration du bot.
     * 
     * @since 2.1
     */
    private void createBotSection() {
        botSection = createStyledSection("Intelligence Artificielle");

        botDifficultyLabel = createStyledLabel("Difficulté du bot : " + botDifficulty);
        botDifficultySlider = new JSlider(MIN_BOT_DIFFICULTY, MAX_BOT_DIFFICULTY, DEFAULT_BOT_DIFFICULTY);
        botDifficultySlider.setMajorTickSpacing(SLIDER_MAJOR_TICK_SPACING);
        botDifficultySlider.setPaintTicks(true);
        botDifficultySlider.setPaintLabels(true);
        botDifficultySlider.setBackground(SECTION_BACKGROUND_COLOR);
        botDifficultySlider.addChangeListener(e -> updateBotDifficulty());

        botSection.add(botDifficultyLabel);
        botSection.add(Box.createRigidArea(new Dimension(0, COMPONENT_VERTICAL_SPACING)));
        botSection.add(botDifficultySlider);
    }

    /**
     * Crée la section des options de jeu.
     * 
     * @since 2.1
     */
    private void createGameOptionsSection() {
        gameOptionsSection = createStyledSection("Options");

        friendlyFireCheckBox = new JCheckBox("Tirs alliés", friendlyFire);
        friendlyFireCheckBox.setFont(new Font("SansSerif", Font.PLAIN, NORMAL_FONT_SIZE));
        friendlyFireCheckBox.setBackground(SECTION_BACKGROUND_COLOR);
        friendlyFireCheckBox.addActionListener(e -> friendlyFire = friendlyFireCheckBox.isSelected());

        windCheckBox = new JCheckBox("Vent activé", windEnabled);
        windCheckBox.setFont(new Font("SansSerif", Font.PLAIN, NORMAL_FONT_SIZE));
        windCheckBox.setBackground(SECTION_BACKGROUND_COLOR);
        windCheckBox.addActionListener(e -> windEnabled = windCheckBox.isSelected());

        teamNameCheckBox = new JCheckBox("Affichage du nom des équipes", showTeamNameOption);
        teamNameCheckBox.setFont(new Font("SansSerif", Font.PLAIN, NORMAL_FONT_SIZE));
        teamNameCheckBox.setBackground(SECTION_BACKGROUND_COLOR);
        teamNameCheckBox.addActionListener(e -> app.Launcher.CONFIG.replace("TEAM_NAME_OPTION", showTeamNameOption = teamNameCheckBox.isSelected()));

        
        timerButtonGroup = new ButtonGroup();
        timer10RadioButton = new JRadioButton("Durée du tour : 10 secondes");
        timer30RadioButton = new JRadioButton("Durée du tour : 30 secondes");
        timer45RadioButton = new JRadioButton("Durée du tour : 45 secondes");
        switch (app.Launcher.CONFIG.getIntParameter("DEFAULT_TURN_TIME")) {
            case 10 -> timer10RadioButton.setSelected(true);
            case 30 -> timer30RadioButton.setSelected(true);
            case 45 -> timer45RadioButton.setSelected(true);
            default -> timer30RadioButton.setSelected(true);
        }

        timer10RadioButton.addActionListener(e -> app.Launcher.CONFIG.replace("DEFAULT_TURN_TIME", 10));
        timer30RadioButton.addActionListener(e -> app.Launcher.CONFIG.replace("DEFAULT_TURN_TIME", 30));
        timer45RadioButton.addActionListener(e -> app.Launcher.CONFIG.replace("DEFAULT_TURN_TIME", 45));

        timerButtonGroup.add(timer10RadioButton);
        timerButtonGroup.add(timer30RadioButton);
        timerButtonGroup.add(timer45RadioButton);

        gameOptionsSection.add(friendlyFireCheckBox);
        gameOptionsSection.add(Box.createRigidArea(new Dimension(0, COMPONENT_VERTICAL_SPACING)));
        gameOptionsSection.add(windCheckBox);
        gameOptionsSection.add(Box.createRigidArea(new Dimension(0, COMPONENT_VERTICAL_SPACING)));
        gameOptionsSection.add(teamNameCheckBox);
        gameOptionsSection.add(Box.createRigidArea(new Dimension(0, COMPONENT_VERTICAL_SPACING)));
        gameOptionsSection.add(timer10RadioButton);
        gameOptionsSection.add(timer30RadioButton);
        gameOptionsSection.add(timer45RadioButton);
    }

    /**
     * Crée la section de sélection de la carte.
     * 
     * @since 2.1
     */
    private void createMapSection() {
        mapSection = createStyledSection("Type de Carte");

        JLabel mapLabel = createStyledLabel("Sélectionnez le terrain :");

        String[] mapTypes = { "Islands", "Bridge", "Cave" };
        mapTypeComboBox = new JComboBox<>(mapTypes);
        mapTypeComboBox.setFont(new Font("SansSerif", Font.PLAIN, NORMAL_FONT_SIZE));
        mapTypeComboBox.setSelectedIndex(0);
        mapTypeComboBox.addActionListener(e -> updateMapType());

        // Panneau horizontal contenant le combo et l'aperçu
        JPanel mapChoicePanel = new JPanel();
        mapChoicePanel.setLayout(new BoxLayout(mapChoicePanel, BoxLayout.X_AXIS));
        mapChoicePanel.setBackground(SECTION_BACKGROUND_COLOR);

        mapTypeComboBox.setMaximumSize(new Dimension(200, 30));
        mapChoicePanel.add(mapTypeComboBox);
        mapChoicePanel.add(Box.createRigidArea(new Dimension(10, 0)));

        mapPreviewLabel = new JLabel("Aperçu non disponible", SwingConstants.CENTER);
        mapPreviewLabel.setPreferredSize(new Dimension(140, 90));
        mapPreviewLabel.setMaximumSize(new Dimension(140, 90));
        mapPreviewLabel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        mapPreviewLabel.setBackground(SECTION_BACKGROUND_COLOR);
        mapPreviewLabel.setOpaque(true);
        mapChoicePanel.add(mapPreviewLabel);

        mapSection.add(mapLabel);
        mapSection.add(Box.createRigidArea(new Dimension(0, COMPONENT_VERTICAL_SPACING)));
        mapSection.add(mapChoicePanel);

        // aperçu initial (essayez de charger plusieurs variantes)
        updateMapType();
    }

    /**
     * Crée le panneau des boutons d'action.
     * 
     * @since 2.1
     */
    private void createButtonPanel() {
        buttonPanel = new JPanel();
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        closeButton = new JButton("← Retour");
        closeButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        closeButton.setFont(new Font("SansSerif", Font.BOLD, NORMAL_FONT_SIZE));
        closeButton.setBackground(ACCENT_COLOR);
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> eventCloseButton());

        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(closeButton);
        buttonPanel.add(Box.createHorizontalGlue());
    }

    /**
     * Configure le layout principal avec sections gauche/droite.
     * 
     * @since 2.1
     */
    private void setupLayout() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.setBackground(BACKGROUND_COLOR);
        leftColumn.add(teamSection);
        leftColumn.add(Box.createRigidArea(new Dimension(0, SECTION_VERTICAL_SPACING)));
        leftColumn.add(wormSection);

        JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
        rightColumn.setBackground(BACKGROUND_COLOR);
        rightColumn.add(botSection);
        rightColumn.add(Box.createRigidArea(new Dimension(0, SECTION_VERTICAL_SPACING)));
        rightColumn.add(gameOptionsSection);
        rightColumn.add(Box.createRigidArea(new Dimension(0, SECTION_VERTICAL_SPACING)));
        rightColumn.add(mapSection);
        rightColumn.add(Box.createVerticalGlue());

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, SECTION_HORIZONTAL_SPACING / 2);
        contentPanel.add(leftColumn, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, SECTION_HORIZONTAL_SPACING / 2, 0, 0);
        contentPanel.add(rightColumn, gbc);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        scrollPane = new JScrollPane(mainPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_UNIT_INCREMENT);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

    // ==================== UPDATE METHODS ====================

    /**
     * Met à jour le nombre d'équipes.
     * 
     * @since 2.1
     */
    private void updateTeamCount() {
        nbTeams = teamCountSlider.getValue();
        teamCountLabel.setText("Nombre d'équipes : " + nbTeams);

        for (int i = 0; i < MAX_TEAMS; i++) {
            JPanel teamRow = (JPanel) teamInputsPanel.getComponent(i * 2);
            JLabel label = (JLabel) teamRow.getComponent(0);
            JTextField nameField = (JTextField) teamRow.getComponent(1);
            JCheckBox botCheckBox = (JCheckBox) teamRow.getComponent(2);

            boolean enabled = i < nbTeams;
            label.setEnabled(enabled);
            nameField.setEnabled(enabled);
            botCheckBox.setEnabled(enabled);
        }
    }

    /**
     * Met à jour le nombre de worms par équipe.
     * 
     * @since 2.1
     */
    private void updateWormCount() {
        nbWormsPerTeam = wormCountSlider.getValue();
        wormCountLabel.setText("Nombre de worms : " + nbWormsPerTeam);
    }

    /** Met à jour les hp des worms
     * 
     * @since 2.1
     */
    private void updateWormHpCount(){
        nbHpPerWorm = wormHpSlider.getValue();
        app.Launcher.CONFIG.replace("HP", nbHpPerWorm);
        wormHpLabel.setText("Nombre de points de vie par worm : " + nbHpPerWorm);
    }

    /**
     * Met à jour la difficulté du bot.
     * 
     * @since 2.1
     */
    private void updateBotDifficulty() {
        botDifficulty = botDifficultySlider.getValue();
        botDifficultyLabel.setText("Difficulté du robot : " + botDifficulty);
    }

    /**
     * Met à jour le type de carte sélectionné.
     * 
     * @since 2.1
     */
    private void updateMapType() {
        String selected = (String) mapTypeComboBox.getSelectedItem();
        if (selected != null) {
            selectedMapType = selected.toLowerCase();
            updateMapPreview(selectedMapType);
        }
    }

    /**
     * Met à jour l'image d'aperçu selon le type.
     * 
     * @param type Le nom en String de la map choisie
     * 
     * @since 2.1
     */
    private void updateMapPreview(String type) {
        if (mapPreviewLabel == null || type == null)
            return;

        java.net.URL imgUrl = getClass().getResource("/assets/preview_" + type.toLowerCase() + ".png");

        if (imgUrl != null) {
            ImageIcon icon = new ImageIcon(imgUrl);
            Image img = icon.getImage().getScaledInstance(mapPreviewLabel.getPreferredSize().width,
                    mapPreviewLabel.getPreferredSize().height, Image.SCALE_SMOOTH);
            mapPreviewLabel.setIcon(new ImageIcon(img));
            mapPreviewLabel.setText("");
        } else {
            mapPreviewLabel.setIcon(null);
            mapPreviewLabel.setText("Aperçu non disponible");
        }
    }

    // ==================== EVENT HANDLERS ====================

    /**
     * Démarre une nouvelle partie avec les paramètres actuels.
     * Cette méthode est appelée depuis HomePanel ou le menu.
     * 
     * @since 2.1
     */
    public void eventStartGameButton() {
        if (this.model == null) {
            System.err.println("Erreur : le modèle est null");
            return;
        }

        this.view.gameMenuBar.enableMenu(this.view.gameMenuBar.saveGame);

        Map map = new Map(MAP_WIDTH, MAP_HEIGHT);
        map.setMapType(selectedMapType);
        map.mapWithDecorations();

        ArrayList<Team> teams = new ArrayList<>();

        for (int i = 0; i < nbTeams; i++) {
            JPanel teamRow = (JPanel) teamInputsPanel.getComponent(i * 2);
            JTextField nameField = (JTextField) teamRow.getComponent(1);
            JCheckBox botCheckBox = (JCheckBox) teamRow.getComponent(2);

            String teamName = nameField.getText().trim();
            if (teamName.isEmpty()) {
                teamName = "Equipe " + (i + 1);
            }

            boolean isBot = botCheckBox.isSelected();

            Team team = new Team(teamName, i, nbWormsPerTeam, model);
            team.setBot(isBot);

            teams.add(team);
        }

        model.init(teams, map, friendlyFire);
        model.getWind().enabled = windEnabled;
        model.setWindEnabled(windEnabled);
        view.setTimer(app.Launcher.CONFIG.getIntParameter("DEFAULT_TURN_TIME"));

        this.view.setModel(model);
        this.view.setController(new GuiController(model, this.view));

        for (Team team : model.getTeams()) {
            team.placeWormsOnMap(map);
        }

        this.view.showMap();
        this.view.setBotDifficulty(botDifficulty);

        this.view.soundPlayer.stopMusic();
        this.view.soundPlayer.playBackgroundMusic("/sounds/ambiance/main_theme.wav", 5000);
    }

    /**
     * Ferme le panneau de paramètres et retourne au menu principal.
     * 
     * @since 2.1
     */
    private void eventCloseButton() {
        this.view.setContentPane(this.view.homePanel);
        this.view.homePanel.setupMenu();
        this.view.refresh();
    }

    // ==================== GETTERS ====================

    /**
     * @return Le nombre d'équipes configuré
     * @since 2.0
     */
    public int getNbTeams() {
        return nbTeams;
    }

    /**
     * @return Le nombre de worms par équipe configuré
     * @since 2.0
     */
    public int getNbWormsPnbWormsPerTeam() {
        return nbWormsPerTeam;
    }

    /**
     * @return Le nombre de point de vie par worm
     * @since 2.1
     */
    public static int getNbHpPerWorm(){
        return nbHpPerWorm;
    }

    /**
     * @return Le modèle de jeu
     * @since 2.0
     */
    public GameModel getModel() {
        return model;
    }

    /**
     * @return true si les tirs alliés sont activés
     * @since 2.0
     */
    public boolean getFriendlyFire() {
        return friendlyFire;
    }

    /**
     * @return true si le vent est activé
     * @since 2.1
     */
    public boolean isWindEnabled() {
        return windEnabled;
    }

    // ==================== MENU SETUP ====================

    /**
     * Configure les boutons de la barre de menu.
     * 
     * @since 2.0
     */
    protected void setupMenu() {
        GameMenu menuBar = this.view.gameMenuBar;
        menuBar.enableMenu(menuBar.newGame);
        menuBar.enableMenu(menuBar.loadGame);
        menuBar.disableMenu(menuBar.saveGame);
        menuBar.disableMenu(menuBar.paramGame);
    }
}