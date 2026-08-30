package controller.bot;

import static org.mockito.Mockito.*;

import model.GameModel;
import model.players.Worm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MoveActionTest {

    private GameModel mockModel;
    private Worm mockWorm;

    @BeforeEach
    void setUp() {
        mockModel = mock(GameModel.class);
        mockWorm = mock(Worm.class);

        when(mockModel.getCurrentWorm()).thenReturn(mockWorm);

        doNothing().when(mockWorm).jumpSmooth(any());
    }

    @Test
    void testExecuteMovesRightWithoutJump() throws InterruptedException {
        MoveAction action = new MoveAction(1, 0.1, false);

        action.execute(mockModel);

        verify(mockWorm).startMoveRight();
        verify(mockWorm, never()).jumpSmooth(any());

        Thread.sleep(200);

        verify(mockWorm).stopMove();
    }

    @Test
    void testExecuteMovesLeftWithJump() throws InterruptedException {
        MoveAction action = new MoveAction(-1, 0.1, true);

        action.execute(mockModel);

        verify(mockWorm).jumpSmooth(any());
        verify(mockWorm).startMoveLeft();

        Thread.sleep(200);
        verify(mockWorm).stopMove();
    }
}
