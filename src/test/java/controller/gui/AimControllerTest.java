package controller.gui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.event.ActionEvent;
import javax.swing.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.GameModel;
import model.players.Team;
import model.players.Worm;

class AimControllerTest {

    private GameModel model;
    private Worm worm;
    private AimController controller;

    @BeforeEach
    void setup() {
        model = mock(GameModel.class);
        Team team = mock(Team.class);
        worm = new Worm(team, "Worm", '0');

        when(model.getCurrentWorm()).thenReturn(worm);

        controller = new AimController(model);
    }

    @Test
    void testAimUp() {
        double defaultAngle = worm.getAimAngle();
        controller.aimUp();
        assertEquals(defaultAngle + 0.03, worm.getAimAngle(), 1e-6); // Avec 1e-6 c'es plus precis
    }

    @Test
    void testAimDown() {
        double defaultAngle = worm.getAimAngle();
        controller.aimDown();
        assertEquals(defaultAngle - 0.03, worm.getAimAngle(), 1e-6);
    }

    @Test
    void testBindKeysTo() {
        JPanel panel = new JPanel();
        controller.bindKeysTo(panel);

        InputMap im = panel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = panel.getActionMap();

        assertEquals("aimUp", im.get(KeyStroke.getKeyStroke("UP")));
        assertEquals("aimDown", im.get(KeyStroke.getKeyStroke("DOWN")));

        assertNotNull(am.get("aimUp"));
        assertNotNull(am.get("aimDown"));
    }

    @Test
    void testBindKeysToActions() {
        JPanel inputPanel = new JPanel();
        JPanel repaintPanel = spy(new JPanel());

        controller.bindKeysTo(inputPanel, repaintPanel);

        ActionMap am = inputPanel.getActionMap();
        Action aimUpAction = am.get("aimUp");

        double angleBefore = worm.getAimAngle();
        aimUpAction.actionPerformed(new ActionEvent(inputPanel, ActionEvent.ACTION_PERFORMED, ""));

        assertEquals(angleBefore + 0.03, worm.getAimAngle(), 1e-6);
        verify(repaintPanel, atLeastOnce()).repaint();
    }

    @Test
    void testAimUpnullWormdoesNothing() {
        when(model.getCurrentWorm()).thenReturn(null);
        assertDoesNotThrow(() -> controller.aimUp());
    }

    @Test
    void testAimDownnullWormdoesNothing() {
        when(model.getCurrentWorm()).thenReturn(null);
        assertDoesNotThrow(() -> controller.aimDown());
    }

    @Test
    void testAimDownwestAngleBeyondLimit() {
        Worm mockWorm = mock(Worm.class);
        when(model.getCurrentWorm()).thenReturn(mockWorm);
        when(mockWorm.isFacingWest()).thenReturn(true);
        when(mockWorm.getAimAngle()).thenReturn(-Math.PI + 0.01);

        controller.aimDown();

        verify(mockWorm).rotateAim(-Math.PI - (-Math.PI + 0.01));
    }

    @Test
    void testAimDowneastAngleBeyondLimit() {
        Worm mockWorm = mock(Worm.class);
        when(model.getCurrentWorm()).thenReturn(mockWorm);
        when(mockWorm.isFacingWest()).thenReturn(false);
        when(mockWorm.getAimAngle()).thenReturn(Math.PI - 0.01);

        controller.aimDown();

        verify(mockWorm).rotateAim(Math.PI - (Math.PI - 0.01));
    }

}