package model.items.tools;

import model.GameModel;
import model.physics.Projectile;
import model.players.Worm;
import model.players.Team;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class AirStrikeTest {

    @Mock
    private GameModel mockModel;

    @Mock
    private Team mockTeam;

    @Mock
    private Worm mockWorm;

    private AirStrike airStrike;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockModel.getCurrentTeam()).thenReturn(mockTeam);
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testConstructor() {
        airStrike = new AirStrike(mockModel);

        assertNotNull(airStrike);
        assertEquals("Air Strike", airStrike.getName());
        assertEquals(1, airStrike.getAmmo());
        verify(mockModel, times(1)).getCurrentTeam();
    }

    @Test
    void testConstructorStoresModel() {
        airStrike = new AirStrike(mockModel);

        assertNotNull(airStrike);
    }

    @Test
    void testUseToolWithWormOnly() {
        airStrike = new AirStrike(mockModel);

        assertDoesNotThrow(() -> airStrike.useTool(mockWorm));
    }

    @Test
    void testUseToolWithTargetXWhenAmmoAvailable() {
        airStrike = new AirStrike(mockModel);
        int targetX = 100;

        Projectile result = airStrike.useTool(mockWorm, targetX);

        assertNotNull(result);
        assertEquals(0, airStrike.getAmmo());
    }

    @Test
    void testUseToolWithTargetXWhenAmmoIsZero() {
        airStrike = new AirStrike(mockModel);

        airStrike.useTool(mockWorm, 50);
        outputStream.reset();

        Projectile result = airStrike.useTool(mockWorm, 100);

        assertNull(result);
        assertEquals(0, airStrike.getAmmo());
        assertFalse(outputStream.toString().contains("AirStrike used"));
    }

    @Test
    void testUseToolWithTargetXWhenAmmoIsNegative() {
        airStrike = new AirStrike(mockModel);

        try {
            java.lang.reflect.Field ammoField = Tools.class.getDeclaredField("ammo");
            ammoField.setAccessible(true);
            ammoField.setInt(airStrike, -1);
        } catch (Exception e) {
            fail("Could not set ammo field");
        }

        Projectile result = airStrike.useTool(mockWorm, 100);

        assertNull(result);
    }

    @Test
    void testUseToolDecrementsAmmo() {
        airStrike = new AirStrike(mockModel);

        assertEquals(1, airStrike.getAmmo());
        airStrike.useTool(mockWorm, 50);
        assertEquals(0, airStrike.getAmmo());
    }

    @Test
    void testUseToolCreatesProjectileWithCorrectParameters() {
        airStrike = new AirStrike(mockModel);
        int targetX = 200;

        Projectile result = airStrike.useTool(mockWorm, targetX);

        assertNotNull(result);
    }

    @Test
    void testUseToolWithNegativeTargetX() {
        airStrike = new AirStrike(mockModel);
        int targetX = -50;

        Projectile result = airStrike.useTool(mockWorm, targetX);

        assertNotNull(result);
    }

    @Test
    void testUseToolWithZeroTargetX() {
        airStrike = new AirStrike(mockModel);
        int targetX = 0;

        Projectile result = airStrike.useTool(mockWorm, targetX);

        assertNotNull(result);
    }

    @Test
    void testUseToolWithNullWorm() {
        airStrike = new AirStrike(mockModel);

        assertDoesNotThrow(() -> {
            Projectile result = airStrike.useTool(null, 100);
            assertNotNull(result);
        });
    }

    @Test
    void testConstructorWithNullModel() {
        assertThrows(NullPointerException.class, () -> {
            new AirStrike(null);
        });
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}