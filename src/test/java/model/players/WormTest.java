package model.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyChar;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import model.GameModel;
import model.Map;
import model.items.Inventory;
import model.items.Item;
import model.items.guns.Guns;
import model.items.tools.AirStrike;
import model.items.tools.Tools;
import model.physics.Projectile;
import view.gui.GuiView;

class WormTest {

    private Team team;
    private Worm worm;
    private GameModel model;

    @BeforeEach
    void setUp() {
        model = mock(GameModel.class);
        team = new Team("Team", 0, 0, model);
        worm = new Worm(team, "TestWorm", 'W');
        worm.setName("TestWorm");
    }

    @Test
    void testConstructors() {
        Worm worm2 = new Worm(team, "Worm2", 'X');
        Worm worm3 = new Worm(team, "Worm3", 'Y');
        Worm worm4 = new Worm(team, null, 'Z');

        assertNotNull(worm2, "Le constructeur avec équipe et nom doit créer un worm");
        assertNotNull(worm3, "Le constructeur avec équipe, nom, PV et caractère doit créer un worm");
        assertNotNull(worm4, "Le constructeur avec équipe, nom, PV et caractère doit créer un worm");
    }

    @Test
    void testGetAndSetName() {
        assertEquals("TestWorm", worm.getName(), "Le nom initial doit être correct");
        worm.setName("NewName");
        assertEquals("NewName", worm.getName(), "Le nom doit être modifié correctement");
    }

    @Test
    void testGetAndSetLifePoints() {
        assertEquals(100, worm.getHp(), "Les points de vie initiaux doivent être corrects");
        worm.setHp(50);
        assertEquals(50, worm.getHp(), "Les points de vie doivent être modifiés correctement");
    }

    @Test
    void testIsDead() {
        assertFalse(worm.isDead(), "Le worm ne doit pas être mort à 100 PV");
        worm.setHp(0);
        assertTrue(worm.isDead(), "Le worm doit être mort à 0 PV");
        worm.setHp(-10);
        assertTrue(worm.isDead(), "Le worm doit être mort avec PV négatif");
    }

    @Test
    void testSetLifePointsNegative() {
        worm.setHp(-50);
        assertEquals(-50, worm.getHp(), "Les PV négatifs doivent être acceptés");
        assertTrue(worm.isDead(), "Le worm doit être mort si PV négatifs");
    }

    @Test
    void testGetSymbol() {
        assertEquals('W', worm.getSymbol(), "Le symbole doit être correct");
    }

    @Test
    void testJumpImpossible() {
        worm.setPosition(0, 0);
        Map map = mock(Map.class);
        boolean toLeft = true;
        worm.jump(map, toLeft, mock(GameModel.class));
        assertEquals(0, worm.getX(), "La position X doit rester la même après le saut mocké");
        assertEquals(0, worm.getY(), "La position Y doit rester la même après le saut mocké");
    }

    @Test
    void testJumpImpossibleOutOfBounds() {
        worm.setPosition(0, 0);
        Map map = mock(Map.class);

        boolean result = worm.jump(map, true, mock(GameModel.class));

        assertFalse(result, "Le saut en dehors de la grille doit être impossible");
        assertEquals(0, worm.getX());
        assertEquals(0, worm.getY());
    }

    @Test
    void testJumpPossible() {
        worm.setPosition(1, 1);
        Map map = mock(Map.class);
        when(map.getWidth()).thenReturn(10);
        when(map.getHeight()).thenReturn(10);
        when(map.isEmpty(0, 0)).thenReturn(true);

        boolean result = worm.jump(map, true, mock(GameModel.class));

        assertTrue(result, "Le saut vers une cellule libre doit réussir");
        assertEquals(0, worm.getX());
        assertEquals(0, worm.getY());
        verify(map).setCell(1, 1, ' ');
        verify(map).setCell(0, 0, worm.getSymbol());
    }

    @Test
    void testJumpLeft() {
        worm.setPosition(0, 0);
        Map map = mock(Map.class);
        worm.jumpLeft(map, mock(GameModel.class));
        assertEquals(0, worm.getX(), "La position X doit rester la même après le saut mocké");
        assertEquals(0, worm.getY(), "La position Y doit rester la même après le saut mocké");
    }

    @Test
    void testJumpRight() {
        worm.setPosition(0, 0);
        Map map = mock(Map.class);
        worm.jumpRight(map, mock(GameModel.class));
        assertEquals(0, worm.getX(), "La position X doit rester la même après le saut mocké");
        assertEquals(0, worm.getY(), "La position Y doit rester la même après le saut mocké");
    }

    @Test
    void testMoveLeftImpossibleOutOfBounds() {
        worm.setPosition(0, 0);
        Map map = mock(Map.class);

        boolean result = worm.moveLeft(map);

        assertFalse(result, "Le déplacement à gauche hors de la grille doit être impossible");
        assertEquals(0, worm.getX());
        assertEquals(0, worm.getY());
    }

    @Test
    void testMoveLeftImpossibleCellNotEmpty() {
        worm.setPosition(5, 0);
        Map map = mock(Map.class);
        when(map.isEmpty(4, 0)).thenReturn(false);

        boolean result = worm.moveLeft(map);

        assertFalse(result, "Le déplacement vers une cellule non vide doit être impossible");
        assertEquals(5, worm.getX());
        assertEquals(0, worm.getY());
    }

    @Test
    void testMoveRightImpossibleOutOfBounds() {
        worm.setPosition(9, 0);
        Map map = mock(Map.class);
        when(map.getWidth()).thenReturn(10);

        boolean result = worm.moveRight(map);

        assertFalse(result, "Le déplacement à droite hors de la grille doit être impossible");
        assertEquals(9, worm.getX());
        assertEquals(0, worm.getY());
    }

    @Test
    void testMoveRightImpossibleCellNotEmpty() {
        worm.setPosition(5, 0);
        Map map = mock(Map.class);
        when(map.getWidth()).thenReturn(10);
        when(map.isEmpty(6, 0)).thenReturn(false);

        boolean result = worm.moveRight(map);

        assertFalse(result, "Le déplacement vers une cellule non vide doit être impossible");
        assertEquals(5, worm.getX());
        assertEquals(0, worm.getY());
    }

    @Test
    void testShoot() {
        assertDoesNotThrow(() -> worm.shoot(mock(Map.class), 0.0));
    }

    @Test
    void testSetPosition() {
        worm.setPosition(10, 20);
        assertEquals(10, worm.getX(), "La position X doit être définie correctement");
        assertEquals(20, worm.getY(), "La position Y doit être définie correctement");
    }

    @Test
    void testPlaceOnMapRandomlyFail() {
        Map map = mock(Map.class);
        when(map.getWidth()).thenReturn(2);
        when(map.getHeight()).thenReturn(2);

        when(map.isEmpty(anyInt(), anyInt())).thenReturn(false);

        assertEquals(0, worm.getX());
        assertEquals(0, worm.getY());
    }

    @Test
    void testPlaceOnMapRandomlyFail2() {
        Map map = mock(Map.class);
        when(map.getWidth()).thenReturn(1);
        when(map.getHeight()).thenReturn(2);

        when(map.isEmpty(anyInt(), anyInt())).thenReturn(true);
        when(map.isGround(anyInt(), anyInt())).thenReturn(false);
        when(map.isWater(anyInt(), anyInt())).thenReturn(false);

        assertTrue(worm.getY() < map.getHeight());
    }

    @Test
    void testPlaceOnMapRandomlyFail3() {
        Map map = mock(Map.class);
        when(map.getWidth()).thenReturn(3);
        when(map.getHeight()).thenReturn(3);

        when(map.isEmpty(1, 0)).thenReturn(true);
        when(map.isGround(1, 1)).thenReturn(true);
        when(map.isWater(1, 1)).thenReturn(true);

        verify(map, never()).setCell(1, 0, worm.getSymbol());
    }

    @Test
    void testApplyGravityFallsOneStep() throws Exception {
        worm.setPosition(5, 0);
        Map map = mock(Map.class);
        when(map.getHeight()).thenReturn(3);
        when(map.isEmpty(5, 1)).thenReturn(true);
        when(map.isEmpty(5, 2)).thenReturn(false);
        when(map.isWater(anyInt(), anyInt())).thenReturn(false);
        assertEquals(0, worm.getY(), "Le worm doit tomber d'une case");
    }

    @Test
    void testSetY() {
        Worm worm = new Worm("TestWorm", 100, 'W', 0);

        worm.setY(42.5);

        assertEquals(42.5, worm.getY(), "setY should update the y-coordinate");
    }

    @Test
    void testGetTeam() {
        Team mockTeam = mock(Team.class);
        Worm worm = new Worm("TestWorm", 100, 'W', 0);

        worm.setTeam(mockTeam);
        assertEquals(mockTeam, worm.getTeam(), "getTeam should return the team assigned to the worm");
    }

    @Test
    void testGetSelectedItem() {
        Worm worm = new Worm("TestWorm", 100, 'W', 0);
        Item mockItem = mock(Item.class);

        worm.setSelectedItem(mockItem);

        assertEquals(mockItem, worm.getSelectedItem(), "getSelectedItem should return the item assigned");
    }

    @Test
    void testIsFacingWestWithReflection() throws Exception {
        Worm worm = new Worm("TestWorm", 100, 'W', 0);

        java.lang.reflect.Field field = Worm.class.getDeclaredField("facingWest");
        field.setAccessible(true);

        field.setBoolean(worm, true);
        assertTrue(worm.isFacingWest(), "Worm should be facing west");

        field.setBoolean(worm, false);
        assertFalse(worm.isFacingWest(), "Worm should not be facing west");
    }

    @Test
    void jumpSmoothwhenOnGroundshouldStartJump() {
        Map mockMap = mock(Map.class);
        when(mockMap.isGround(anyInt(), anyInt())).thenReturn(true);

        double yBefore = worm.getY();

        worm.jumpSmooth(mockMap);

        assertEquals(yBefore, worm.getY(), "Y ne change pas immédiatement après jumpSmooth");
        worm.jumpSmooth(mockMap);
        assertEquals(yBefore, worm.getY(), "Jump ne doit pas s'exécuter si already jumping");
    }

    @Test
    void jumpSmoothwhenNotOnGroundshouldDoNothing() {
        Map mockMap = mock(Map.class);
        when(mockMap.isGround(anyInt(), anyInt())).thenReturn(false);

        double yBefore = worm.getY();

        worm.jumpSmooth(mockMap);

        assertEquals(yBefore, worm.getY(), "Y ne change pas si le worm n'est pas sur le sol");
    }

    @Test
    void startMoveLeftshouldNotMoveOutOfMap() {
        Map mockMap = mock(Map.class);
        GuiView mockView = mock(GuiView.class);

        worm.setPosition(0, 5);
        worm.startMoveLeft();

        worm.update(mockMap, mockView);

        assertEquals(0, worm.getX(), "Worm should not move left beyond map boundary");
    }

    @Test
    void startMoveRightshouldNotMoveOutOfMap() {
        Map mockMap = mock(Map.class);
        GuiView mockView = mock(GuiView.class);
        worm.setPosition(9, 5);
        worm.startMoveRight();

        worm.update(mockMap, mockView);

        assertEquals(9, worm.getX(), "Worm should not move right beyond map boundary");
    }

    @Test
    void stopMoveshouldPreventHorizontalMovement() {
        Map mockMap = mock(Map.class);
        GuiView mockView = mock(GuiView.class);
        double xBefore, xAfter;

        worm.startMoveRight();
        xBefore = worm.getX();

        worm.stopMove();
        xBefore = worm.getX();

        worm.update(mockMap, mockView);
        xAfter = worm.getX();
        assertEquals(xBefore, xAfter, "Worm should not move horizontally after stopMove");
    }

    @Test
    void shootnotOnGroundreturnsNull() {
        Worm worm = spy(new Worm("Test", 100, 'W', 0));
        Map map = mock(Map.class);

        doReturn(false).when(worm).isOnGround(map);

        Projectile result = worm.shoot(map, 50);

        assertNull(result, "shoot should return null when worm is not on ground");
    }

    @Test
    void shootnoSelectedItemreturnsNull() {
        Worm worm = spy(new Worm("Test", 100, 'W', 0));
        Map map = mock(Map.class);

        doReturn(true).when(worm).isOnGround(map);

        Projectile result = worm.shoot(map, 50);

        assertNull(result, "shoot should return null when no item is selected");
    }

    @Test
    void shootgunWithAmmoreturnsProjectile() {
        Worm worm = spy(new Worm("Test", 100, 'W', 0));
        Map map = mock(Map.class);

        doReturn(true).when(worm).isOnGround(map);

        Guns gun = mock(Guns.class);
        Projectile projectile = mock(Projectile.class);

        when(gun.hasAmmo()).thenReturn(true);
        when(gun.createProjectile(worm)).thenReturn(projectile);

        worm.setSelectedItem(gun);

        Projectile result = worm.shoot(map, 50);

        assertNotNull(result, "shoot should return a projectile when gun has ammo");
        assertEquals(projectile, result);
    }

    @Test
    void shootgunWithoutAmmoreturnsNull() {
        Worm worm = spy(new Worm("Test", 100, 'W', 0));
        Map map = mock(Map.class);

        doReturn(true).when(worm).isOnGround(map);

        Guns gun = mock(Guns.class);
        when(gun.hasAmmo()).thenReturn(false);

        worm.setSelectedItem(gun);

        Projectile result = worm.shoot(map, 50);

        assertNull(result, "shoot should return null when gun has no ammo");
    }

    @Test
    void shootairStrikereturnsProjectile() {
        Worm worm = spy(new Worm("Test", 100, 'W', 0));
        Map map = mock(Map.class);

        doReturn(true).when(worm).isOnGround(map);

        AirStrike airStrike = mock(AirStrike.class);
        Projectile projectile = mock(Projectile.class);

        when(airStrike.useTool(eq(worm), eq(100))).thenReturn(projectile);

        worm.setSelectedItem(airStrike);

        Projectile result = worm.shoot(map, 100);

        assertEquals(projectile, result, "shoot should return projectile from AirStrike");
    }

    @Test
    void shoottoolcallsUseToolAndReturnsNull() {
        Worm worm = spy(new Worm("Test", 100, 'W', 0));
        Map map = mock(Map.class);

        doReturn(true).when(worm).isOnGround(map);

        Tools tool = mock(Tools.class);
        worm.setSelectedItem(tool);

        Projectile result = worm.shoot(map, 50);

        verify(tool).useTool(worm);
        assertNull(result, "shoot should return null for non-AirStrike tools");
    }

    @Test
    void rotateAimpositiveDeltaincreasesAngle() {
        Worm worm = new Worm("Test", 100, 'W', 0);

        double initialAngle = worm.getAimAngle();
        worm.rotateAim(0.5);

        assertEquals(initialAngle + 0.5, worm.getAimAngle(), 1e-9);
    }

    @Test
    void placeOnMapAtwhenPlacementAllowedsetsPositionAndMapCell() {
        Map map = mock(Map.class);
        when(map.canWormBePlaced(5, 10)).thenReturn(true);

        Worm worm = new Worm("Test", 100, 'W', 0);

        worm.placeOnMapAt(5, 10, map);

        assertEquals(5, worm.getX());
        assertEquals(10, worm.getY());

        verify(map).setCell(5, 10, 'W');
    }

    @Test
    void placeOnMapAtwhenPlacementDenieddoesNothing() {
        Map map = mock(Map.class);
        when(map.canWormBePlaced(5, 10)).thenReturn(false);

        Worm worm = new Worm("Test", 100, 'W', 0);

        worm.setPosition(1, 2);

        worm.placeOnMapAt(5, 10, map);

        assertEquals(1, worm.getX());
        assertEquals(2, worm.getY());

        verify(map, never()).setCell(anyInt(), anyInt(), anyChar());
    }

    @Test
    void jumpLeftmovesWormAndUpdatesMap() {
        Map map = mock(Map.class);

        when(map.getWidth()).thenReturn(20);
        when(map.isEmpty(4, 4)).thenReturn(true);

        Worm worm = new Worm(team, "Worm", 'W');
        worm.setPosition(5, 5);

        boolean result = worm.jumpLeft(map, mock(GameModel.class));

        assertTrue(result);

        verify(map).setCell(5, 5, ' ');
        verify(map).setCell(4, 4, 'W');
    }

    @Test
    void collectCratetoolAlreadyOwnedaddsAmmo() {
        Inventory inventory = mock(Inventory.class);
        Team team = mock(Team.class);
        when(team.getInventory()).thenReturn(inventory);

        Worm worm = new Worm(team, "Worm", 'W');

        Tools ownedTool = mock(Tools.class);
        Tools newTool = mock(Tools.class);

        when(inventory.getTool(newTool.getClass())).thenReturn(ownedTool);
        when(ownedTool.getAmmo()).thenReturn(5);
        when(newTool.getAmmo()).thenReturn(3);

        worm.collectCrate(newTool);

        verify(ownedTool).setAmmo(8);
        verify(inventory, never()).addItem((Guns) any());
        verify(inventory, never()).addItem((Tools) any());

    }

    @Test
    void collectCrategunAlreadyOwnedaddsAmmo() {
        Inventory inventory = mock(Inventory.class);
        Team team = mock(Team.class);
        when(team.getInventory()).thenReturn(inventory);

        Worm worm = new Worm(team, "Worm", 'W');

        Guns ownedGun = mock(Guns.class);
        Guns newGun = mock(Guns.class);

        when(inventory.getGun(newGun.getClass())).thenReturn(ownedGun);
        when(ownedGun.getAmmo()).thenReturn(10);
        when(newGun.getAmmo()).thenReturn(5);

        worm.collectCrate(newGun);

        verify(ownedGun).setAmmo(15);
        verify(inventory, never()).addItem((Guns) any());
        verify(inventory, never()).addItem((Tools) any());

    }

    @Test
    void collectCrategunNotOwnedaddsGunToInventory() {
        Inventory inventory = mock(Inventory.class);
        Team team = mock(Team.class);
        when(team.getInventory()).thenReturn(inventory);

        Worm worm = new Worm(team, "Worm", 'W');

        Guns gun = mock(Guns.class);
        when(inventory.getGun(gun.getClass())).thenReturn(null);

        worm.collectCrate(gun);

        verify(inventory).addItem(gun);
    }

    @Test
    void applyFallDamageaboveThresholdreducesHpCorrectly() throws Exception {
        Worm worm = new Worm("Worm", 100, 'W', 0);

        Method method = Worm.class.getDeclaredMethod("applyFallDamage", int.class);
        method.setAccessible(true);

        method.invoke(worm, 3);

        assertEquals(90, worm.getHp());
    }

    @Test
    void applyGravitywhenWaterBelowsetsHpToZeroAndStops() throws Exception {
        Map map = mock(Map.class);
        Worm worm = new Worm("Worm", 100, 'W', 0);

        worm.setPosition(2, 2);

        when(map.getHeight()).thenReturn(10);

        when(map.isEmpty(2, 3)).thenReturn(true);

        when(map.isWater(2, 3)).thenReturn(true);

        Method method = Worm.class.getDeclaredMethod("applyGravity", Map.class);
        method.setAccessible(true);

        method.invoke(worm, map);

        assertEquals(0, worm.getHp(), "Worm should die immediately when falling into water");

        assertEquals(2, worm.getX());
        assertEquals(2, worm.getY());

        verify(map, never()).setCell(anyInt(), anyInt(), anyChar());
    }

    @Test
    void collectToolwhenToolNotOwnedaddsToolToInventory() throws Exception {
        Inventory inventory = mock(Inventory.class);
        Team team = mock(Team.class);
        when(team.getInventory()).thenReturn(inventory);

        Tools tool = mock(Tools.class);

        when(inventory.getTool(tool.getClass())).thenReturn(null);

        Worm worm = new Worm("Worm", 100, 'W', 0);
        worm.setTeam(team);

        Method method = Worm.class.getDeclaredMethod("collectTool", Tools.class);
        method.setAccessible(true);

        method.invoke(worm, tool);

        verify(inventory).addItem(tool);
        verify(tool, never()).setAmmo(anyInt());
    }

    @Test
    void handleFallDamageappliesDamageWithoutDeath() throws Exception {
        Map map = mock(Map.class);
        GuiView view = mock(GuiView.class);

        Worm worm = new Worm("Test", 100, 'W', 0);

        worm.setY(5);
        Field jumpStartYField = Worm.class.getDeclaredField("jumpStartY");
        jumpStartYField.setAccessible(true);
        jumpStartYField.setDouble(worm, 0);

        Field maxJumpHeightField = Worm.class.getDeclaredField("maxJumpHeight");
        maxJumpHeightField.setAccessible(true);
        maxJumpHeightField.setDouble(worm, 0);

        int hpBefore = worm.getHp();

        Method method = Worm.class.getDeclaredMethod("handleFallDamage", Map.class, GuiView.class);
        method.setAccessible(true);

        method.invoke(worm, map, view);

        assertTrue(worm.getHp() < hpBefore, "Worm should take fall damage");
        verify(map, never()).setCell(anyInt(), anyInt(), anyChar());
        verify(view, never()).handleTurnEnd();
    }

    @Test
    void handleFallDamageappliesDamageAndKillsWorm() throws Exception {
        Map map = mock(Map.class);
        GuiView view = mock(GuiView.class);

        Worm worm = new Worm("Test", 10, 'W', 0);

        worm.setY(5);
        Field jumpStartYField = Worm.class.getDeclaredField("jumpStartY");
        jumpStartYField.setAccessible(true);
        jumpStartYField.setDouble(worm, 0);

        Field maxJumpHeightField = Worm.class.getDeclaredField("maxJumpHeight");
        maxJumpHeightField.setAccessible(true);
        maxJumpHeightField.setDouble(worm, 0);

        Method method = Worm.class.getDeclaredMethod("handleFallDamage", Map.class, GuiView.class);
        method.setAccessible(true);

        method.invoke(worm, map, view);

        assertTrue(worm.isDead(), "Worm should be dead");

        verify(map).setCell((int) worm.getX(), (int) worm.getY(), ' ');
        verify(view).handleTurnEnd();
    }

    @Test
    void handleLandingappliesFallDamageAndResetsState() throws Exception {
        Map map = mock(Map.class);
        GuiView view = mock(GuiView.class);

        Worm worm = new Worm("Test", 100, 'W', 0);

        Field jumpingField = Worm.class.getDeclaredField("jumping");
        jumpingField.setAccessible(true);
        jumpingField.setBoolean(worm, true);

        Field verticalField = Worm.class.getDeclaredField("verticalVelocity");
        verticalField.setAccessible(true);
        verticalField.setDouble(worm, 5.0);

        Field yField = Worm.class.getDeclaredField("y");
        yField.setAccessible(true);
        yField.setDouble(worm, 10.0);

        Field maxJumpField = Worm.class.getDeclaredField("maxJumpHeight");
        maxJumpField.setAccessible(true);
        maxJumpField.setDouble(worm, 5.0);

        Field jumpStartField = Worm.class.getDeclaredField("jumpStartY");
        jumpStartField.setAccessible(true);
        jumpStartField.setDouble(worm, 0.0);

        Worm spyWorm = spy(worm);
        doReturn(true).when(spyWorm).isOnGround(map);

        Method method = Worm.class.getDeclaredMethod("handleLanding", Map.class, GuiView.class);
        method.setAccessible(true);
        method.invoke(spyWorm, map, view);

        assertFalse(jumpingField.getBoolean(spyWorm));
        assertEquals(0.0, verticalField.getDouble(spyWorm));

        assertTrue(spyWorm.getHp() < 100);
    }

    @Test
    void testCheckWaterDeathWhenNotInWater() throws Exception {
        Map map = mock(Map.class);
        when(map.isWater(anyInt(), anyInt())).thenReturn(false);

        GuiView view = mock(GuiView.class);

        Worm worm = new Worm("Test", 100, 'W', 0);

        Field xField = Worm.class.getDeclaredField("x");
        xField.setAccessible(true);
        xField.setDouble(worm, 2.0);

        Field yField = Worm.class.getDeclaredField("y");
        yField.setAccessible(true);
        yField.setDouble(worm, 3.0);

        Field hpField = Worm.class.getDeclaredField("hp");
        hpField.setAccessible(true);

        Method method = Worm.class.getDeclaredMethod("checkWaterDeath", Map.class, GuiView.class);
        method.setAccessible(true);

        boolean result = (boolean) method.invoke(worm, map, view);

        assertFalse(result, "Should return false when worm is not in water");
        assertEquals(100, hpField.getInt(worm), "HP should remain unchanged");
        verify(map, never()).setCell(anyInt(), anyInt(), anyChar());
        verify(view, never()).handleTurnEnd();
        if (view.soundPlayer != null) {
            verify(view.soundPlayer, never()).playSoundEffect(anyString());
        }
    }

    @Test
    void testGetHitBoxAtOrigin() {
        Worm worm = new Worm("TestWorm", 100, 'W', 0);
        worm.setPosition(0, 0);

        Rectangle hitBox = worm.getHitBox();

        assertEquals(0, hitBox.x, "La hitbox X doit correspondre à la position X du worm");
        assertEquals(0, hitBox.y, "La hitbox Y doit correspondre à la position Y du worm");
        assertEquals(1, hitBox.width, "La hitbox doit avoir une largeur de 1");
        assertEquals(1, hitBox.height, "La hitbox doit avoir une hauteur de 1");
    }

    @Test
    void testGetHitBoxAtPositivePosition() {
        Worm worm = new Worm("TestWorm", 100, 'W', 0);
        worm.setPosition(5, 7);

        Rectangle hitBox = worm.getHitBox();

        assertEquals(5, hitBox.x, "La hitbox X doit correspondre à la position X du worm");
        assertEquals(7, hitBox.y, "La hitbox Y doit correspondre à la position Y du worm");
        assertEquals(1, hitBox.width, "La hitbox doit avoir une largeur de 1");
        assertEquals(1, hitBox.height, "La hitbox doit avoir une hauteur de 1");
    }

    @Test
    void testGetHitBoxAtFractionalPosition() {
        Worm worm = new Worm("TestWorm", 100, 'W', 0);
        worm.setPosition(3, 4);

        Rectangle hitBox = worm.getHitBox();

        assertEquals(3, hitBox.x, "La hitbox X doit être le cast en int de la position X");
        assertEquals(4, hitBox.y, "La hitbox Y doit être le cast en int de la position Y");
        assertEquals(1, hitBox.width, "La hitbox doit avoir une largeur de 1");
        assertEquals(1, hitBox.height, "La hitbox doit avoir une hauteur de 1");
    }

}
