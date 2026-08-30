package view.gui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.*;

class ParticleTest {

    private Particle particle;

    @BeforeEach
    void setUp() {
        particle = new Particle(0, 0, 1, 2, Color.RED, 5);
    }

    @Test
    void testConstructorSetsFieldsCorrectly() {
        assertEquals(0, particle.x, "x doit être initialisé correctement");
        assertEquals(0, particle.y, "y doit être initialisé correctement");
        assertEquals(1, particle.vx, "vx doit être initialisé correctement");
        assertEquals(2, particle.vy, "vy doit être initialisé correctement");
        assertEquals(Color.RED, particle.color, "color doit être initialisé correctement");
        assertEquals(5, particle.lifetime, "lifetime doit être initialisé correctement");
    }

    @Test
    void testUpdateChangesPositionAndVelocity() {
        double initialVY = particle.vy;
        particle.update();

        assertEquals(1, particle.x, "x doit augmenter de vx");
        assertEquals(2, particle.y, "y doit augmenter de vy");

        assertEquals(initialVY + 0.05, particle.vy, 1e-9, "vy doit augmenter de GRAVITY");

        assertEquals(4, particle.lifetime, "lifetime doit diminuer de 1");
    }

    @Test
    void testIsAliveReturnsTrueWhileLifetimePositive() {
        assertTrue(particle.isAlive(), "Particule avec lifetime > 0 doit être vivante");
    }

    @Test
    void testIsAliveReturnsFalseWhenLifetimeZero() {
        particle.lifetime = 0;
        assertFalse(particle.isAlive(), "Particule avec lifetime = 0 ne doit pas être vivante");
    }

    @Test
    void testIsAliveReturnsFalseWhenLifetimeNegative() {
        particle.lifetime = -1;
        assertFalse(particle.isAlive(), "Particule avec lifetime < 0 ne doit pas être vivante");
    }

    @Test
    void testUpdateMultipleTimes() {
        int updates = 3;
        double expectedX = particle.x;
        double expectedY = particle.y;
        double expectedVY = particle.vy;
        int expectedLifetime = particle.lifetime;

        for (int i = 0; i < updates; i++) {
            expectedX += particle.vx;
            expectedY += expectedVY;
            expectedVY += 0.05;
            expectedLifetime--;
            particle.update();
        }

        assertEquals(expectedX, particle.x, 1e-9, "x après plusieurs updates");
        assertEquals(expectedY, particle.y, 1e-9, "y après plusieurs updates");
        assertEquals(expectedVY, particle.vy, 1e-9, "vy après plusieurs updates");
        assertEquals(expectedLifetime, particle.lifetime, "lifetime après plusieurs updates");
    }
}
