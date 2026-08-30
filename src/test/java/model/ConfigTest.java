package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

    private Config config;

    @BeforeEach
    void setUp() {
        config = new Config();
    }

    @Test
    void testConstructorInitializesParameters() {
        assertNotNull(config.parameters, "La table des paramètres ne doit pas être nulle");
        assertEquals(45, config.getIntParameter("TIMER"));
        assertFalse(config.getBoolParameter("WIND"));
        assertEquals(500, config.getIntParameter("DELAY"));
        assertEquals(100, config.getIntParameter("HP"));
    }

    @Test
    void testGetters() {
        assertEquals(45, config.getIntParameter("TIMER"));
        assertFalse(config.getBoolParameter("WIND"));
        assertNull(config.getStringParameter("NONEXISTANTKEY"));
    }

    @Test
    void testReplaceParameter() {
        config.replace("DELAY", 1000);
        assertEquals(1000, config.getIntParameter("DELAY"));

        config.replace("WIND", true);
        assertTrue(config.getBoolParameter("WIND"));

        config.replace("GAMENAME", "WormsTest");
        assertEquals("WormsTest", config.getStringParameter("GAMENAME"));
    }

    @Test
    void testReplaceNonExistingKey() {
        config.replace("NEWKEY", 123);
        assertEquals(123, config.getIntParameter("NEWKEY"));
    }
}
