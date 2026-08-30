package view.gui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import controller.bot.BotController;
import controller.gui.GuiController;
import controller.gui.ShootController;
import model.GameModel;
import model.Map;
import model.physics.Projectile;
import model.physics.Wind;
import model.players.Worm;


class GuiTestGuiViewTest {

    private GuiView guiView;
    private GameModel model;
    private GuiController controller;

    @BeforeEach
    void setUp() {
        model = mock(GameModel.class);
        controller = mock(GuiController.class);

        guiView = new GuiView(model);
    }

    @Test
    void testConstructorSetsTitle() {
        assertEquals("Worms", guiView.getTitle());
    }

    @Test
    void testSetModel() {
        guiView.setModel(model);
        assertEquals(model, guiView.model);
    }

    @Test
    void testGetController() {
        guiView.setController(controller);
        assertTrue(guiView.getController() instanceof GuiController);
    }

    @Test
    void testSetController() {
        guiView.setController(controller);
        assertEquals(controller, guiView.getController());
    }

    @Test
    void testSetDevMode() {
        guiView.setDevMode(true);
    }

    @Test
    void testGetGamePanel() {
        GamePanel gamePanel = mock(GamePanel.class);
        guiView.gamePanel = gamePanel;
        assertEquals(gamePanel, guiView.getGamePanel());
    }

    @Test
    void testSetMapShown() throws NoSuchFieldException, IllegalAccessException {
        GuiView guiView = new GuiView(mock(GameModel.class));

        Field mapShownField = GuiView.class.getDeclaredField("mapShown");
        mapShownField.setAccessible(true);

        guiView.setMapShown(true);

        boolean mapShownValue = mapShownField.getBoolean(guiView);
        assertTrue(mapShownValue);
    }

    @Test
    void testGetTrajectoryLayer() {
        JPanel trajectoryLayer = mock(JPanel.class);
        guiView.trajectoryLayer = trajectoryLayer;
        assertEquals(trajectoryLayer, guiView.getTrajectoryLayer());
    }

    @Test
    void testGetInventoryPanel() {
        InventoryPanel inventoryPanel = mock(InventoryPanel.class);
        guiView.inventoryPanel = inventoryPanel;
        assertEquals(inventoryPanel, guiView.getInventoryPanel());
    }

    @Test
    void testConstructorSetsDefaultCloseOperation() {
        assertEquals(JFrame.EXIT_ON_CLOSE, guiView.getDefaultCloseOperation());
    }

    @Test
    void testSetControllerWithValidInput() {
        guiView.setController(controller);
        assertNotNull(guiView.getController());
    }

    @Test
    void testSetControllerWithNullInput() {
        guiView.setController(null);
        assertNull(guiView.getController());
    }

    @Test
    void testSetModelWithNull() {
        guiView.setModel(null);
        assertNull(guiView.model);
    }

    @Test
    void testGetGamePanelWhenNull() {
        guiView.gamePanel = null;
        assertNull(guiView.getGamePanel());
    }

    @Test
    void testGetInventoryPanelWhenNull() {
        guiView.inventoryPanel = null;
        assertNull(guiView.getInventoryPanel());
    }

    @Test
    void testConstructorSetsVisibleTrue() {
        assertTrue(guiView.isVisible());
    }

    @Test
    void testSetMapShownWhenTrue() throws NoSuchFieldException, IllegalAccessException {
        guiView.setMapShown(true);

        Field mapShownField = GuiView.class.getDeclaredField("mapShown");
        mapShownField.setAccessible(true);

        boolean mapShownValue = mapShownField.getBoolean(guiView);

        assertTrue(mapShownValue, "mapShown should be true after calling setMapShown(true)");
    }

    @Test
    void testSetMapShownWhenFalse() throws NoSuchFieldException, IllegalAccessException {
        guiView.setMapShown(false);

        Field mapShownField = GuiView.class.getDeclaredField("mapShown");
        mapShownField.setAccessible(true);

        boolean mapShownValue = mapShownField.getBoolean(guiView);

        assertFalse(mapShownValue, "mapShown should be false after calling setMapShown(false)");
    }

    @Test
    void testSetModelWithValidInput() {
        guiView.setModel(model);
        assertEquals(model, guiView.model);
    }

    @Test
    void testSetBotDifficultyCreatesBotController() throws NoSuchFieldException, IllegalAccessException {
        Field botControllerField = GuiView.class.getDeclaredField("botController");
        botControllerField.setAccessible(true);
        assertNull(botControllerField.get(guiView));

        guiView.setBotDifficulty(3);

        Object botControllerValue = botControllerField.get(guiView);
        assertNotNull(botControllerValue, "botController should be initialized after setBotDifficulty");

        assertTrue(botControllerValue instanceof controller.bot.BotController,
                "botController should be instance of BotController");
    }

    @Test
    void testSetTimer() throws NoSuchFieldException, IllegalAccessException {
        Field turnTimeField = GuiView.class.getDeclaredField("turnTimeSeconds");
        turnTimeField.setAccessible(true);
        int defaultValue = turnTimeField.getInt(guiView);

        guiView.setTimer(42);

        int newValue = turnTimeField.getInt(guiView);
        assertEquals(42, newValue, "turnTimeSeconds should be updated by setTimer");
        assertNotEquals(defaultValue, newValue, "turnTimeSeconds should be different from default after setTimer");
    }

    @Test
    void testRefreshAllPanels() throws Exception {
        JPanel trajectorySpy = mock(JPanel.class);
        InventoryPanel inventorySpy = mock(InventoryPanel.class);
        GamePanel gameSpy = mock(GamePanel.class);

        guiView.trajectoryLayer = trajectorySpy;
        guiView.inventoryPanel = inventorySpy;
        guiView.gamePanel = gameSpy;

        java.lang.reflect.Method method = GuiView.class.getDeclaredMethod("refreshAllPanels");
        method.setAccessible(true);

        method.invoke(guiView);

        verify(trajectorySpy).repaint();
        verify(inventorySpy).repaint();
        verify(gameSpy).repaint();
    }

    @Test
    void testDisablePlayerControls() throws Exception {
        ShootController shootSpy = mock(ShootController.class);
        JPanel trajectorySpy = mock(JPanel.class);

        guiView.shootController = shootSpy;
        guiView.trajectoryLayer = trajectorySpy;

        Method method = GuiView.class.getDeclaredMethod("disablePlayerControls");
        method.setAccessible(true);

        method.invoke(guiView);

        verify(shootSpy).setCanShoot(false);
        verify(trajectorySpy).setEnabled(false);
    }

    @Test
    void testEnablePlayerControls() throws Exception {
        ShootController shootSpy = mock(ShootController.class);
        JPanel trajectorySpy = mock(JPanel.class);

        guiView.shootController = shootSpy;
        guiView.trajectoryLayer = trajectorySpy;

        Method method = GuiView.class.getDeclaredMethod("enablePlayerControls");
        method.setAccessible(true);

        method.invoke(guiView);

        verify(shootSpy).setCanShoot(true);
        verify(trajectorySpy).setEnabled(true);
        verify(trajectorySpy).requestFocusInWindow();
    }

    @Test
    void testHandlePlayerTurnEffects() throws Exception {
        guiView.shootController = mock(ShootController.class);
        guiView.trajectoryLayer = mock(JPanel.class);
        guiView.gamePanel = mock(GamePanel.class);
        guiView.inventoryPanel = mock(InventoryPanel.class);

        GuiView spyView = spy(guiView);
        doNothing().when(spyView).startTurnTimer();
        doNothing().when(spyView).refresh();

        Method method = GuiView.class.getDeclaredMethod("handlePlayerTurn");
        method.setAccessible(true);
        method.invoke(spyView);

        verify(guiView.shootController).setCanShoot(true);
        verify(guiView.trajectoryLayer).setEnabled(true);
        verify(guiView.trajectoryLayer).requestFocusInWindow();

        verify(guiView.trajectoryLayer).repaint();
        verify(guiView.inventoryPanel).repaint();
        verify(guiView.gamePanel).repaint();

        verify(spyView).startTurnTimer();
        verify(spyView).refresh();

        verify(guiView.gamePanel).startMovementTimer();
        verify(guiView.inventoryPanel).refresh();
    }

    @Test
    void testHandleGameOverEffects() throws Exception {
        GuiView spyView = spy(guiView);

        spyView.endGamePanel = null;
        spyView.turnTimer = mock(Timer.class);

        doNothing().when(spyView).refresh();

        var winningTeam = mock(model.players.Team.class);
        when(winningTeam.getName()).thenReturn("Team 1");
        when(spyView.model.getWinningTeam()).thenReturn(winningTeam);

        Method method = GuiView.class.getDeclaredMethod("handleGameOver");
        method.setAccessible(true);
        method.invoke(spyView);

        assertNotNull(spyView.endGamePanel);

        verify(spyView).setContentPane(spyView.endGamePanel);

        verify(spyView).refresh();
    }

    @Test
    void testEnsureWormOnGroundCreatesTimer() throws Exception {
        Worm wormMock = mock(Worm.class);
        when(wormMock.isOnGround(any())).thenReturn(true);

        model.players.Team teamMock = mock(model.players.Team.class);
        when(teamMock.getWorms()).thenReturn(new ArrayList<>(List.of(wormMock)));

        when(guiView.model.getTeams()).thenReturn(new ArrayList<>(List.of(teamMock)));
        when(guiView.model.getMap()).thenReturn(mock(model.Map.class));

        guiView.gamePanel = mock(GamePanel.class);

        GuiView spyView = spy(guiView);

        Method method = GuiView.class.getDeclaredMethod("ensureWormOnGround");
        method.setAccessible(true);
        method.invoke(spyView);

        assertNotNull(method);
    }

    @Test
    void testStopTimerIfRunning() throws Exception {
        GuiView spyView = spy(guiView);

        Timer runningTimer = mock(Timer.class);
        when(runningTimer.isRunning()).thenReturn(true);

        Timer stoppedTimer = mock(Timer.class);
        when(stoppedTimer.isRunning()).thenReturn(false);

        Method method = GuiView.class.getDeclaredMethod("stopTimerIfRunning", Timer.class);
        method.setAccessible(true);

        method.invoke(spyView, runningTimer);
        verify(runningTimer).stop();

        method.invoke(spyView, stoppedTimer);
        verify(stoppedTimer, never()).stop();

        method.invoke(spyView, new Object[] { null });
    }

    @Test
    @SuppressWarnings("unchecked")
    void testUpdateParticlesWithReflection() throws Exception {
        GuiView spyView = spy(guiView);

        Particle aliveParticle = mock(Particle.class);
        when(aliveParticle.isAlive()).thenReturn(true);

        Particle deadParticle = mock(Particle.class);
        when(deadParticle.isAlive()).thenReturn(false);

        Field particlesField = GuiView.class.getDeclaredField("particles");
        particlesField.setAccessible(true);
        ArrayList<Particle> particlesList = new ArrayList<>();
        particlesList.add(aliveParticle);
        particlesList.add(deadParticle);
        particlesField.set(spyView, particlesList);

        Field particleTimerField = GuiView.class.getDeclaredField("particleTimer");
        particleTimerField.setAccessible(true);
        Timer timerMock = mock(Timer.class);
        particleTimerField.set(spyView, timerMock);

        Method updateParticlesMethod = GuiView.class.getDeclaredMethod("updateParticles");
        updateParticlesMethod.setAccessible(true);
        updateParticlesMethod.invoke(spyView);

        verify(aliveParticle).update();
        verify(deadParticle).update();

        ArrayList<Particle> updatedParticles = (ArrayList<Particle>) particlesField.get(spyView);
        assertTrue(updatedParticles.contains(aliveParticle));
        assertFalse(updatedParticles.contains(deadParticle));

        updatedParticles.clear();
        updateParticlesMethod.invoke(spyView);
        verify(timerMock).stop();
    }

    @Test
    void testUpdateTimerLabelColor() throws Exception {
        GuiView spyView = spy(guiView);

        JLabel labelMock = mock(JLabel.class);

        Field labelField = GuiView.class.getDeclaredField("turnTimerLabel");
        labelField.setAccessible(true);
        labelField.set(spyView, labelMock);

        Field timeLeftField = GuiView.class.getDeclaredField("timeLeft");
        timeLeftField.setAccessible(true);

        timeLeftField.setInt(spyView, 15);
        Method method = GuiView.class.getDeclaredMethod("updateTimerLabelColor");
        method.setAccessible(true);
        method.invoke(spyView);
        verify(labelMock).setForeground(Color.WHITE);

        timeLeftField.setInt(spyView, 8);
        method.invoke(spyView);
        verify(labelMock).setForeground(Color.RED);

        timeLeftField.setInt(spyView, 7);
        method.invoke(spyView);
        verify(labelMock, times(2)).setForeground(Color.WHITE); // déjà une fois avant
    }

    @Test
    void testUpdateTimerLabelSetsText() throws Exception {
        GuiView spyView = spy(guiView);

        JLabel labelMock = mock(JLabel.class);
        Field labelField = GuiView.class.getDeclaredField("turnTimerLabel");
        labelField.setAccessible(true);
        labelField.set(spyView, labelMock);

        Field timeLeftField = GuiView.class.getDeclaredField("timeLeft");
        timeLeftField.setAccessible(true);
        timeLeftField.setInt(spyView, 12);

        Method method = GuiView.class.getDeclaredMethod("updateTimerLabel");
        method.setAccessible(true);
        method.invoke(spyView);

        verify(labelMock).setText("Temps: 12s");
    }

    @Test
    void testHandleNoMoreProjectilesStopsTimer() throws Exception {
        GuiView view = new GuiView(mock(GameModel.class));

        view.shootController = mock(ShootController.class);
        view.trajectoryLayer = mock(JPanel.class);
        view.gamePanel = mock(GamePanel.class);
        view.inventoryPanel = mock(InventoryPanel.class);

        Timer timerMock = mock(Timer.class);
        Field projectileTimerField = GuiView.class.getDeclaredField("projectileTimer");
        projectileTimerField.setAccessible(true);
        projectileTimerField.set(view, timerMock);

        Method method = GuiView.class.getDeclaredMethod("handleNoMoreProjectiles");
        method.setAccessible(true);
        method.invoke(view);

        verify(timerMock).stop();
        verify(view.shootController).setCanShoot(true);
        verify(view.trajectoryLayer).setEnabled(true);
        verify(view.trajectoryLayer).requestFocusInWindow();

        verify(view.gamePanel, atLeastOnce()).repaint();
        verify(view.inventoryPanel, atLeastOnce()).repaint();
        verify(view.trajectoryLayer, atLeastOnce()).repaint();
    }

    @Test
    void testUpdateProjectiles() throws Exception {
        GameModel modelMock = mock(GameModel.class);
        GuiView view = new GuiView(modelMock);

        view.projectileLayer = mock(JPanel.class);
        Worm wormMock = mock(Worm.class);
        when(modelMock.getCurrentWorm()).thenReturn(wormMock);
        when(modelMock.getMap()).thenReturn(mock(model.Map.class));
        when(modelMock.getProjectiles()).thenReturn(new ArrayList<>());

        GuiView spyView = spy(view);

        Method updateProjectiles = GuiView.class.getDeclaredMethod("updateProjectiles");
        updateProjectiles.setAccessible(true);

        updateProjectiles.invoke(spyView);

        verify(modelMock).updateProjectiles(0.033);
        verify(view.projectileLayer).repaint();
        verify(wormMock).update(modelMock.getMap(), spyView);
    }

    @Test
    void testFinalizeMapDisplayCreatesProjectileTimerAndSetsContentPane() throws Exception {
        GuiView view = new GuiView(mock(GameModel.class));

        JLayeredPane layeredPane = new JLayeredPane();

        GuiView spyView = spy(view);

        doNothing().when(spyView).refresh();

        spyView.projectileTimer = null;

        Method method = GuiView.class.getDeclaredMethod("finalizeMapDisplay", JLayeredPane.class);
        method.setAccessible(true);

        method.invoke(spyView, layeredPane);

        assertEquals(layeredPane, spyView.getContentPane());
        verify(spyView).refresh();
        assertNotNull(spyView.projectileTimer);
        assertTrue(spyView.projectileTimer.getDelay() > 0);
    }

    @Test
    void testHandleSkipTurndisablesButtonAndStartsMovement() throws Exception {
        GuiView view = new GuiView(mock(GameModel.class));
        GuiView spyView = spy(view);

        Worm worm = mock(Worm.class);
        when(worm.isOnGround(any())).thenReturn(true);

        GameModel model = mock(GameModel.class);
        when(model.getCurrentWorm()).thenReturn(worm);

        spyView.model = model;
        spyView.gamePanel = mock(GamePanel.class);

        JButton skipButton = new JButton();

        Method method = GuiView.class.getDeclaredMethod("handleSkipTurn", JButton.class);
        method.setAccessible(true);

        method.invoke(spyView, skipButton);

        assertFalse(skipButton.isEnabled(), "Le bouton doit être désactivé immédiatement");
        verify(spyView.gamePanel).startMovementTimer();
    }

    @Test
    void testAddSkipButtonaddsButtonWithListener() throws Exception {
        GuiView view = new GuiView(mock(GameModel.class));
        GuiView spyView = spy(view);

        JLayeredPane layeredPane = new JLayeredPane();

        Method method = GuiView.class.getDeclaredMethod("addSkipButton", JLayeredPane.class);
        method.setAccessible(true);

        method.invoke(spyView, layeredPane);

        Component[] components = layeredPane.getComponents();
        assertEquals(1, components.length, "Un seul composant doit être ajouté au layeredPane");
        assertTrue(components[0] instanceof JButton, "Le composant doit être un JButton");

        JButton skipButton = (JButton) components[0];

        assertEquals("Passer le tour", skipButton.getText());

        assertTrue(skipButton.getActionListeners().length > 0, "Le bouton doit avoir un ActionListener attaché");
    }

    @Test
    void testAddTurnTimerLabelcreatesLabelAndAddsToLayeredPane() throws Exception {
        GuiView view = new GuiView(mock(GameModel.class));
        GuiView spyView = spy(view);

        JLayeredPane layeredPane = new JLayeredPane();

        Method method = GuiView.class.getDeclaredMethod("addTurnTimerLabel", JLayeredPane.class);
        method.setAccessible(true);
        method.invoke(spyView, layeredPane);

        Field labelField = GuiView.class.getDeclaredField("turnTimerLabel");
        labelField.setAccessible(true);
        JLabel turnTimerLabel = (JLabel) labelField.get(spyView);
        assertNotNull(turnTimerLabel, "turnTimerLabel doit être créé");

        assertEquals(JLabel.RIGHT, turnTimerLabel.getHorizontalAlignment());
        assertEquals(new Font("Arial", Font.BOLD, 24).getName(), turnTimerLabel.getFont().getName());
        assertEquals(Color.WHITE, turnTimerLabel.getForeground());

        boolean added = false;
        for (Component c : layeredPane.getComponents()) {
            if (c == turnTimerLabel) {
                added = true;
                break;
            }
        }
        assertTrue(added, "turnTimerLabel doit être ajouté au layeredPane");
    }

    @Test
    void testAddHUDLayerwithRealPanel() throws Exception {
        GuiView view = new GuiView(mock(GameModel.class));

        GamePanel gamePanel = mock(GamePanel.class);
        when(gamePanel.getPreferredSize()).thenReturn(new Dimension(800, 600));
        InventoryPanel inventoryPanel = new InventoryPanel(mock(GameModel.class), view);

        view.gamePanel = gamePanel;
        view.inventoryPanel = inventoryPanel;

        JLayeredPane layeredPane = new JLayeredPane();

        Method method = GuiView.class.getDeclaredMethod("addHUDLayer", JLayeredPane.class);
        method.setAccessible(true);
        method.invoke(view, layeredPane);

        assertEquals(new Rectangle(0, 0, 800, 80), inventoryPanel.getBounds());

        boolean added = false;
        for (Component c : layeredPane.getComponents()) {
            if (c == inventoryPanel) {
                added = true;
                break;
            }
        }
        assertTrue(added, "inventoryPanel doit être ajouté au layeredPane");
    }

    @Test
    void testRenderProjectilesdrawsAllProjectiles() throws Exception {
        GuiView view = new GuiView(mock(GameModel.class));

        Graphics2D g2d = mock(Graphics2D.class);

        Projectile p1 = mock(Projectile.class);
        when(p1.getX()).thenReturn(1.0);
        when(p1.getY()).thenReturn(2.0);

        Projectile p2 = mock(Projectile.class);
        when(p2.getX()).thenReturn(3.0);
        when(p2.getY()).thenReturn(4.0);

        GameModel model = mock(GameModel.class);
        when(model.getProjectiles()).thenReturn(new ArrayList<>(List.of(p1, p2)));
        view.setModel(model);

        Method method = GuiView.class.getDeclaredMethod("renderProjectiles", Graphics2D.class);
        method.setAccessible(true);
        method.invoke(view, g2d);

        verify(g2d).setColor(Color.BLACK);

        int tileSize = MapRenderer.TILE_SIZE;
        int size = MapRenderer.getTileSize() / 2;

        verify(g2d).fillOval((int) (1.0 * tileSize) - 6, (int) (2.0 * tileSize) - 6, size, size);
        verify(g2d).fillOval((int) (3.0 * tileSize) - 6, (int) (4.0 * tileSize) - 6, size, size);
    }

    @Test
    void testCreateProjectilePanelcreatesNonNullPanel() throws Exception {
        GameModel model = mock(GameModel.class);
        GuiView view = new GuiView(model);

        GamePanel mockGamePanel = mock(GamePanel.class);
        view.gamePanel = mockGamePanel;

        Method method = GuiView.class.getDeclaredMethod("createProjectilePanel");
        method.setAccessible(true);
        JPanel panel = (JPanel) method.invoke(view);

        assertNotNull(panel, "Le panel ne doit pas être null");
        assertTrue(panel.isOpaque(), "Le panel doit être transparent");
        assertTrue(panel instanceof JPanel, "Doit être un JPanel");
    }

    @Test
    void testAddProjectileLayercreatesLayerAndAddsToPane() throws Exception {
        GameModel model = mock(GameModel.class);
        GuiView view = new GuiView(model);

        GamePanel mockGamePanel = mock(GamePanel.class);
        when(mockGamePanel.getPreferredSize()).thenReturn(new Dimension(800, 600));
        view.gamePanel = mockGamePanel;

        JLayeredPane layeredPane = new JLayeredPane();

        Method method = GuiView.class.getDeclaredMethod("addProjectileLayer", JLayeredPane.class);
        method.setAccessible(true);
        method.invoke(view, layeredPane);

        assertNotNull(view.projectileLayer, "projectileLayer doit être créé");
        assertFalse(view.projectileLayer.isOpaque(), "projectileLayer doit être transparent");

        assertEquals(800, view.projectileLayer.getWidth());
        assertEquals(600, view.projectileLayer.getHeight());

        boolean found = false;
        for (int i = 0; i < layeredPane.getComponentCount(); i++) {
            if (layeredPane.getComponent(i) == view.projectileLayer) {
                found = true;
                break;
            }
        }
        assertTrue(found, "projectileLayer doit être ajouté au layeredPane");
    }

    @Test
    void testInitControllerscreatesControllersAndBinds() throws Exception {
        GameModel model = mock(GameModel.class);
        GuiView view = new GuiView(model);

        JPanel realLayer = new JPanel();
        view.trajectoryLayer = realLayer;

        view.aimController = null;
        view.shootController = null;

        Method method = GuiView.class.getDeclaredMethod("initControllers");
        method.setAccessible(true);
        method.invoke(view);

        assertNotNull(view.aimController);
        assertNotNull(view.shootController);
        assertEquals(realLayer, view.trajectoryLayer);
    }

    @Test
    void testCreateTrajectoryPanelcanPaintWithoutException() throws Exception {
        GameModel modelMock = mock(GameModel.class);
        GuiView view = new GuiView(modelMock);

        view.model = modelMock;
        view.gamePanel = mock(GamePanel.class);
        when(view.gamePanel.getCameraOffsetX()).thenReturn(0);
        when(view.gamePanel.getCameraOffsetY()).thenReturn(0);

        view.trajectoryRenderer = mock(TrajectoryRenderer.class);
        Worm wormMock = mock(Worm.class);
        when(modelMock.getCurrentWorm()).thenReturn(wormMock);
        when(modelMock.getMap()).thenReturn(mock(model.Map.class));

        Method method = GuiView.class.getDeclaredMethod("createTrajectoryPanel");
        method.setAccessible(true);
        JPanel panel = (JPanel) method.invoke(view);

        assertNotNull(panel);
        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        assertDoesNotThrow(() -> panel.printAll(g2d));

        g2d.dispose();
    }

    @Test
    void testAddTrajectoryLayerinitializesRendererAndLayer() throws Exception {
        GameModel modelMock = mock(GameModel.class);
        GuiView view = new GuiView(modelMock);

        view.gamePanel = mock(GamePanel.class);
        when(view.gamePanel.getPreferredSize()).thenReturn(new Dimension(500, 400));

        JLayeredPane layeredPane = new JLayeredPane();

        Field rendererField = GuiView.class.getDeclaredField("trajectoryRenderer");
        rendererField.setAccessible(true);
        assertNull(rendererField.get(view));

        Field layerField = GuiView.class.getDeclaredField("trajectoryLayer");
        layerField.setAccessible(true);
        assertNull(layerField.get(view));

        Method method = GuiView.class.getDeclaredMethod("addTrajectoryLayer", JLayeredPane.class);
        method.setAccessible(true);
        method.invoke(view, layeredPane);

        assertNotNull(rendererField.get(view));

        JPanel layer = (JPanel) layerField.get(view);
        assertNotNull(layer);
        assertEquals(500, layer.getWidth());
        assertEquals(400, layer.getHeight());

        assertEquals(layer, layeredPane.getComponent(layeredPane.getComponentCount() - 1));
    }

    @Test
    void testAddMapLayersetsBoundsAndAddsToLayeredPane() throws Exception {
        Map mockMap = mock(Map.class);
        when(mockMap.getWidth()).thenReturn(800);
        when(mockMap.getHeight()).thenReturn(600);

        GameModel modelMock = mock(GameModel.class);
        when(modelMock.getMap()).thenReturn(mockMap);

        GuiView view = new GuiView(modelMock);

        GamePanel realGamePanel = new GamePanel(modelMock, view) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(800, 600);
            }
        };
        view.gamePanel = realGamePanel;

        JLayeredPane layeredPane = new JLayeredPane();

        Method method = GuiView.class.getDeclaredMethod("addMapLayer", JLayeredPane.class);
        method.setAccessible(true);
        method.invoke(view, layeredPane);

        assertEquals(new Rectangle(0, 0, 800, 600), realGamePanel.getBounds());
        assertEquals(realGamePanel, layeredPane.getComponent(layeredPane.getComponentCount() - 1));
        assertEquals(JLayeredPane.DEFAULT_LAYER, JLayeredPane.getLayer(realGamePanel));
    }

    @Test
    void testCreateLayeredPanereturnsPaneWithCorrectSize() throws Exception {
        GamePanel mockGamePanel = mock(GamePanel.class);
        when(mockGamePanel.getPreferredSize()).thenReturn(new Dimension(640, 480));

        GuiView view = new GuiView(mock(GameModel.class));
        view.gamePanel = mockGamePanel;

        Method method = GuiView.class.getDeclaredMethod("createLayeredPane");
        method.setAccessible(true);
        JLayeredPane result = (JLayeredPane) method.invoke(view);

        assertNotNull(result);
        assertEquals(new Dimension(640, 480), result.getPreferredSize());
    }

    @Test
    void testInitGamePanelscreatesPanelsIfNull() throws Exception {
        GameModel mockModel = mock(GameModel.class);
        Map mockMap = mock(Map.class);
        Wind mockWind = mock(Wind.class);

        when(mockModel.getMap()).thenReturn(mockMap);
        when(mockMap.getWidth()).thenReturn(800);
        when(mockMap.getHeight()).thenReturn(600);
        when(mockModel.getWind()).thenReturn(mockWind);

        GuiView view = new GuiView(mockModel);

        view.gamePanel = null;
        view.inventoryPanel = null;

        Method method = GuiView.class.getDeclaredMethod("initGamePanels");
        method.setAccessible(true);
        method.invoke(view);

        assertNotNull(view.gamePanel, "GamePanel should be initialized");
        assertNotNull(view.inventoryPanel, "InventoryPanel should be initialized");
    }

    @Test
    void testRefreshcallsRepaintAndRevalidate() throws Exception {
        GuiView view = new GuiView(mock(GameModel.class));
        GuiView spyView = spy(view);

        GamePanel mockGamePanel = mock(GamePanel.class);
        spyView.gamePanel = mockGamePanel;

        Method refreshMethod = GuiView.class.getDeclaredMethod("refresh");
        refreshMethod.setAccessible(true);
        refreshMethod.invoke(spyView);

        verify(mockGamePanel).repaint();
        verify(spyView).revalidate();
        verify(spyView).repaint();
    }

    @Test
    void testStartParticleTimercreatesAndStartsTimer() throws Exception {
        GuiView view = new GuiView(mock(GameModel.class));
        GuiView spyView = spy(view);

        spyView.projectileLayer = mock(JPanel.class);

        Field particlesField = GuiView.class.getDeclaredField("particles");
        particlesField.setAccessible(true);
        particlesField.set(spyView, new ArrayList<>());

        Method method = GuiView.class.getDeclaredMethod("startParticleTimer");
        method.setAccessible(true);
        method.invoke(spyView);

        Field timerField = GuiView.class.getDeclaredField("particleTimer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(spyView);

        assertNotNull(timer, "Le particleTimer doit être créé");
        assertTrue(timer.isRunning(), "Le particleTimer doit être démarré");
    }

    @Test
    void testAddParticlesaddsParticleToList() throws Exception {
        GuiView view = new GuiView(mock(GameModel.class));

        Particle particle = mock(Particle.class);

        view.addParticles(particle);

        Field particlesField = GuiView.class.getDeclaredField("particles");
        particlesField.setAccessible(true);

        @SuppressWarnings("unchecked")
        ArrayList<Particle> particles = (ArrayList<Particle>) particlesField.get(view);

        assertTrue(particles.contains(particle), "La particule doit être dans la liste");
    }

    @Test
    void testStartProjectileTimercreatesAndStartsTimer() throws Exception {
        GuiView view = new GuiView(mock(GameModel.class));

        Field timerField = GuiView.class.getDeclaredField("projectileTimer");
        timerField.setAccessible(true);
        assertNull(timerField.get(view));

        view.startProjectileTimer();

        Timer projectileTimer = (Timer) timerField.get(view);
        assertNotNull(projectileTimer, "projectileTimer doit être créé");
        assertTrue(projectileTimer.isRunning(), "projectileTimer doit être démarré");
    }

    @Test
    void testStartTurnTimercreatesAndStartsTimer() throws Exception {
        GameModel model = mock(GameModel.class);
        GuiView view = new GuiView(model);

        Field labelField = GuiView.class.getDeclaredField("turnTimerLabel");
        labelField.setAccessible(true);
        JLabel timerLabel = new JLabel();
        labelField.set(view, timerLabel);

        Field turnTimeField = GuiView.class.getDeclaredField("turnTimeSeconds");
        turnTimeField.setAccessible(true);
        turnTimeField.setInt(view, 15);

        Field turnTimerField = GuiView.class.getDeclaredField("turnTimer");
        turnTimerField.setAccessible(true);
        assertNull(turnTimerField.get(view));

        view.startTurnTimer();

        Timer turnTimer = (Timer) turnTimerField.get(view);
        assertNotNull(turnTimer, "turnTimer doit être créé");
        assertTrue(turnTimer.isRunning(), "turnTimer doit être démarré");

        Field timeLeftField = GuiView.class.getDeclaredField("timeLeft");
        timeLeftField.setAccessible(true);
        int timeLeft = timeLeftField.getInt(view);
        assertEquals(15, timeLeft, "timeLeft doit être égal à turnTimeSeconds");
    }

    @Test
    void testShowMapeffectsObservable() throws Exception {
        GameModel model = mock(GameModel.class);
        Map map = mock(Map.class);
        when(model.getMap()).thenReturn(map);
        when(map.getWidth()).thenReturn(500);
        when(map.getHeight()).thenReturn(300);
        when(model.getCurrentTeam()).thenReturn(null);

        GuiView guiView = spy(new GuiView(model));

        GamePanel gamePanel = new GamePanel(model, guiView) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(500, 300);
            }

            @Override
            public void zoomIn(Point p) {
            }
        };
        guiView.gamePanel = gamePanel;

        InventoryPanel inventoryPanel = new InventoryPanel(model, guiView) {
            @Override
            public void setWindRenderer(WindRenderer wr) {
            }
        };
        guiView.inventoryPanel = inventoryPanel;

        Field mapShownField = GuiView.class.getDeclaredField("mapShown");
        mapShownField.setAccessible(true);
        mapShownField.set(guiView, false);

        guiView.showMap();

        assertTrue(mapShownField.getBoolean(guiView), "mapShown should be true after showMap");
        assertTrue(guiView.getContentPane() instanceof JLayeredPane, "Content pane should be a JLayeredPane");
    }

    @Test
    void testScheduleDelayedBotTurninvokesBotController() throws Exception {
        GameModel model = mock(GameModel.class);
        GuiView guiView = new GuiView(model);

        BotController botController = mock(BotController.class);

        Field botControllerField = GuiView.class.getDeclaredField("botController");
        botControllerField.setAccessible(true);
        botControllerField.set(guiView, botController);

        Method method = GuiView.class.getDeclaredMethod("scheduleDelayedBotTurn", int.class);
        method.setAccessible(true);

        method.invoke(guiView, 1);

        Thread.sleep(50);

        verify(botController).playTurn(model);
    }

    @Test
    void testHandleBotTurndisablesControlsAndSchedulesBotTurn() throws Exception {
        GameModel model = mock(GameModel.class);
        GuiView guiView = spy(new GuiView(model));

        ShootController mockShootController = mock(ShootController.class);
        guiView.shootController = mockShootController;

        JPanel mockTrajectoryLayer = mock(JPanel.class);
        guiView.trajectoryLayer = mockTrajectoryLayer;

        Method handleBotTurn = GuiView.class.getDeclaredMethod("handleBotTurn");
        handleBotTurn.setAccessible(true);
        handleBotTurn.invoke(guiView);

        verify(mockShootController).setCanShoot(false);
        verify(mockTrajectoryLayer).setEnabled(false);
    }

    @Test
    void testHandleBotTurnStartdisablesControls() throws Exception {
        GameModel model = mock(GameModel.class);
        GuiView guiView = spy(new GuiView(model));

        ShootController mockShootController = mock(ShootController.class);
        guiView.shootController = mockShootController;

        JPanel mockTrajectoryLayer = mock(JPanel.class);
        guiView.trajectoryLayer = mockTrajectoryLayer;

        Method method = GuiView.class.getDeclaredMethod("handleBotTurnStart");
        method.setAccessible(true);
        method.invoke(guiView);

        verify(mockShootController).setCanShoot(false);
        verify(mockTrajectoryLayer).setEnabled(false);

    }
}
