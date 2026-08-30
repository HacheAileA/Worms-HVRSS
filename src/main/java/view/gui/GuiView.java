package view.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import controller.bot.BotController;
import controller.gui.AimController;
import controller.gui.GuiController;
import controller.gui.ShootController;
import model.GameModel;
import model.Map;
import model.physics.Projectile;
import model.players.Worm;
import view.GameView;

/**
 * Classe GuiView qui gère l'affichage en vue graphique (2D).
 * 
 * @author ARNAUD Hugo
 * @author MESNILDREY Valentin
 * 
 * @see Dimension#getClass()
 * @see JFrame#getClass()
 * 
 * @since 2.0
 * 
 * @version 2.0
 */
public class GuiView extends JFrame implements GameView {
    /** Constante représentant les images par seconde */
    private static final int FPS = 30;
    /** Constante représentant le temps par défaut d'un tour en secondes */
    private static final int DEFAULT_TURN_TIME = app.Launcher.CONFIG.getIntParameter("DEFAULT_TURN_TIME");
    /** Constante représentant la hauteur du HUD */
    private static final int HUD_HEIGHT = 80;
    /** Constante représentant le délai avant que le bot joue son tour en ms */
    private static final int BOT_DELAY_MS = 1000;
    /**
     * Constante représentant le délai initial avant que le bot joue son tour en ms
     */
    private static final int BOT_INITIAL_DELAY_MS = 500;

    /** Le GameModel où sont stockées toutes les informations du model */
    public GameModel model;
    /** Le controller de l'interface graphique */
    private GuiController controller;
    /** Le player de sons */
    public SoundPlayer soundPlayer;

    /** Le GamePanel */
    protected GamePanel gamePanel;
    /** Le HomePanel */
    protected HomePanel homePanel;
    /** Le SettingsPanel */
    protected SettingsPanel gameSettingsPanel;
    /** Le GameMenu */
    protected GameMenu gameMenuBar;
    /** Le SavePanel */
    protected SavePanel savePanel;
    /** Le EndGamePanel */
    protected EndGamePanel endGamePanel;

    /** Booléen indiquant si la map est affichée */
    private boolean mapShown = false;
    /** Le temps par tour en secondes */
    private int turnTimeSeconds = DEFAULT_TURN_TIME;
    /** Le temps restant avant la fin du tour */
    private int timeLeft;

    /** Le renderer de la trajectoire */
    TrajectoryRenderer trajectoryRenderer;
    /** Le layer de la trajectoire */
    JPanel trajectoryLayer;
    /** Le layer des projectiles */
    JPanel projectileLayer;
    /** Le panel de l'inventaire */
    InventoryPanel inventoryPanel;

    /** Les particules */
    private ArrayList<Particle> particles = new ArrayList<>();
    /** Le timer des particules */
    private Timer particleTimer;
    /** Le label du timer de tour */
    private JLabel turnTimerLabel;
    /** Le timer du tour */
    Timer turnTimer;
    /** Le timer du projectile */
    Timer projectileTimer;

    /** Le controller de l'aim */
    AimController aimController;
    /** Le controller du shoot */
    ShootController shootController;

    /** Le controller du bot */
    private BotController botController;

    // ----------------------------- CONSTRUCTORS -----------------------------

    /**
     * Constructeur de GUI view permettant d'initialiser la vue Graphique. Ajout du
     * titre, de la taille etc.
     * 
     * @param model - Le GameModel où sont stockées toutes les informations du model
     * 
     * @see JFrame#setTitle(String)
     * @see JFrame#setPreferredSize(Dimension)
     * @see JFrame#setJMenuBar(javax.swing.JMenuBar)
     * @see JFrame#setContentPane(java.awt.Container)
     * @see JFrame#setLocation(int, int)
     * @see JFrame#pack()
     * @see JFrame#setLocationRelativeTo(java.awt.Component)
     * @see JFrame#setVisible(boolean)
     * @see JFrame#setAlwaysOnTop(boolean)
     * @see JFrame#toFront()
     * @see JFrame#requestFocus()
     * @see JFrame#setDefaultCloseOperation(int)
     * 
     * @since 2.0
     */
    public GuiView(GameModel model) {
        this.setTitle("Worms");
        this.setModel(model);
        this.createPanels();

        this.setJMenuBar(this.gameMenuBar);
        this.setContentPane(this.homePanel);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setDefautDisplay();

        this.setFocusable(true);
        this.requestFocusInWindow();

        this.setVisible(true);

        this.setAlwaysOnTop(true);
        this.toFront();
        this.requestFocus();
        this.setAlwaysOnTop(false);
    }

    /**
     * Méthode pour définir les paramètres d'affichage par défaut de la fenêtre
     * 
     * @see JFrame#pack()
     * @see JFrame#setExtendedState(int)
     * @see JFrame#setSize(Dimension)
     * @see JFrame#setResizable(boolean)
     * @see JFrame#setLocationRelativeTo(java.awt.Component)
     * 
     * @since 2.0
     */
    protected void setDefautDisplay() {
        int xScreenSize = (int) this.getToolkit().getScreenSize().getWidth();
        int yScreenSize = (int) this.getToolkit().getScreenSize().getHeight();

        this.pack();
        this.setSize(getToolkit().getScreenSize());
        this.setResizable(false);

        if (this.model != null && this.model.getMap() != null) {
            MapRenderer.fitToDimensions(xScreenSize, yScreenSize, this.model.getMap().getWidth(),
                    this.model.getMap().getHeight());
        }
    }

    // ----------------------------- ACCESSORS -----------------------------

    /**
     * Getter permettant d'obtenir la layer de la prévisualisation de la trajectoire
     * 
     * @return Le JPanel correspondant
     */
    public JPanel getTrajectoryLayer() {
        return trajectoryLayer;
    }

    /**
     * Getter permettant d'obtenir le panel de l'inventaire
     * 
     * @return L'InventoryPanel correspondant
     */
    public InventoryPanel getInventoryPanel() {
        return inventoryPanel;
    }

    /**
     * Méthode pour choisir le model.
     * 
     * @param model - La model choisi
     * 
     * @since 1.2
     */
    public void setModel(GameModel model) {
        this.model = model;
    }

    /**
     * Getter pour obtenir le controller
     * 
     * @return Le controller
     * 
     * @since 2.0
     */
    public GuiController getController() {
        return controller;
    }

    /**
     * Setter pour définir le controller
     * 
     * @param controller - le controller à définir
     * 
     * @since 2.0
     */
    public void setController(GuiController controller) {
        this.controller = controller;
    }

    /**
     * Setter pour mettre le devMode à la valeur voulue
     * 
     * @param devMode - valeur du devMode voulue
     * 
     * @since 2.0
     */
    public void setDevMode(boolean devMode) {
        return;
    }

    /**
     * Getter pour obtenir le GamePanel
     * 
     * @return Le GamePanel
     * 
     * @since 2.0
     */
    public GamePanel getGamePanel() {
        return gamePanel;
    }

    /**
     * Getter pour obtenir le SettingsPanel
     * 
     * @return le SettingsPanel
     * 
     * @since 2.0
     */
    public SettingsPanel getSettingsPanel() {
        return gameSettingsPanel;
    }

    /**
     * Getter pour savoir si la map est affichée
     *
     * @param mapShown - true si la map est affichée, false sinon
     * 
     * @since 2.0
     */
    public void setMapShown(boolean mapShown) {
        this.mapShown = mapShown;
    }

    /**
     * Setter pour définir la difficulté du bot
     * 
     * @param difficulty - La difficulté du bot
     * 
     * @since 2.1
     */
    public void setBotDifficulty(int difficulty) {
        this.botController = new BotController(difficulty, this);
    }

    /**
     * méthode pour mettre le timer à un temps prédéfini
     * 
     * @param timer temps voulu
     * @since 2.1
     */
    public void setTimer(int timer) {
        this.turnTimeSeconds = timer;
    }

    // ----------------------------- PUBLIC METHODS -----------------------------

    @Override
    public void showMap() {
        if (mapShown)
            return;

        MapRenderer.fitToDimensions(this.getWidth(), this.getHeight(), model.getMap().getWidth(),
                model.getMap().getHeight());
        initGamePanels();
        JLayeredPane layeredPane = createLayeredPane();
        layeredPane.addMouseWheelListener(gamePanel.getInputController());

        setupLayers(layeredPane);
        finalizeMapDisplay(layeredPane);

        mapShown = true;
        startTurnTimer();
        gamePanel.zoomIn(new Point(0, 0));
        if (model.getCurrentTeam() != null && model.getCurrentTeam().isBot()) {
            handleBotTurnStart();
        }
    }

    /**
     * Méthode pour démarrer le timer du tour
     * 
     * @see Timer#start()
     * @see Timer#stop()
     * 
     * @since 2.0
     */
    public void startTurnTimer() {
        stopTimerIfRunning(turnTimer);

        timeLeft = turnTimeSeconds;
        updateTimerLabel();

        turnTimer = new Timer(1000, e -> {
            timeLeft--;
            updateTimerLabel();

            if (timeLeft < 0) {
                ensureWormOnGround();
            }
        });

        turnTimer.start();
    }

    /**
     * Méthode pour démarrer le timer du projectile
     * 
     * @see Timer#start()
     * 
     * @since 2.0
     */
    public void startProjectileTimer() {
        stopTimerIfRunning(projectileTimer);
        projectileTimer = new Timer(1000 / FPS, e -> updateProjectiles());
        projectileTimer.start();
    }

    /**
     * Méthode pour gérer la fin du tour
     * 
     * @see GameModel#nextTurn()
     * @see GuiView#startTurnTimer()
     * 
     * @since 2.0
     */
    public void handleTurnEnd() {
        if (model.isGameOver()) {
            handleGameOver();
            return;
        }

        stopTimerIfRunning(turnTimer);
        model.nextTurn();
        gamePanel.centerCameraOnCurrentWorm();

        if (model.getCurrentTeam().isBot()) {
            handleBotTurn();
        } else {
            handlePlayerTurn();
        }
    }

    /**
     * Méthode pour ajouter des particules à afficher
     * 
     * @param p - La particule à ajouter
     * 
     * @since 2.0
     */
    public void addParticles(Particle p) {
        particles.add(p);
    }

    /**
     * Méthode pour démarrer le timer des particules
     * 
     * @see Timer#start()
     * 
     * @since 2.0
     */
    public void startParticleTimer() {
        if (particleTimer == null) {
            particleTimer = new Timer(1000 / 30, e -> {
                updateParticles();
                projectileLayer.repaint();
            });
        }
        particleTimer.start();
    }

    /**
     * Méthode pour mettre à jour l'affichage
     * 
     * @see JFrame#revalidate()
     * @see JFrame#repaint()
     * 
     * @since 2.0
     */
    protected void refresh() {
        if (this.gamePanel != null) {
            this.gamePanel.repaint();
        }
        this.revalidate();
        this.repaint();
    }

    // -------------------- PRIVATE METHODS --------------------

    /**
     * Méthode pour créer les panels de l'interface graphique
     * 
     * @since 2.0
     */
    private void createPanels() {
        this.gameMenuBar = new GameMenu(this);
        this.homePanel = new HomePanel(this);
        this.savePanel = new SavePanel(this);
        this.gameSettingsPanel = new SettingsPanel(this.model, this);
        this.soundPlayer = new SoundPlayer();
        soundPlayer.playBackgroundMusic("/sounds/ambiance/main_menu_theme.wav", 10000);
        this.setModel(this.gameSettingsPanel.getModel());
    }

    /**
     * Méthode d'initialisation des panels de jeu
     * 
     * @since 2.0
     */
    private void initGamePanels() {
        if (this.gamePanel == null) {
            this.gamePanel = new GamePanel(this.model, this);
            gamePanel.setParticlesList(particles);
            inventoryPanel = new InventoryPanel(model, this);
            inventoryPanel.setWindRenderer(new WindRenderer(model.getWind()));
            inventoryPanel.setOpaque(false);
        }
    }

    /**
     * Méthode pour créer le layered pane (la couche superposée)
     * 
     * @return Le JLayeredPane créé
     */
    private JLayeredPane createLayeredPane() {
        JLayeredPane layeredPane = new JLayeredPane();
        Dimension mapSize = gamePanel.getPreferredSize();
        layeredPane.setPreferredSize(mapSize);
        return layeredPane;
    }

    /**
     * Méthode pour configurer les différentes couches du layered pane
     * 
     * @param layeredPane - Le JLayeredPane à configurer
     * 
     * @since 2.0
     */
    private void setupLayers(JLayeredPane layeredPane) {
        addMapLayer(layeredPane);
        addTrajectoryLayer(layeredPane);
        initControllers();
        addProjectileLayer(layeredPane);
        addHUDLayer(layeredPane);
        addTurnTimerLabel(layeredPane);
    }

    /**
     * Méthode pour ajouter la couche de la map
     * 
     * @param layeredPane - Le JLayeredPane où ajouter la couche
     * 
     * @since 2.0
     */
    private void addMapLayer(JLayeredPane layeredPane) {
        Dimension mapSize = gamePanel.getPreferredSize();
        gamePanel.setBounds(0, 0, mapSize.width, mapSize.height);
        layeredPane.add(gamePanel, JLayeredPane.DEFAULT_LAYER);
    }

    /**
     * Méthode pour ajouter la ligne (couche) de la trajectoire
     * 
     * 
     * @param layeredPane - Le JLayeredPane où ajouter la ligne (couche)
     * 
     * 
     * @since 2.0
     */
    private void addTrajectoryLayer(JLayeredPane layeredPane) {
        Dimension mapSize = gamePanel.getPreferredSize();

        if (trajectoryRenderer == null) {
            trajectoryRenderer = new TrajectoryRenderer();
        }

        if (trajectoryLayer == null) {
            trajectoryLayer = createTrajectoryPanel();
            trajectoryLayer.setOpaque(false);
            trajectoryLayer.setBounds(0, 0, mapSize.width, mapSize.height);
            layeredPane.add(trajectoryLayer, JLayeredPane.PALETTE_LAYER);
        }
    }

    /**
     * Méthode pour créer la ligne (couche) de la trajectoire
     * 
     * @return Le JPanel créé pour la trajectoire
     * 
     * @since 2.0
     */
    private JPanel createTrajectoryPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Worm active = model.getCurrentWorm();
                if (active != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.translate(gamePanel.getCameraOffsetX(), gamePanel.getCameraOffsetY());
                    trajectoryRenderer.render(g2d, active, model.getMap());
                }
            }
        };
    }

    /**
     * Méthode pour initialiser les controllers de trajectoire
     * 
     * @since 2.0
     */
    private void initControllers() {
        if (aimController == null) {
            aimController = new AimController(model);
            aimController.bindKeysTo(trajectoryLayer);
        }

        if (shootController == null) {
            shootController = new ShootController(model, this);
            shootController.bindMouseTo(trajectoryLayer);
        }
    }

    /**
     * Méthode pour ajouter la couche des projectiles
     * 
     * @param layeredPane - Le JLayeredPane où ajouter la couche
     * 
     * @since 2.0
     */
    private void addProjectileLayer(JLayeredPane layeredPane) {
        Dimension mapSize = gamePanel.getPreferredSize();

        if (projectileLayer == null) {
            projectileLayer = createProjectilePanel();
            projectileLayer.setOpaque(false);
            projectileLayer.setBounds(0, 0, mapSize.width, mapSize.height);
            layeredPane.add(projectileLayer, JLayeredPane.DRAG_LAYER);
        }
    }

    /**
     * Méthode de création de la couche des projectiles
     * 
     * @return Le JPanel créé pour les projectiles
     * 
     * @since 2.0
     */
    private JPanel createProjectilePanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.translate(gamePanel.getCameraOffsetX(), gamePanel.getCameraOffsetY());
                renderProjectiles(g2d);
            }
        };
    }

    /**
     * Méthode pour dessiner les projectiles
     * 
     * @param g2d - Le Graphics2D où dessiner les projectiles
     * 
     * @since 2.0
     */
    private void renderProjectiles(Graphics2D g2d) {
        int size = MapRenderer.getTileSize() / 2;
        g2d.setColor(Color.BLACK);

        for (Projectile p : model.getProjectiles()) {
            int px = (int) (p.getX() * MapRenderer.TILE_SIZE);
            int py = (int) (p.getY() * MapRenderer.TILE_SIZE);
            g2d.fillOval(px - 6, py - 6, size, size);
        }
    }

    /**
     * Méthode pour ajouter la couche HUD
     * 
     * @param layeredPane - Le JLayeredPane où ajouter la couche
     * 
     * @since 2.0
     */
    private void addHUDLayer(JLayeredPane layeredPane) {
        Dimension mapSize = gamePanel.getPreferredSize();
        inventoryPanel.setBounds(0, 0, mapSize.width, HUD_HEIGHT);
        layeredPane.add(inventoryPanel, JLayeredPane.MODAL_LAYER);
    }

    /**
     * Méthode pour ajouter le label du timer (de tour)
     * 
     * @param layeredPane - Le JLayeredPane où ajouter le label
     * 
     * @since 2.0
     */
    private void addTurnTimerLabel(JLayeredPane layeredPane) {
        if (turnTimerLabel == null) {
            turnTimerLabel = new JLabel();
            turnTimerLabel.setFont(new Font("Arial", Font.BOLD, 24));
            turnTimerLabel.setForeground(Color.WHITE);

            int screenWidth = getToolkit().getScreenSize().width;
            int windOffset = 190;
            int labelWidth = 200;
            turnTimerLabel.setBounds(screenWidth - windOffset - labelWidth, 20, labelWidth, 40);
            turnTimerLabel.setHorizontalAlignment(JLabel.RIGHT);
            turnTimerLabel.setOpaque(false);
            layeredPane.add(turnTimerLabel, JLayeredPane.DRAG_LAYER);
        }

        timeLeft = turnTimeSeconds;
        updateTimerLabel();

        addSkipButton(layeredPane);
    }

    /**
     * Méthode pour ajouter le bouton "Skip Turn"
     * 
     * @param layeredPane - Le JLayeredPane où ajouter le bouton
     * 
     * @since 2.0
     */
    private void addSkipButton(JLayeredPane layeredPane) {
        JButton skipButton = new JButton("Passer le tour");
        skipButton.setBounds(1200, 25, 120, 30);
        skipButton.addActionListener(e -> handleSkipTurn(skipButton));
        layeredPane.add(skipButton, JLayeredPane.DRAG_LAYER);
    }

    /**
     * Méthode de gestion du clic sur le bouton "Skip Turn"
     * 
     * @param skipButton - Le bouton "Skip Turn"
     * 
     * @since 2.0
     */
    private void handleSkipTurn(JButton skipButton) {
        Worm w = model.getCurrentWorm();
        if (w == null)
            return;

        skipButton.setEnabled(false);
        gamePanel.startMovementTimer();

        Timer waitForFallTimer = new Timer(30, null);
        waitForFallTimer.addActionListener(ev -> {
            if (w.isOnGround(model.getMap())) {
                waitForFallTimer.stop();
                skipButton.setEnabled(true);
                handleTurnEnd();
            }
        });
        waitForFallTimer.start();
    }

    /**
     * Méthode de finalisation de l'affichage de la carte
     * 
     * @param layeredPane - Le JLayeredPane contenant les couches
     * 
     * @since 2.0
     */
    private void finalizeMapDisplay(JLayeredPane layeredPane) {
        this.setContentPane(layeredPane);
        this.refresh();

        if (projectileTimer == null) {
            projectileTimer = new Timer(1000 / FPS, e -> updateProjectiles());
        }
    }

    /**
     * Méthode de mise à jour des projectiles
     * 
     * @see GameModel#updateProjectiles(double)
     * @see JPanel#repaint()
     * @see Worm#update(model.map.GameMap, view.gui.GuiView)
     * 
     * @since 2.0
     */
    private void updateProjectiles() {
        model.updateProjectiles(0.033);
        projectileLayer.repaint();

        if (!model.getProjectiles().isEmpty()) {
            Projectile p = model.getProjectiles().get(0);
            gamePanel.centerCameraOn((int) (p.getX() * MapRenderer.TILE_SIZE),
                    (int) (p.getY() * MapRenderer.TILE_SIZE));
        }

        model.getCurrentWorm().update(model.getMap(), this);

        if (model.getProjectiles().isEmpty() && model.getCurrentWorm().isOnGround(model.getMap())) {
            handleNoMoreProjectiles();
        }
    }

    /**
     * Méthode de gestion de la fin des projectiles
     * 
     * @since 2.0
     */
    private void handleNoMoreProjectiles() {
        projectileTimer.stop();
        ensureWormOnGround();
        enablePlayerControls();
        refreshAllPanels();
    }

    /**
     * Méthode de mmise à jour l'étiquette du minuteur
     * 
     * @since 2.0
     */
    private void updateTimerLabel() {
        turnTimerLabel.setText("Temps: " + timeLeft + "s");
        this.updateTimerLabelColor();
    }

    private void updateTimerLabelColor() {
        if ((timeLeft <= 10) && (timeLeft % 2 == 0)) {
            turnTimerLabel.setForeground(Color.RED);
        } else {
            turnTimerLabel.setForeground(Color.WHITE);
        }
    }

    /**
     * Méthode de mise à jour des particules
     * 
     * @since 2.0
     */
    private void updateParticles() {
        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            p.update();
            if (!p.isAlive()) {
                it.remove();
            }
        }

        if (particles.isEmpty() && particleTimer != null) {
            particleTimer.stop();
        }
    }

    /**
     * Méthode pour arrêter un timer s'il est en cours
     *
     * @param timer - Le Timer à arrêter
     *
     * @since 2.0
     */
    private void stopTimerIfRunning(Timer timer) {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }

    /**
     * Méthode pour s'assurer que le ver est au sol
     *
     * @since 2.0
     */
    private void ensureWormOnGround() {
        Timer fallTimer = new Timer(30, null);
        fallTimer.addActionListener(ev -> {

            boolean someoneStillFalling = false;

            for (var team : new ArrayList<>(model.getTeams())) {
                for (Worm w : new ArrayList<>(team.getWorms())) {

                    if (!w.isOnGround(model.getMap())) {
                        w.update(model.getMap(), this);
                        someoneStillFalling = true;
                    }
                }
            }

            gamePanel.repaint();

            if (!someoneStillFalling) {
                fallTimer.stop();
                handleTurnEnd();
            }
        });
        fallTimer.start();
    }

    /**
     * Méthode de gestion de la fin du jeu
     *
     * @since 2.0
     */
    private void handleGameOver() {
        if (endGamePanel == null) {
            this.endGamePanel = new EndGamePanel(this);
        }
        this.setContentPane(endGamePanel);
        this.refresh();
        stopTimerIfRunning(turnTimer);
    }

    /**
     * Méthode de gestion du début du tour du bot
     *
     * @since 2.0
     */
    private void handleBotTurnStart() {
        disablePlayerControls();
        scheduleDelayedBotTurn(BOT_INITIAL_DELAY_MS);
    }

    /**
     * Méthode de gestion du tour du bot
     *
     * @since 2.0
     */
    private void handleBotTurn() {
        disablePlayerControls();
        scheduleDelayedBotTurn(BOT_DELAY_MS);
    }

    /**
     * Méthode pour programmer un tour de bot avec un délai
     *
     * @param delayMs - Le délai en millisecondes
     *
     * @since 2.0
     */
    private void scheduleDelayedBotTurn(int delayMs) {
        new Timer(delayMs, e -> {
            ((Timer) e.getSource()).stop();
            botController.playTurn(model);
            refresh();
            startTurnTimer();
        }).start();
    }

    /**
     * Méthode de gestion du tour du joueur
     *
     * @since 2.0
     */
    private void handlePlayerTurn() {
        enablePlayerControls();
        refreshAllPanels();
        startTurnTimer();

        SwingUtilities.invokeLater(() -> gamePanel.requestFocusInWindow());
        gamePanel.startMovementTimer();
        inventoryPanel.refresh();
        refresh();
    }

    /**
     * Méthode d'activation des contrôles du joueur
     *
     * @since 2.0
     */
    private void enablePlayerControls() {
        shootController.setCanShoot(true);
        trajectoryLayer.setEnabled(true);
        trajectoryLayer.requestFocusInWindow();
    }

    /**
     * Méthode de désactivation des contrôles du joueur
     *
     * @since 2.0
     */
    private void disablePlayerControls() {
        shootController.setCanShoot(false);
        trajectoryLayer.setEnabled(false);
    }

    /**
     * Méthode de rafraîchissement de tous les panneaux
     *
     * @since 2.0
     */
    private void refreshAllPanels() {
        trajectoryLayer.repaint();
        inventoryPanel.repaint();
        gamePanel.repaint();
    }

    @Override
    public void onWormPlaced(Worm worm, Map map) {
        map.setCell((int) worm.getX(), (int) worm.getY(), ' ');
    }
}
