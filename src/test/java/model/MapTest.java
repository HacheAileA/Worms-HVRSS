package model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MapTest {

    private Map map;

    @BeforeEach
    void setUp() {
        map = new Map(10, 10);
    }

    @Test
    void testDimensions() {
        assertEquals(10, map.getHeight());
        assertEquals(10, map.getWidth());
    }

    @Test
    void testGetAndSetCell() {
        map.setCell(2, 3, '#');
        assertEquals('#', map.getCell(2, 3));
    }

    @Test
    void testIsEmpty() {
        map.setCell(1, 1, ' ');
        assertTrue(map.isEmpty(1, 1));
        map.setCell(1, 1, '#');
        assertFalse(map.isEmpty(1, 1));
    }

    @Test
    void testIsGroundBranches() {
        map.setCell(0, 0, '#');
        assertTrue(map.isGround(0, 0));
        assertFalse(map.isGround(-1, 0));
        assertFalse(map.isGround(0, -1));
        assertFalse(map.isGround(5, 0));
        assertFalse(map.isGround(0, 5));
    }

    @Test
    void testIsWater() {
        map.setCell(0, 0, '~');
        assertTrue(map.isWater(0, 0));
        map.setCell(0, 0, ' ');
        assertFalse(map.isWater(0, 0));
    }

    @Test
    void testCanWormBePlacedBranches() {
        map.setCell(2, 2, '#');
        assertTrue(map.canWormBePlaced(2, 1));

        map.setCell(1, 1, 'X');
        assertFalse(map.canWormBePlaced(1, 1));

        assertFalse(map.canWormBePlaced(-1, 0));
        assertFalse(map.canWormBePlaced(0, -1));
        assertFalse(map.canWormBePlaced(5, 0));
        assertFalse(map.canWormBePlaced(0, 5));

        map.setCell(3, 3, ' ');
        assertFalse(map.canWormBePlaced(3, 2));
    }

    @Test
    void testSetMapTypeValidTypes() {
        Map map = new Map(10, 10);

        map.setMapType("islands");
        assertEquals("islands", map.getMapType());

        map.setMapType("bridge");
        assertEquals("bridge", map.getMapType());

        map.setMapType("cave");
        assertEquals("cave", map.getMapType());
    }

    @Test
    void testSmoothTerrainConnection() throws Exception {
        Method method = Map.class.getDeclaredMethod("smoothTerrainConnection", int.class, int.class, int.class,
                int.class);
        method.setAccessible(true);

        method.invoke(map, 2, 5, 1, 3);

        assertEquals('#', map.grid[1][2]);
        assertEquals('#', map.grid[1][3]);
        assertEquals('#', map.grid[2][3]);
        assertEquals('#', map.grid[2][4]);
        assertEquals('#', map.grid[3][4]);
    }

    @Test
    void testIsEmptyOutOfBounds() {
        assertFalse(map.isEmpty(-1, 0));
        assertFalse(map.isEmpty(0, -1));
        assertTrue(map.isEmpty(5, 0));
        assertTrue(map.isEmpty(0, 5));
    }

    @Test
    void testIsEmptyEmptySpace() {
        map.grid[2][2] = ' ';
        assertTrue(map.isEmpty(2, 2));
    }

    @Test
    void testIsEmptyWorm() {
        map.grid[1][1] = 'W';
        assertTrue(map.isEmpty(1, 1), "Un worms est considéré comme du vide. On peut passer à travers les autres");
    }

    @Test
    void testIsEmptyAllowedCharacters() {
        char[] allowed = { 'R', 'E', 'L', 'I', 'P', 'r', 'c' };
        int y = 0;
        for (char c : allowed) {
            map.grid[y][0] = c;
            assertTrue(map.isEmpty(0, y), "Character " + c + " should be empty");
            y++;
        }
    }

    @Test
    void testIsEmptyOtherCharacter() {
        map.grid[3][3] = '#';
        assertFalse(map.isEmpty(3, 3));
    }

    @Test
    void testCanWormBePlacedThirdIfAtBottomRow() {
        int x = 2;
        int y = map.getHeight() - 1;

        boolean result = map.canWormBePlaced(x, y);

        assertFalse(result, "Worm should not be placeable on the bottom row");
    }

    @Test
    void testCanWormBePlacedThirdIfBelowGrid() {
        int x = 2;
        int y = map.getHeight();

        boolean result = map.canWormBePlaced(x, y);

        assertFalse(result, "Worm should not be placeable below the grid");
    }

    @Test
    void testGenerateMapByTypeIslands() {
        map.setMapType("islands");
        map.generateMapByType();

        boolean hasLandOrWater = false;
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                char c = map.grid[y][x];
                if (c == '#' || c == '~') {
                    hasLandOrWater = true;
                    break;
                }
            }
        }
        assertTrue(hasLandOrWater, "Islands map should generate land or water cells");
    }

    @Test
    void testGenerateMapByTypeCave() {
        map.setMapType("cave");
        map.generateMapByType();

        boolean hasWalls = false;
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                if (map.grid[y][x] == '#') {
                    hasWalls = true;
                    break;
                }
            }
        }
        assertFalse(hasWalls, "Cave map should have wall cells");
    }

    @Test
    void testGenerateMapByTypeUnknownType() {
        map.setMapType("volcano");
        map.generateMapByType();

        boolean hasTerrain = false;
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                if (map.grid[y][x] != ' ') {
                    hasTerrain = true;
                    break;
                }
            }
        }
        assertTrue(hasTerrain, "Unknown map type should fallback to default map generation");
    }

    @Test
    void testMapWithDecorationsIslands() {
        map.setMapType("islands");
        map.mapWithDecorations();

        boolean foundWaterDecoration = false;
        for (int y = 1; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                if (map.grid[y][x] == '~' && map.grid[y - 1][x] == 'W') {
                    foundWaterDecoration = true;
                }
            }
        }
        assertTrue(foundWaterDecoration, "Islands map should place 'W' above water '~'");
    }

    @Test
    void testMapWithDecorationsCave() {
        map.setMapType("cave");
        map.mapWithDecorations();

        boolean foundWaterDecoration = false;
        for (int y = 1; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                if (map.grid[y][x] == '~' && map.grid[y - 1][x] == 'W') {
                    foundWaterDecoration = true;
                }
            }
        }
        assertFalse(foundWaterDecoration);
    }

    @Test
    void testMapWithDecorationsNoWater() {
        map.setMapType("islands");
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                map.grid[y][x] = '#';
            }
        }

        map.mapWithDecorations();

        boolean hasW = false;
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                if (map.grid[y][x] == 'W')
                    hasW = true;
            }
        }

        assertTrue(hasW);
    }

    @Test
    void testBridgeDecorationsmatchIslandsDecorations() {
        Map islandsMap = new Map(10, 10);
        Map bridgeMap = new Map(10, 10);

        islandsMap.setMapType("islands");
        islandsMap.mapWithDecorations();

        bridgeMap.setMapType("bridge");
        bridgeMap.mapWithDecorations();

        char[][] islandsGrid = islandsMap.grid;
        char[][] bridgeGrid = bridgeMap.grid;

        boolean decorationsMatch = true;
        for (int y = 0; y < islandsMap.getHeight(); y++) {
            for (int x = 0; x < islandsMap.getWidth(); x++) {
                if (bridgeGrid[y][x] != islandsGrid[y][x]) {
                    decorationsMatch = false;
                    break;
                }
            }
        }

        assertFalse(decorationsMatch, "Les décorations sont générées aléatoirement");
    }

    @Test
    void testSmoothTerrainConnectionfillsEmptyAndWater() throws Exception {
        map.grid[5][2] = '~';
        map.grid[6][3] = '~';

        Method method = Map.class.getDeclaredMethod(
                "smoothTerrainConnection", int.class, int.class, int.class, int.class);
        method.setAccessible(true);

        int startX = 2, endX = 5, startY = 1, endY = 4;

        method.invoke(map, startX, endX, startY, endY);

        for (int x = startX; x < endX; x++) {
            double progress = (double) (x - startX) / (endX - startX);
            int targetY = (int) (startY + progress * (endY - startY));
            for (int y = targetY; y < map.getHeight() - 2; y++) {
                char cell = map.grid[y][x];
                assertEquals('#', cell);
            }
        }
    }

    @Test
    void testGenerateCaveMap() throws Exception {
        Method method = Map.class.getDeclaredMethod("generateCaveMap");
        method.setAccessible(true);

        method.invoke(map);
        assertEquals("cave", map.getMapType());

        char[][] grid = map.grid;
        int height = map.getHeight();
        int width = map.getWidth();

        boolean foundTile = false;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (grid[y][x] == 't') {
                    foundTile = true;
                    break;
                }
            }
        }

        assertTrue(foundTile);
    }

    @Test
    void testGetTeam() {
        map.setCell(0, 0, '1');
        map.setCell(0, 1, '2');
        map.setCell(0, 2, '3');
        map.setCell(0, 3, '4');
        map.setCell(0, 5, 'B');

        assertEquals(1, map.getTeam(0, 0));
        assertEquals(2, map.getTeam(0, 1));
        assertEquals(3, map.getTeam(0, 2));
        assertEquals(4, map.getTeam(0, 3));
        assertEquals(0, map.getTeam(0, 5));
    }

    @Test
    void testIsGroundoutOfBounds() {
        assertFalse(map.isGround(-1, 0));
        assertFalse(map.isGround(0, -1));
        assertFalse(map.isGround(map.getWidth(), 0));
        assertFalse(map.isGround(0, map.getHeight()));
    }

    @Test
    void testIsGroundgroundCharactersWithoutWorm() {
        char[] groundChars = { '#', 'G', 't', 'T', 's', 'S' };
        for (char c : groundChars) {
            map.grid[2][2] = c;
            Map spyMap = spy(map);
            doReturn(false).when(spyMap).isWorm(2, 2);

            assertTrue(spyMap.isGround(2, 2), "Character " + c + " should be ground");
        }
    }

    @Test
    void testIsGroundgroundCharacterWithWorm() {
        map.grid[1][1] = '#';
        Map spyMap = spy(map);
        doReturn(true).when(spyMap).isWorm(1, 1);

        assertFalse(spyMap.isGround(1, 1), "Ground with worm should not be considered ground");
    }

    @Test
    void testIsGroundnonGroundCharacters() {
        char[] nonGroundChars = { ' ', 'R', 'E', 'L', 'I', 'P', 'W', 'b', 'B', 'c', 'r', 'w', 'h', 'H' };
        for (char c : nonGroundChars) {
            map.grid[3][3] = c;
            Map spyMap = spy(map);
            doReturn(false).when(spyMap).isWorm(3, 3);
        }
    }

    @Test
    void testGetCellvalidCoordinates() {
        map.setCell(2, 2, 'B');
        assertEquals(' ', map.getCell(0, 0), "Censée être vide de base");
        assertEquals('B', map.getCell(2, 2), "Doit être B si correctement mise à jour");
    }

    @Test
    void testGetCelloutOfBounds() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> map.getCell(-1, 0));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> map.getCell(0, -1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> map.getCell(map.getWidth(), 0));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> map.getCell(0, map.getHeight()));
    }

    @Test
    void testIsWatertrueForTilde() {
        map.setCell(0, 0, '~');
        assertTrue(map.isWater(0, 0));
    }

    @Test
    void testIsWaterfalseForW() {
        map.setCell(0, 1, 'W');
        assertFalse(map.isWater(0, 1));
    }

    @Test
    void testIsDecorationwithDecorations() throws Exception {
        Method isDecoration = Map.class.getDeclaredMethod("isDecoration", int.class, int.class);
        isDecoration.setAccessible(true);

        char[] decorations = {'R','E','L','I','P','b','B','w','h','H'};
        int x = 2, y = 2;

        for (char c : decorations) {
            map.grid[y][x] = c;
            boolean result = (boolean) isDecoration.invoke(map, x, y);
            assertTrue(result, "Expected true for decoration: " + c);
        }
    }

    @Test
    void testIsDecorationwithNonDecorations() throws Exception {
        Method isDecoration = Map.class.getDeclaredMethod("isDecoration", int.class, int.class);
        isDecoration.setAccessible(true);

        char[] nonDecorations = {' ', '#', 't', 'G', 's', 'S', '~', 'X'};
        int x = 1, y = 1;

        for (char c : nonDecorations) {
            map.grid[y][x] = c;
            boolean result = (boolean) isDecoration.invoke(map, x, y);
            assertFalse(result, "Expected false for non-decoration: " + c);
        }
    }

    @Test
    void testIsDecorationoutOfBounds() throws Exception {
        Method isDecoration = Map.class.getDeclaredMethod("isDecoration", int.class, int.class);
        isDecoration.setAccessible(true);

        assertFalse((boolean) isDecoration.invoke(map, -1, 0));
        assertFalse((boolean) isDecoration.invoke(map, 0, -1));
        assertFalse((boolean) isDecoration.invoke(map, map.getWidth(), 0));
        assertFalse((boolean) isDecoration.invoke(map, 0, map.getHeight()));
    }

        @Test
    void testCreateExplosionremovesGroundAndDecorations() {
        int centerX = 5;
        int centerY = 5;
        int radius = 2;

        map.createExplosion(centerX, centerY, radius);

        assertEquals(' ', map.grid[5][5]);

        assertEquals(' ', map.grid[6][6]);
    }

    @Test
    void testCreateExplosionwaterLineSet() {
        int centerX = 5;
        int centerY = 5;
        int radius = 2;

        map.createExplosion(centerX, centerY, radius);

        int waterLine = map.getHeight() - 3;

        boolean waterPlaced = false;
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            if (x >= 0 && x < map.getWidth() && map.grid[waterLine][x] == 'W') {
                waterPlaced = true;
            }
        }
        assertTrue(waterPlaced, "Expected at least some water tiles to appear at water line");
    }

    @Test
    void testCreateExplosionradiusZerodoesNothing() {
        map.grid[2][2] = '#';
        map.createExplosion(2, 2, 0);

        assertEquals('#', map.grid[2][2]);
    }
}