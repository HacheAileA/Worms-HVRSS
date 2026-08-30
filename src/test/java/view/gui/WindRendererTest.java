package view.gui;

import model.physics.Wind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

class WindRendererTest {

    private WindRenderer renderer;
    private Graphics2D g2dMock;
    private Graphics2D gCreatedMock;
    private FontMetrics fmMock;

    @BeforeEach
    void setUp() {
        g2dMock = mock(Graphics2D.class);
        gCreatedMock = mock(Graphics2D.class);
        fmMock = mock(FontMetrics.class);

        when(g2dMock.create()).thenReturn(gCreatedMock);

        when(gCreatedMock.getFontMetrics()).thenReturn(fmMock);
        when(fmMock.stringWidth(anyString())).thenReturn(50);
        when(fmMock.getAscent()).thenReturn(12);
    }

    @Test
    void testRenderWithDisabledWindShowsDisabled() {
        Wind wind = new Wind();
        wind.enabled = false;
        renderer = new WindRenderer(wind);

        renderer.render(g2dMock, 300, 200);

        verify(gCreatedMock).setColor(Color.LIGHT_GRAY);
        verify(gCreatedMock).setFont(any(Font.class));
        verify(gCreatedMock).drawString(contains("OFF"), anyInt(), anyInt());
        verify(gCreatedMock).dispose();
    }

    @Test
    void testRenderWithEnabledWindDrawsArrowAndText() {
        Wind wind = new Wind();
        wind.enabled = true;
        wind.generateRandom();
        renderer = new WindRenderer(wind);

        renderer.render(g2dMock, 300, 200);

        verify(gCreatedMock, atLeastOnce()).drawLine(anyInt(), anyInt(), anyInt(), anyInt());
        verify(gCreatedMock).drawString(anyString(), anyInt(), anyInt());
        verify(gCreatedMock).dispose();
    }

    @Test
    void testGetWindColorBranches() throws Exception {
        WindRenderer renderer = new WindRenderer(null);
        Method method = WindRenderer.class.getDeclaredMethod("getWindColor", double.class);
        method.setAccessible(true);
        Color color1 = (Color) method.invoke(renderer, 5.0);
        assertEquals(new Color(120, 220, 120), color1);
        Color color2 = (Color) method.invoke(renderer, 10.0);
        assertEquals(new Color(255, 200, 80), color2);
        Color color3 = (Color) method.invoke(renderer, 20.0);
        assertEquals(new Color(255, 120, 120), color3);
    }
}
