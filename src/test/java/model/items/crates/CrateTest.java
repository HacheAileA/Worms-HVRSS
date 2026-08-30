package model.items.crates;

import static org.junit.jupiter.api.Assertions.*;

import model.items.Item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class CrateTest {

    private Crate crate;
    private ArrayList<Item> contents;

    @BeforeEach
    void setUp() {
        contents = new ArrayList<>();
        crate = new Crate(10, contents);
    }

    @Test
    void testInitialPosition() {
        assertEquals(10, crate.getX());
        assertEquals(0, crate.getY());
        assertEquals(10, crate.getMapX());
        assertEquals(0, crate.getMapY());
    }

    @Test
    void testContents() {
        assertSame(contents, crate.getContents());
        assertTrue(crate.getContents().isEmpty());
    }

    @Test
    void testCollectedFlag() {
        assertFalse(crate.isCollected());
        crate.collect();
        assertTrue(crate.isCollected());
    }

    @Test
    void testOnGroundFlag() {
        assertFalse(crate.isOnGround());

        crate.setOnGround(true);
        assertTrue(crate.isOnGround());

        crate.update(1);
        assertEquals(0, crate.getY());
    }

    @Test
    void testUpdateGravity() {
        crate.update(1);
        assertEquals(0, crate.getY());

        for (int i = 0; i < 10; i++) {
            crate.update(1);
        }
        assertTrue(crate.getY() > 0);
    }

    @Test
    void testVelocityCappedAtTerminalVelocity() {
        for (int i = 0; i < 20; i++) {
            crate.update(10);
        }
        assertTrue(crate.getY() > 0);
    }

    @Test
    void testSetY() {
        crate.setY(50);
        assertEquals(50, crate.getY());
    }

    @Test
    void testSetOnGroundResetsVelocity() {
        crate.update(1);
        crate.setOnGround(true);
        int yBefore = crate.getY();
        crate.update(1);
        assertEquals(yBefore, crate.getY(), "Y should not change when on ground");
    }
}
