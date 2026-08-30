package view.gui;

import model.GameModel;
import model.Map;
import model.players.Team;
import model.players.Worm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class DevModRendererTest {

    private DevModRenderer renderer;
    private GameModel model;
    private Map map;
    private Graphics2D g2d;

    @BeforeEach
    void setUp() {
        renderer = new DevModRenderer();

        model = mock(GameModel.class);
        map = mock(Map.class);

        when(model.getMap()).thenReturn(map);

        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
    }

    @Test
    void testRendernoTeamsnoException() {
        when(model.getTeams()).thenReturn(new ArrayList<>(List.of()));

        assertDoesNotThrow(() -> renderer.render(g2d, model));
    }

    @Test
    void testRenderwithTeamAndWorms() {
        Worm worm = mock(Worm.class);
        when(worm.getX()).thenReturn(1.0);
        when(worm.getY()).thenReturn(2.0);

        Team team = mock(Team.class);
        when(team.getWorms()).thenReturn(new ArrayList<>(List.of(worm)));

        when(model.getTeams()).thenReturn(new ArrayList<>(List.of(team)));

        when(map.getWidth()).thenReturn(2);
        when(map.getHeight()).thenReturn(2);
        when(map.isGround(anyInt(), anyInt())).thenReturn(false);
        when(map.isWater(anyInt(), anyInt())).thenReturn(false);

        assertDoesNotThrow(() -> renderer.render(g2d, model));

        verify(worm).getX();
        verify(worm).getY();
    }

    @Test
    void testRendermapTilesDrawn() {
        when(model.getTeams()).thenReturn(new ArrayList<>(List.of()));

        when(map.getWidth()).thenReturn(2);
        when(map.getHeight()).thenReturn(2);

        when(map.isWater(0, 0)).thenReturn(true);
        when(map.isGround(1, 1)).thenReturn(true);

        assertDoesNotThrow(() -> renderer.render(g2d, model));

    }
}

