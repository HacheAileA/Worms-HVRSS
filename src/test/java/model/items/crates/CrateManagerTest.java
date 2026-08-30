package model.items.crates;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import model.GameModel;
import model.Map;
import model.items.Item;
import model.items.Inventory;
import model.items.guns.Bazooka;
import model.players.Team;
import model.players.Worm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;

class CrateManagerTest {

    private CrateManager crateManager;
    private GameModel mockModel;
    private Map mockMap;
    private Team mockTeam;
    private Worm mockWorm;
    private Inventory mockInventory;

    @BeforeEach
    void setUp() {
        crateManager = new CrateManager(1, 1);
        mockModel = mock(GameModel.class);
        mockMap = mock(Map.class);
        mockTeam = mock(Team.class);
        mockWorm = mock(Worm.class);
        mockInventory = mock(Inventory.class);

        when(mockMap.getWidth()).thenReturn(20);
        when(mockMap.getHeight()).thenReturn(20);
        when(mockMap.isGround(anyInt(), anyInt())).thenReturn(false);
        when(mockMap.isWater(anyInt(), anyInt())).thenReturn(false);

        ArrayList<Worm> worms = new ArrayList<>();
        worms.add(mockWorm);

        ArrayList<Team> teams = new ArrayList<>();
        teams.add(mockTeam);

        mockModel.setTeams(teams);
        when(mockTeam.getWorms()).thenReturn(worms);
        when(mockTeam.getInventory()).thenReturn(mockInventory);

        when(mockWorm.getX()).thenReturn(5.0);
        mockWorm.setY(0);

        mockWorm.setTeam(mockTeam);
    }

    @Test
    void testOnTurnEndSpawnsCrateAfterCycle() {
        crateManager.onTurnEnd(mockMap, mockModel);
        assertEquals(1, crateManager.getActiveCrates().size());
    }

    @Test
    void testUpdateCratesRemovesCollected() {
        Crate crate = new Crate(0, new ArrayList<>());
        crate.collect();
        crateManager.getActiveCrates().add(crate);

        crateManager.updateCrates(1.0, mockMap, mockModel);
        assertEquals(0, crateManager.getActiveCrates().size());
    }

    @Test
    void testUpdateCratesRemovesOutOfBounds() {
        Crate crate = new Crate(-1, new ArrayList<>());
        crateManager.getActiveCrates().add(crate);

        crateManager.updateCrates(1.0, mockMap, mockModel);
        assertEquals(0, crateManager.getActiveCrates().size());
    }

    @Test
    void testUpdateCratesRemovesIfInWater() {
        Crate crate = new Crate(5, new ArrayList<>());
        crate.setY(5);
        crateManager.getActiveCrates().add(crate);

        when(mockMap.isWater(5, 5)).thenReturn(true);

        crateManager.updateCrates(1.0, mockMap, mockModel);
        assertEquals(0, crateManager.getActiveCrates().size());
    }

    @Test
    void testUpdateCratesSetsOnGroundIfGroundBelow() {
        Crate crate = new Crate(5, new ArrayList<>());
        crate.setY(0);
        crateManager.getActiveCrates().add(crate);

        when(mockMap.isGround(5, 1)).thenReturn(true);

        crateManager.updateCrates(1.0, mockMap, mockModel);

        assertTrue(crate.isOnGround());
        assertEquals(0, crate.getY());
    }

    @Test
    void testUpdateCratesCollectsCrateWhenWormNearby() {
        Bazooka bazooka = new Bazooka(mockModel);
        ArrayList<Item> contents = new ArrayList<>();
        contents.add(bazooka);
        Crate crate = new Crate(5, contents);
        crate.setY(0);
        crateManager.getActiveCrates().add(crate);

        when(mockWorm.getX()).thenReturn(5.0);
        mockWorm.setY(0);

        crateManager.updateCrates(1.0, mockMap, mockModel);

        assertEquals(1, crateManager.getActiveCrates().size());
    }

    @Test
    void testResetClearsActiveCrates() {
        crateManager.getActiveCrates().add(new Crate(0, new ArrayList<>()));
        crateManager.reset();
        assertEquals(0, crateManager.getActiveCrates().size());
    }

    @Test
    void testCollectCrateAddsItemsToInventory() throws Exception {
        Bazooka bazooka = new Bazooka(mockModel);
        ArrayList<Item> contents = new ArrayList<>();
        contents.add(bazooka);

        Crate crate = new Crate(0, contents);

        when(mockTeam.getInventory()).thenReturn(mockInventory);
        when(mockInventory.getGun(Bazooka.class)).thenReturn(null);

        Method method = CrateManager.class.getDeclaredMethod("collectCrate", Crate.class, Team.class);
        method.setAccessible(true);
        method.invoke(crateManager, crate, mockTeam);

        verify(mockInventory).addItem(bazooka);
    }

    @Test
    void testCollectCrateIncreasesAmmoIfAlreadyExists() throws Exception {
        Bazooka existingBazooka = mock(Bazooka.class);
        Bazooka newBazooka = new Bazooka(mockModel);
        newBazooka.setAmmo(5);

        when(mockTeam.getInventory()).thenReturn(mockInventory);
        when(mockInventory.getGun(Bazooka.class)).thenReturn(existingBazooka);

        Method method = CrateManager.class.getDeclaredMethod("collectCrate", Crate.class, Team.class);
        method.setAccessible(true);
        ArrayList<Item> contents = new ArrayList<>();
        contents.add(newBazooka);
        Crate crate = new Crate(0, contents);
        method.invoke(crateManager, crate, mockTeam);

        verify(existingBazooka).setAmmo(anyInt());
    }

    @Test
    void testFindWormNearCrateReturnsWormWhenOnSamePosition() throws Exception {
        when(mockWorm.getX()).thenReturn(5.0);
        when(mockWorm.getY()).thenReturn(0.0);

        ArrayList<Worm> worms = new ArrayList<>();
        worms.add(mockWorm);
        when(mockTeam.getWorms()).thenReturn(worms);

        ArrayList<Team> teams = new ArrayList<>();
        teams.add(mockTeam);
        when(mockModel.getTeams()).thenReturn(teams);

        Method method = CrateManager.class.getDeclaredMethod("findWormNearCrate", GameModel.class,
                int.class, int.class);
        method.setAccessible(true);

        Worm result = (Worm) method.invoke(crateManager, mockModel, 5, 0);
        assertEquals(mockWorm, result);
    }

    @Test
    void testFindWormNearCrateReturnsWormWhenAdjacent() throws Exception {
        when(mockWorm.getX()).thenReturn(5.0);
        when(mockWorm.getY()).thenReturn(0.0);

        ArrayList<Worm> worms = new ArrayList<>();
        worms.add(mockWorm);
        when(mockTeam.getWorms()).thenReturn(worms);

        ArrayList<Team> teams = new ArrayList<>();
        teams.add(mockTeam);
        when(mockModel.getTeams()).thenReturn(teams);

        Method method = CrateManager.class.getDeclaredMethod("findWormNearCrate", GameModel.class,
                int.class, int.class);
        method.setAccessible(true);

        Worm result = (Worm) method.invoke(crateManager, mockModel, 6, 1);
        assertEquals(mockWorm, result);
    }

    @Test
    void testFindWormNearCrateReturnsNullWhenNoWormNearby() throws Exception {
        when(mockWorm.getX()).thenReturn(5.0);
        when(mockWorm.getY()).thenReturn(0.0);

        ArrayList<Worm> worms = new ArrayList<>();
        worms.add(mockWorm);
        when(mockTeam.getWorms()).thenReturn(worms);

        ArrayList<Team> teams = new ArrayList<>();
        teams.add(mockTeam);
        when(mockModel.getTeams()).thenReturn(teams);

        Method method = CrateManager.class.getDeclaredMethod("findWormNearCrate", GameModel.class,
                int.class, int.class);
        method.setAccessible(true);

        Worm result = (Worm) method.invoke(crateManager, mockModel, 10, 10);
        assertNull(result);
    }
}
