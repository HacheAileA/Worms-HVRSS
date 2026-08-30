package model.physics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

class WindTest {

    private Wind wind;

    @BeforeEach
    void setup() {
        wind = new Wind();
    }

    @Test
    void testGetStrength() throws Exception {
        Field field = Wind.class.getDeclaredField("strength");
        field.setAccessible(true);
        field.set(wind, 12.5);

        assertEquals(12.5, wind.getStrength());
    }

    @Test
    void testGetDirectioninitialValue() {
        assertEquals(1, wind.getDirection());
    }

    @Test
    void testGetForcewhenDisabled() {
        assertFalse(wind.enabled);
        assertEquals(0.0, wind.getForce());
    }

    @Test
    void testGetForcewhenEnabled() throws Exception {
        wind.enabled = true;

        Field strengthField = Wind.class.getDeclaredField("strength");
        strengthField.setAccessible(true);
        strengthField.set(wind, 7.5);

        Field directionField = Wind.class.getDeclaredField("direction");
        directionField.setAccessible(true);
        directionField.set(wind, -1);

        assertEquals(-7.5, wind.getForce());
    }
}
