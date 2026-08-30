package view.gui;

import model.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

class MapRendererTest {

    private MapRenderer renderer;
    private Map mapMock;
    private Graphics2D graphicsMock;

    @BeforeEach
    void setUp() {
        renderer = new MapRenderer();
        mapMock = mock(Map.class);
        graphicsMock = mock(Graphics2D.class);
        MapRenderer.resetZoom();
    }

    @Test
    void testConstructorLoadsAllImages() throws IllegalAccessException {
        Field[] fields = MapRenderer.class.getDeclaredFields();

        for (Field field : fields) {
            if (field.getType() == BufferedImage.class) {
                field.setAccessible(true);
                BufferedImage img = (BufferedImage) field.get(renderer);
                assertNotNull(img, "Le champ " + field.getName() + " doit être initialisé");
            }
        }
    }

    @Test
    void testDefaultTileSize() {
        MapRenderer.resetZoom();
        assertEquals(MapRenderer.DEFAULT_TILE_SIZE, MapRenderer.getTileSize(),
                "getTileSize doit retourner DEFAULT_TILE_SIZE après reset");
    }

    @Test
    void testSetTileSizeWithinLimits() {
        MapRenderer.setTileSize(50);
        assertEquals(50, MapRenderer.getTileSize(), "Tile size doit être réglé correctement");

        MapRenderer.setTileSize(1);
        assertEquals(8, MapRenderer.getTileSize(), "Tile size minimum doit être 8");

        MapRenderer.setTileSize(200);
        assertEquals(128, MapRenderer.getTileSize(), "Tile size maximum doit être 128");
    }

    @Test
    void testRenderDrawsBackgroundForIslandsOrBridge() {
        when(mapMock.getMapType()).thenReturn("islands");
        when(mapMock.getWidth()).thenReturn(2);
        when(mapMock.getHeight()).thenReturn(2);
        when(mapMock.getCell(anyInt(), anyInt())).thenReturn('#');

        renderer.render(graphicsMock, mapMock);

        verify(graphicsMock, atLeastOnce()).drawImage(
                eq(getPrivateField(renderer, "map_islandAndBridge_bg")),
                eq(0),
                eq(0),
                eq(2 * MapRenderer.TILE_SIZE),
                eq(2 * MapRenderer.TILE_SIZE),
                isNull());
    }

    @Test
    void testRenderDrawsBackgroundForCave() {
        when(mapMock.getMapType()).thenReturn("cave");
        when(mapMock.getWidth()).thenReturn(1);
        when(mapMock.getHeight()).thenReturn(1);
        when(mapMock.getCell(0, 0)).thenReturn('~');

        renderer.render(graphicsMock, mapMock);

        verify(graphicsMock).drawImage(
                eq(getPrivateField(renderer, "map_cave_bg")),
                eq(0),
                eq(0),
                eq(1 * MapRenderer.TILE_SIZE),
                eq(1 * MapRenderer.TILE_SIZE),
                isNull());
    }

    @Test
    void testRenderDrawsAllTileTypes() {
        when(mapMock.getMapType()).thenReturn("islands");
        when(mapMock.getWidth()).thenReturn(2);
        when(mapMock.getHeight()).thenReturn(2);

        when(mapMock.getCell(0, 0)).thenReturn('#');
        when(mapMock.getCell(1, 0)).thenReturn('G');
        when(mapMock.getCell(0, 1)).thenReturn('~');
        when(mapMock.getCell(1, 1)).thenReturn('X');

        renderer.render(graphicsMock, mapMock);

        verify(graphicsMock).drawImage(eq(getPrivateField(renderer, "dirt")), eq(0), eq(0),
                eq(MapRenderer.TILE_SIZE), eq(MapRenderer.TILE_SIZE), isNull());
        verify(graphicsMock).drawImage(eq(getPrivateField(renderer, "grass")), eq(MapRenderer.TILE_SIZE), eq(0),
                eq(MapRenderer.TILE_SIZE), eq(MapRenderer.TILE_SIZE), isNull());
        verify(graphicsMock).drawImage(eq(getPrivateField(renderer, "water")), eq(0), eq(MapRenderer.TILE_SIZE),
                eq(MapRenderer.TILE_SIZE), eq(MapRenderer.TILE_SIZE), isNull());
        verify(graphicsMock).drawImage(eq(getPrivateField(renderer, "nothing")), eq(MapRenderer.TILE_SIZE),
                eq(MapRenderer.TILE_SIZE),
                eq(MapRenderer.TILE_SIZE), eq(MapRenderer.TILE_SIZE), isNull());
    }

    @Test
    void testRenderAllSwitchCases() throws Exception {
        BufferedImage testImage = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphicsSpy = spy(testImage.createGraphics());

        when(mapMock.getMapType()).thenReturn("islands");
        when(mapMock.getWidth()).thenReturn(1);
        when(mapMock.getHeight()).thenReturn(22);

        char[] chars = {
                '#', '~', 'W', 'G', 'R', 'P', 'I', 'L', 'E',
                's', 'S', 't', 'T', 'b', 'B', 'w', 'h', 'H', 'c', 'r', 'X'
        };

        for (int y = 0; y < chars.length; y++) {
            when(mapMock.getCell(0, y)).thenReturn(chars[y]);
        }

        renderer.render(graphicsSpy, mapMock);

        for (int y = 0; y < chars.length; y++) {
            String expectedField = switch (chars[y]) {
                case '#' -> "dirt";
                case '~' -> "water";
                case 'W' -> "waterNothingAbove";
                case 'G' -> "grass";
                case 'R' -> "rock";
                case 'P' -> "plant";
                case 'I' -> "signRight";
                case 'L' -> "signLeft";
                case 'E' -> "signExit";
                case 's' -> "stone";
                case 'S' -> "stoneTop";
                case 't' -> "rockDirt";
                case 'T' -> "rockDirtTop";
                case 'b' -> "box";
                case 'B' -> "boxCrossed";
                case 'w' -> "weight";
                case 'h' -> "switchLeft";
                case 'H' -> "switchRight";
                case 'c' -> "chain";
                case 'r' -> "rope";
                default -> "nothing";
            };

            verify(graphicsSpy, atLeastOnce()).drawImage(
                    eq(getPrivateField(renderer, expectedField)),
                    eq(0),
                    eq(y * MapRenderer.TILE_SIZE),
                    eq(MapRenderer.TILE_SIZE),
                    eq(MapRenderer.TILE_SIZE),
                    any());
        }
    }

    private BufferedImage getPrivateField(MapRenderer renderer, String fieldName) {
        try {
            Field field = MapRenderer.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (BufferedImage) field.get(renderer);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
