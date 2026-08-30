package view.gui;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Graphics2D;
import java.util.ArrayList;

import model.Map;
import model.items.guns.Guns;
import model.physics.TrajectoryPredictor;
import model.players.Worm;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

class TrajectoryRendererTest {

    TrajectoryRenderer renderer;

    @Mock
    Graphics2D g2dMock;

    @Mock
    Worm wormMock;

    @Mock
    Map mapMock;
    
    @Mock
    Guns gunMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        renderer = new TrajectoryRenderer();
    }

    @Test
    void testRenderDoesNothingWhenNoGunSelected() {
        when(wormMock.getSelectedItem()).thenReturn(null);
        assertDoesNotThrow(() -> renderer.render(g2dMock, wormMock, mapMock));
        verifyNoInteractions(g2dMock);
    }

    @Test
    void testRenderWithGunDrawsTrajectory() {
        when(wormMock.getSelectedItem()).thenReturn(gunMock);
        when(wormMock.getX()).thenReturn(5.0);
        when(wormMock.getY()).thenReturn(10.0);
        when(wormMock.getAimAngle()).thenReturn(Math.PI / 4);
        when(gunMock.getProjectileSpeed()).thenReturn(10.0);
        when(gunMock.getGravity()).thenReturn(9.8);
        renderer.render(g2dMock, wormMock, mapMock);
        verify(g2dMock, atLeastOnce()).setColor(any());
        verify(g2dMock, atLeastOnce()).setStroke(any());
        verify(g2dMock, atLeastOnce()).drawLine(anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void testRenderDoesNothingWhenWormIsNull() {
        assertDoesNotThrow(() -> renderer.render(g2dMock, null, mapMock));
        verifyNoInteractions(g2dMock);
    }

    @Test
    void testRenderWithSinglePointTrajectoryDoesNothing() {
        when(wormMock.getSelectedItem()).thenReturn(gunMock);

        try (MockedStatic<TrajectoryPredictor> mockedPredictor = mockStatic(TrajectoryPredictor.class)) {
            ArrayList<double[]> singlePoint = new ArrayList<>();
            singlePoint.add(new double[] { 0, 0 });
            mockedPredictor.when(() -> TrajectoryPredictor.predict(
                    anyDouble(), anyDouble(), anyInt(), anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                    .thenReturn(singlePoint);

            renderer.render(g2dMock, wormMock, mapMock);

            verify(g2dMock, never()).drawLine(anyInt(), anyInt(), anyInt(), anyInt());
        }
    }

}
