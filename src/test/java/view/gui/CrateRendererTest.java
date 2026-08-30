package view.gui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Graphics2D;
import java.util.ArrayList;

import model.items.crates.Crate;
import model.items.crates.CrateManager;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class CrateRendererTest {

    private CrateRenderer crateRenderer;
    private Graphics2D graphics;
    private CrateManager crateManager;

    @BeforeEach
    void setUp() {
        crateRenderer = new CrateRenderer();
        graphics = mock(Graphics2D.class);
        crateManager = mock(CrateManager.class);
    }

    @Test
    void testRenderWithNoCrates() {
        when(crateManager.getActiveCrates()).thenReturn(new ArrayList<>());
        crateRenderer.render(graphics, crateManager, 32);
        verify(graphics, never()).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
        verify(graphics, never()).drawRect(anyInt(), anyInt(), anyInt(), anyInt());
        verify(graphics, never()).drawLine(anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void testRenderWithSingleCrate() {
        Crate crate = mock(Crate.class);
        when(crate.getX()).thenReturn(5);
        when(crate.getY()).thenReturn(10);
        ArrayList<Crate> crates = new ArrayList<>();
        crates.add(crate);
        when(crateManager.getActiveCrates()).thenReturn(crates);
        crateRenderer.render(graphics, crateManager, 32);
        verify(graphics, times(2)).setColor(any());
        verify(graphics, times(1)).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
        verify(graphics, times(1)).drawRect(anyInt(), anyInt(), anyInt(), anyInt());
        verify(graphics, times(2)).drawLine(anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void testRenderWithMultipleCrates() {
        Crate crate1 = mock(Crate.class);
        Crate crate2 = mock(Crate.class);
        when(crate1.getX()).thenReturn(2);
        when(crate1.getY()).thenReturn(3);
        when(crate2.getX()).thenReturn(7);
        when(crate2.getY()).thenReturn(1);
        ArrayList<Crate> crates = new ArrayList<>();
        crates.add(crate1);
        crates.add(crate2);
        when(crateManager.getActiveCrates()).thenReturn(crates);

        crateRenderer.render(graphics, crateManager, 32);

        verify(graphics, atLeast(2)).setColor(any());
        verify(graphics, atLeast(2)).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
        verify(graphics, atLeast(2)).drawRect(anyInt(), anyInt(), anyInt(), anyInt());
        verify(graphics, atLeast(2)).drawLine(anyInt(), anyInt(), anyInt(), anyInt());
    }
}
