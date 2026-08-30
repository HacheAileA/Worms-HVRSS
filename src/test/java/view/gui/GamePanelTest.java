package view.gui;

import model.GameModel;
import model.Map;
import model.items.crates.CrateManager;
import model.players.Worm;
import model.physics.Projectile;
import controller.gui.InputController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Color;
import java.util.ArrayList;
import java.lang.reflect.Field;
import java.awt.image.BufferedImage;
import javax.swing.Timer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GamePanelTest {

    private GameModel model;
    private GuiView view;
    private Map map;
    private GamePanel panel;

    @BeforeEach
    public void setup() {
        model = mock(GameModel.class);
        view = mock(GuiView.class);
        map = mock(Map.class);

        when(model.getMap()).thenReturn(map);
        when(map.getWidth()).thenReturn(10);
        when(map.getHeight()).thenReturn(10);

        CrateManager crateManager = mock(CrateManager.class);
        when(model.getCrateManager()).thenReturn(crateManager);

        when(model.getProjectiles()).thenReturn(new ArrayList<>());

        panel = new GamePanel(model, view);

        panel.stopMovementTimer();
    }

    @Test
    public void testGetInputController() {
        InputController controller = panel.getInputController();
        assertNotNull(controller);
    }

    @Test
    public void testCameraOffsetsInitiallyZero() {
        assertEquals(0, panel.getCameraOffsetX());
        assertEquals(0, panel.getCameraOffsetY());
    }

    @Test
    public void testStopAndStartMovementTimer() {
        panel.stopMovementTimer();
        panel.startMovementTimer();
    }

    @Test
    public void testSetParticlesList() throws NoSuchFieldException, IllegalAccessException {
        ArrayList<Particle> particles = new ArrayList<>();
        panel.setParticlesList(particles);

        Field particlesField = GamePanel.class.getDeclaredField("particles");
        particlesField.setAccessible(true);
        Object value = particlesField.get(panel);

        assertEquals(particles, value);
    }

    @Test
    public void testPaintComponentWithParticlesAndProjectiles() {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        when(map.getMapType()).thenReturn("TEST_MAP");

        Projectile p = mock(Projectile.class);
        when(p.getX()).thenReturn(1.0);
        when(p.getY()).thenReturn(2.0);
        when(model.getProjectiles()).thenReturn(new ArrayList<>() {
            {
                add(p);
            }
        });

        ArrayList<Particle> particles = new ArrayList<>();
        Particle particle = new Particle(5, 5, 0, 0, Color.RED, 10);
        particles.add(particle);
        panel.setParticlesList(particles);

        panel.paintComponent(g2d);

        g2d.dispose();
    }

    @Test
    public void testZoomInAndOut() {
        Point mouse = new Point(50, 50);
        MapRenderer.setTileSize(16);

        panel.zoomIn(mouse);
        assertTrue(MapRenderer.getTileSize() > 16);

        panel.zoomOut(mouse);
        assertTrue(MapRenderer.getTileSize() <= MapRenderer.getTileSize());
    }

    @Test
    public void testMoveCameraLimits() {
        panel.moveCamera(1000, 1000);
        assertTrue(panel.getCameraOffsetX() <= 0);
        assertTrue(panel.getCameraOffsetY() <= 0);

        panel.moveCamera(-1000, -1000);
        assertTrue(panel.getCameraOffsetX() >= panel.getWidth() - map.getWidth() * MapRenderer.TILE_SIZE);
        assertTrue(panel.getCameraOffsetY() >= panel.getHeight() - map.getHeight() * MapRenderer.TILE_SIZE);
    }

    @Test
    public void testCenterCameraOnPoint() {
        panel.centerCameraOn(50, 50);
        assertEquals(panel.getWidth() / 2 - 50, panel.getCameraOffsetX());
        assertEquals(panel.getHeight() / 2 - 50, panel.getCameraOffsetY());
    }

    @Test
    public void testCenterCameraOnCurrentWormWithNull() {
        when(model.getCurrentWorm()).thenReturn(null);
        panel.centerCameraOnCurrentWorm();
    }

    @Test
    public void testCenterCameraOnCurrentWormWithWorm() {
        Worm worm = mock(Worm.class);
        when(worm.getX()).thenReturn(5.0);
        when(worm.getY()).thenReturn(7.0);
        when(model.getCurrentWorm()).thenReturn(worm);

        panel.centerCameraOnCurrentWorm();
        int expectedX = panel.getWidth() / 2 - 5 * MapRenderer.TILE_SIZE;
        int expectedY = panel.getHeight() / 2 - 7 * MapRenderer.TILE_SIZE;

        assertEquals(expectedX, panel.getCameraOffsetX());
        assertEquals(expectedY, panel.getCameraOffsetY());
    }

    @Test
    public void testTimerWithNullMouse() throws Exception {
        when(model.getCurrentWorm()).thenReturn(null);
        when(view.getMousePosition()).thenReturn(null);

        Field timerField = GamePanel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(panel);

        timer.getActionListeners()[0].actionPerformed(null);
    }

    @Test
    public void testLimitCameraCentersSmallMap() {
        when(map.getWidth()).thenReturn(1);
        when(map.getHeight()).thenReturn(1);

        panel.setSize(500, 500);
        MapRenderer.setTileSize(16);

        panel.moveCamera(100, 100);

        assertEquals((500 - 16) / 2, panel.getCameraOffsetX());
        assertEquals((500 - 16) / 2, panel.getCameraOffsetY());
    }

    @Test
    public void testMoveCameraZeroDelta() {
        panel.moveCamera(0, 0);
        assertEquals(0, panel.getCameraOffsetX());
        assertEquals(0, panel.getCameraOffsetY());
    }

    @Test
    public void testTimerWithWormAndMouseMovement() throws Exception {
        panel.setSize(500, 500);
        Worm worm = mock(Worm.class);
        when(model.getCurrentWorm()).thenReturn(worm);
        when(view.getMousePosition()).thenReturn(new Point(0, 0));
        when(map.getMapType()).thenReturn("TEST");
        Field timerField = GamePanel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(panel);
        timer.getActionListeners()[0].actionPerformed(null);
        assertTrue(panel.getCameraOffsetX() != 0 || panel.getCameraOffsetY() != 0);
    }

    @Test
    public void testPaintComponentWithoutCrateRenderer() throws Exception {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        when(map.getMapType()).thenReturn("TEST");

        Field crateField = GamePanel.class.getDeclaredField("crateRenderer");
        crateField.setAccessible(true);
        crateField.set(panel, null);

        panel.paintComponent(g2d);

        g2d.dispose();
    }

    @Test
    public void testTimerWithMouseBottomRight() throws Exception {
        panel.setSize(500, 500);

        when(model.getCurrentWorm()).thenReturn(mock(Worm.class));
        when(view.getMousePosition()).thenReturn(new Point(499, 499));
        when(map.getMapType()).thenReturn("TEST");

        Field timerField = GamePanel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(panel);

        timer.getActionListeners()[0].actionPerformed(null);

        assertTrue(panel.getCameraOffsetX() != 0 || panel.getCameraOffsetY() != 0);
    }

    @Test
    public void testTimerWithMouseCenteredNoMovement() throws Exception {
        panel.setSize(500, 500);

        when(model.getCurrentWorm()).thenReturn(mock(Worm.class));
        when(view.getMousePosition()).thenReturn(new Point(250, 250));
        when(map.getMapType()).thenReturn("TEST");

        Field timerField = GamePanel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(panel);

        timer.getActionListeners()[0].actionPerformed(null);

        assertEquals(0, panel.getCameraOffsetX());
        assertEquals(0, panel.getCameraOffsetY());
    }

    @Test
    public void testZoomOutWidthDominantMinimum() {
        panel.setSize(300, 100);
        when(map.getWidth()).thenReturn(5);
        when(map.getHeight()).thenReturn(50);

        MapRenderer.setTileSize(20);

        panel.zoomOut(new Point(10, 10));

        assertTrue(MapRenderer.getTileSize() >= 8);
    }

    @Test
    public void testZoomOutHeightDominantMinimum() {
        panel.setSize(100, 300);
        when(map.getWidth()).thenReturn(50);
        when(map.getHeight()).thenReturn(5);

        MapRenderer.setTileSize(20);

        panel.zoomOut(new Point(10, 10));

        assertTrue(MapRenderer.getTileSize() >= 8);
    }

    @Test
    public void testLimitCameraWidthSmallerOnly() {
        when(map.getWidth()).thenReturn(1);
        when(map.getHeight()).thenReturn(100);

        panel.setSize(500, 200);
        MapRenderer.setTileSize(16);

        panel.moveCamera(50, 50);

        assertEquals((500 - 16) / 2, panel.getCameraOffsetX());
    }

    @Test
    public void testLimitCameraHeightSmallerOnly() {
        when(map.getWidth()).thenReturn(100);
        when(map.getHeight()).thenReturn(1);

        panel.setSize(200, 500);
        MapRenderer.setTileSize(16);

        panel.moveCamera(50, 50);

        assertEquals((500 - 16) / 2, panel.getCameraOffsetY());
    }

}
