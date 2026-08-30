package view.gui;

import model.GameModel;
import model.players.Team;
import model.players.Worm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

class WormRendererTest {

    private WormRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = new WormRenderer();
    }

    @Test
    void testConstructorLoadsAllSprites() throws Exception {
        Method loadMethod = WormRenderer.class.getDeclaredMethod("load", String.class);
        loadMethod.setAccessible(true);

        for (int i = 0; i <= 3; i++) {
            BufferedImage eastImg = (BufferedImage) loadMethod.invoke(renderer,
                    "/assets/worms/worm_east" + (i + 1) + ".png");
            BufferedImage westImg = (BufferedImage) loadMethod.invoke(renderer,
                    "/assets/worms/worm_west" + (i + 1) + ".png");
            assertNotNull(eastImg);
            assertNotNull(westImg);
        }
    }

    @Test
    void testRenderWithCurrentWormEastAndWest() {
        Graphics2D g = mock(Graphics2D.class);

        when(g.getFont()).thenReturn(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));

        Worm eastWorm = mock(Worm.class);
        when(eastWorm.getX()).thenReturn(1.0);
        when(eastWorm.getY()).thenReturn(2.0);
        when(eastWorm.isFacingWest()).thenReturn(false);
        when(eastWorm.getHp()).thenReturn(100);
        when(eastWorm.getName()).thenReturn("EastWorm");

        Worm westWorm = mock(Worm.class);
        when(westWorm.getX()).thenReturn(3.0);
        when(westWorm.getY()).thenReturn(4.0);
        when(westWorm.isFacingWest()).thenReturn(true);
        when(westWorm.getHp()).thenReturn(50);
        when(westWorm.getName()).thenReturn("WestWorm");

        Team team = mock(Team.class);
        when(team.getTeamId()).thenReturn(0);
        when(team.getWorms()).thenReturn(new ArrayList<>(java.util.List.of(eastWorm, westWorm)));

        GameModel model = mock(GameModel.class);
        when(model.getTeams()).thenReturn(new ArrayList<>(java.util.List.of(team)));
        when(model.getCurrentWorm()).thenReturn(eastWorm);

        renderer.render(g, model);

        verify(g, atLeast(2)).drawImage(any(BufferedImage.class), anyInt(), anyInt(), anyInt(), anyInt(), isNull());
    }

    @Test
    void testRenderWithNoTeams() {
        Graphics2D g = mock(Graphics2D.class);
        GameModel model = mock(GameModel.class);
        when(model.getTeams()).thenReturn(new ArrayList<>());

        renderer.render(g, model);

        verifyNoInteractions(g);
    }

    @Test
    void testDrawHUDAndDrawGlowViaReflection() throws Exception {
        Graphics2D g = mock(Graphics2D.class);
        when(g.getFont()).thenReturn(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
        Worm worm = mock(Worm.class);
        when(worm.getHp()).thenReturn(75);
        when(worm.getName()).thenReturn("TestWorm");

        Method drawHUD = WormRenderer.class.getDeclaredMethod("drawHUD", Graphics2D.class, Worm.class, int.class,int.class);
        drawHUD.setAccessible(true);
        drawHUD.invoke(renderer, g, worm, 100, 200);

        Method drawGlow = WormRenderer.class.getDeclaredMethod("drawGlow", Graphics2D.class, int.class, int.class);
        drawGlow.setAccessible(true);
        drawGlow.invoke(renderer, g, 50, 60);
    }

    @Test
    void testWormWithZeroHpHUD() throws Exception {
        Graphics2D g = mock(Graphics2D.class);
        when(g.getFont()).thenReturn(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
        Worm worm = mock(Worm.class);
        when(worm.getHp()).thenReturn(0);
        when(worm.getName()).thenReturn("DeadWorm");

        Method drawHUD = WormRenderer.class.getDeclaredMethod("drawHUD", Graphics2D.class, Worm.class, int.class,
                int.class);
        drawHUD.setAccessible(true);
        drawHUD.invoke(renderer, g, worm, 0, 0);
    }

    @Test
    void testWormWithMaxHpHUD() throws Exception {
        Graphics2D g = mock(Graphics2D.class);
        when(g.getFont()).thenReturn(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
        Worm worm = mock(Worm.class);
        when(worm.getHp()).thenReturn(100);
        when(worm.getName()).thenReturn("FullHP");

        Method drawHUD = WormRenderer.class.getDeclaredMethod("drawHUD", Graphics2D.class, Worm.class, int.class,
                int.class);
        drawHUD.setAccessible(true);
        drawHUD.invoke(renderer, g, worm, 0, 0);
    }

    @Test
    void testDrawHUDWithPartialHp() throws Exception {
        Graphics2D g = mock(Graphics2D.class);
        when(g.getFont()).thenReturn(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
        Worm worm = mock(Worm.class);

        for (int hp : new int[] { 1, 49, 99 }) {
            when(worm.getHp()).thenReturn(hp);
            when(worm.getName()).thenReturn("PartialHP");

            Method drawHUD = WormRenderer.class.getDeclaredMethod("drawHUD", Graphics2D.class, Worm.class, int.class,
                    int.class);
            drawHUD.setAccessible(true);
            drawHUD.invoke(renderer, g, worm, 0, 0);
        }
    }
}
