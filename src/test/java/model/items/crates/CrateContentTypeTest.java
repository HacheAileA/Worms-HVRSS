package model.items.crates;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import model.GameModel;
import model.items.Item;
import model.items.guns.*;
import model.items.tools.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

class CrateContentTypeTest {

    private GameModel mockModel;

    @BeforeEach
    void setUp() {
        mockModel = mock(GameModel.class);
    }

    @Test
    void testBazookaCrate() {
        ArrayList<Item> items = CrateContentType.BAZOOKA_CRATE.createContents(mockModel);
        assertEquals(1, items.size());
        assertTrue(items.get(0) instanceof Bazooka);
        assertEquals(1, ((Bazooka) items.get(0)).getAmmo());
    }

    @Test
    void testShotgunCrate() {
        ArrayList<Item> items = CrateContentType.SHOTGUN_CRATE.createContents(mockModel);
        assertEquals(1, items.size());
        assertTrue(items.get(0) instanceof ShotGun);
        assertEquals(3, ((ShotGun) items.get(0)).getAmmo());
    }

    @Test
    void testSniperCrate() {
        ArrayList<Item> items = CrateContentType.SNIPER_CRATE.createContents(mockModel);
        assertEquals(1, items.size());
        assertTrue(items.get(0) instanceof Sniper);
        assertEquals(2, ((Sniper) items.get(0)).getAmmo());
    }

    @Test
    void testGrenadesCrate() {
        ArrayList<Item> items = CrateContentType.GRENADES_CRATE.createContents(mockModel);
        assertEquals(1, items.size());
        assertTrue(items.get(0) instanceof Grenade);
        assertEquals(2, ((Grenade) items.get(0)).getAmmo());
    }

    @Test
    void testAirstrikeCrate() {
        ArrayList<Item> items = CrateContentType.AIRSTRIKE_CRATE.createContents(mockModel);
        assertEquals(1, items.size());
        assertTrue(items.get(0) instanceof AirStrike);
        assertEquals(1, ((AirStrike) items.get(0)).getAmmo());
    }

    @Test
    void testHealthPackCrate() {
        ArrayList<Item> items = CrateContentType.HEALTH_PACK_CRATE.createContents(mockModel);
        assertEquals(1, items.size());
        assertTrue(items.get(0) instanceof HealthPack);
        assertEquals(1, ((HealthPack) items.get(0)).getAmmo());
    }

    @Test
    void testRandomTpCrate() {
        ArrayList<Item> items = CrateContentType.RANDOM_TP_CRATE.createContents(mockModel);
        assertEquals(1, items.size());
        assertTrue(items.get(0) instanceof RandomTp);
        assertEquals(1, ((RandomTp) items.get(0)).getAmmo());
    }

    @Test
    void testGetRandomTypeReturnsValidValue() {
        Random random = mock(Random.class);
        when(random.nextInt(anyInt())).thenReturn(0);

        CrateContentType type = CrateContentType.getRandomType(random);
        assertNotNull(type);
        assertTrue(type instanceof CrateContentType);
        assertEquals(CrateContentType.values()[0], type);
    }
}
