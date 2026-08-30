package controller.bot;

import model.GameModel;
import model.players.Worm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.gui.GuiView;

import javax.swing.SwingUtilities;
import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

class BotControllerTest {

    private BotController controller;
    private BotDecisionEngine engineMock;
    private GameModel model;
    private Worm worm;

    @BeforeEach
    void setUp() throws Exception {
        GuiView view = mock(GuiView.class);
        controller = new BotController(1, view);

        engineMock = mock(BotDecisionEngine.class);

        Field engineField = BotController.class.getDeclaredField("engine");
        engineField.setAccessible(true);
        engineField.set(controller, engineMock);

        model = mock(GameModel.class);
        worm = mock(Worm.class);
        when(model.getCurrentWorm()).thenReturn(worm);
    }

    @Test
    void playTurnWithShootActionExecutesAndStopsTimer() throws Exception {
        ShootAction shootAction = mock(ShootAction.class);

        when(engineMock.decide(model, worm)).thenReturn(shootAction);

        SwingUtilities.invokeLater(() -> controller.playTurn(model));

        verify(shootAction, timeout(2000)).execute(model);
        verify(engineMock, timeout(2000)).decide(model, worm);
    }

    @Test
    void playTurnWithNonShootActionExecutesWithoutStopping() throws Exception {
        BotAction action = mock(BotAction.class);

        when(engineMock.decide(model, worm))
                .thenReturn(action)
                .thenReturn(null);

        SwingUtilities.invokeLater(() -> controller.playTurn(model));

        verify(action, timeout(2000)).execute(model);
        verify(engineMock, timeout(2000).atLeastOnce()).decide(model, worm);
    }

    @Test
    void playTurnWithNullActionStopsImmediately() throws Exception {
        when(engineMock.decide(model, worm)).thenReturn(null);

        SwingUtilities.invokeLater(() -> controller.playTurn(model));

        verify(engineMock, timeout(2000)).decide(model, worm);
    }
}
