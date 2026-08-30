package controller.bot;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.items.tools.AirStrike;
import model.items.tools.HealthPack;
import model.items.tools.RandomTp;
import model.items.tools.Tools;
import model.physics.Wind;
import model.players.Team;
import model.players.Worm;
import model.GameModel;
import model.Map;
import model.items.Inventory;
import model.items.guns.Bazooka;
import model.items.guns.Grenade;
import model.items.guns.Guns;
import model.items.guns.ShotGun;
import model.items.guns.Sniper;
import view.gui.GuiView;

class GuiTestBotDecisionEngineTest {

    private BotDecisionEngine engine;
    private Team team;
    private Map mockMap;
    private GameModel mockModel;
    private Inventory inventory;

    @BeforeEach
    void setup() {
        GuiView view = mock(GuiView.class);
        engine = new BotDecisionEngine(5, view);
        mockModel = mock(GameModel.class);
        mockMap = mock(Map.class);
        team = mock(Team.class);
        inventory = mock(Inventory.class);

        when(mockModel.getMap()).thenReturn(mockMap);
        when(mockMap.getWidth()).thenReturn(100);
        when(mockMap.getHeight()).thenReturn(100);

        when(team.getInventory()).thenReturn(inventory);
    }

    @Test
    void shouldReturnHealthPackWhenAvailableAndHasAmmo() throws Exception {
        Tools healthPack = mock(Tools.class);
        when(healthPack.getName()).thenReturn("Health Pack");
        when(healthPack.hasAmmo()).thenReturn(true);

        ArrayList<Tools> tools = new ArrayList<>();
        tools.add(healthPack);

        when(inventory.getAvailableTools()).thenReturn(tools);

        Tools result = invokeFindHealthPack();

        assertNotNull(result);
        assertEquals(healthPack, result);
    }

    @Test
    void shouldReturnNullWhenHealthPackHasNoAmmo() throws Exception {
        Tools healthPack = mock(Tools.class);
        when(healthPack.getName()).thenReturn("Health Pack");
        when(healthPack.hasAmmo()).thenReturn(false);

        ArrayList<Tools> tools = new ArrayList<>();
        tools.add(healthPack);

        when(inventory.getAvailableTools()).thenReturn(tools);

        Tools result = invokeFindHealthPack();

        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenNoHealthPackInInventory() throws Exception {
        Tools teleport = mock(Tools.class);
        when(teleport.getName()).thenReturn("RandomTP");
        when(teleport.hasAmmo()).thenReturn(true);

        ArrayList<Tools> tools = new ArrayList<>();
        tools.add(teleport);

        when(inventory.getAvailableTools()).thenReturn(tools);

        Tools result = invokeFindHealthPack();

        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenInventoryIsEmpty() throws Exception {
        when(inventory.getAvailableTools()).thenReturn(new ArrayList<>());

        Tools result = invokeFindHealthPack();

        assertNull(result);
    }

    @Test
    void shouldIgnoreCaseWhenMatchingHealthPackName() throws Exception {
        Tools healthPack = mock(Tools.class);
        when(healthPack.getName()).thenReturn("hEaLtH pAcK");
        when(healthPack.hasAmmo()).thenReturn(true);

        ArrayList<Tools> tools = new ArrayList<>();
        tools.add(healthPack);

        when(inventory.getAvailableTools()).thenReturn(tools);

        Tools result = invokeFindHealthPack();

        assertNotNull(result);
        assertEquals(healthPack, result);
    }

    @Test
    void testDistanceZero() throws Exception {
        Method method = BotDecisionEngine.class.getDeclaredMethod(
                "getDistance", double.class, double.class, double.class, double.class);
        method.setAccessible(true);

        double result = (double) method.invoke(engine, 0.0, 0.0, 0.0, 0.0);
        assertEquals(0.0, result, 1e-6, "Distance entre mêmes points doit être 0");
    }

    @Test
    void testDistancePositive() throws Exception {
        Method method = BotDecisionEngine.class.getDeclaredMethod(
                "getDistance", double.class, double.class, double.class, double.class);
        method.setAccessible(true);

        double result = (double) method.invoke(engine, 0.0, 0.0, 3.0, 4.0);
        assertEquals(5.0, result, 1e-6, "Distance entre (0,0) et (3,4) doit être 5");
    }

    @Test
    void testDistanceNegativeCoordinates() throws Exception {
        Method method = BotDecisionEngine.class.getDeclaredMethod(
                "getDistance", double.class, double.class, double.class, double.class);
        method.setAccessible(true);

        double result = (double) method.invoke(engine, -1.0, -2.0, -4.0, -6.0);
        assertEquals(5.0, result, 1e-6, "Distance entre points négatifs");
    }

    @Test
    void testDistanceMixedCoordinates() throws Exception {
        Method method = BotDecisionEngine.class.getDeclaredMethod(
                "getDistance", double.class, double.class, double.class, double.class);
        method.setAccessible(true);

        double result = (double) method.invoke(engine, -1.0, 2.0, 3.0, -2.0);
        assertEquals(5.656854, result, 1e-6, "Distance entre coordonnées mixtes");
    }

    @Test
    void testFindRandomTeleportNotNull() throws Exception {
        Method method = BotDecisionEngine.class.getDeclaredMethod(
                "findRandomTeleport", Team.class);
        method.setAccessible(true);

        Team mockTeam = mock(Team.class);
        Inventory mockInventory = mock(Inventory.class);
        when(mockTeam.getInventory()).thenReturn(mockInventory);

        Tools teleport = mock(Tools.class);
        when(teleport.getName()).thenReturn("RandomTP");
        when(teleport.hasAmmo()).thenReturn(true);

        ArrayList<Tools> tools = new ArrayList<>();
        tools.add(teleport);
        when(mockInventory.getAvailableTools()).thenReturn(tools);

        Object result = method.invoke(engine, mockTeam);

        assertNotNull(result, "Le téléport trouvé ne doit pas être null");
        assertEquals(teleport, result, "Le teleport renvoyé doit être celui de l'inventaire");
    }

    @Test
    void testFindRandomTeleportMultipleCalls() throws Exception {
        Method method = BotDecisionEngine.class.getDeclaredMethod(
                "findRandomTeleport", Team.class);
        method.setAccessible(true);

        Team mockTeam = mock(Team.class);
        Inventory mockInventory = mock(Inventory.class);
        when(mockTeam.getInventory()).thenReturn(mockInventory);

        Tools teleport1 = mock(Tools.class);
        when(teleport1.getName()).thenReturn("RandomTP");
        when(teleport1.hasAmmo()).thenReturn(true);

        ArrayList<Tools> tools = new ArrayList<>();
        tools.add(teleport1);
        when(mockInventory.getAvailableTools()).thenReturn(tools);

        Object t1 = method.invoke(engine, mockTeam);
        Object t2 = method.invoke(engine, mockTeam);

        assertNotNull(t1);
        assertNotNull(t2);
        assertEquals(teleport1, t1);
        assertEquals(teleport1, t2);
    }

    @Test
    void testFindAirStrike() throws Exception {
        GameModel model = new GameModel();
        Team team = new Team("aaa", 1, 0, model);
        Tools airStrike = new AirStrike(mock(GameModel.class));
        team.getInventory().addItem(airStrike);

        GuiView dummyView = null;
        BotDecisionEngine engine = new BotDecisionEngine(5, dummyView);

        java.lang.reflect.Method method = BotDecisionEngine.class.getDeclaredMethod("findAirStrike", Team.class);
        method.setAccessible(true);

        Tools result = (Tools) method.invoke(engine, team);

        assertNotNull(result);
        assertEquals("Air Strike", result.getName());
    }

    @Test
    void testFindDestructiveGun() throws Exception {
        GameModel model = new GameModel();
        Team team = new Team("aaa", 0, 0, model);

        Guns destructiveGun = new Bazooka(model);
        destructiveGun.setAmmo(5);

        Guns normalGun = new ShotGun(model);
        normalGun.setAmmo(5);

        team.getInventory().addItem(normalGun);
        team.getInventory().addItem(normalGun);

        Worm self = new Worm(team, "a", '0');
        Team enemyTeam = new Team("aa", 0, 0, model);
        Worm target = new Worm(enemyTeam, "b", '0');

        BotDecisionEngine engine = new BotDecisionEngine(5, null);

        Method method = BotDecisionEngine.class.getDeclaredMethod(
                "findDestructiveGun", Team.class, Worm.class, Worm.class, GameModel.class);
        method.setAccessible(true);

        Guns selectedGun = (Guns) method.invoke(engine, team, self, target, model);

        assertNotNull(selectedGun, "Le bot devrait sélectionner une arme destructive");
        assertTrue(selectedGun.isCanDestruct(), "L'arme sélectionnée doit pouvoir détruire le terrain");
        System.out.println("Arme sélectionnée : " + selectedGun.getName());
    }

    @Test
    void testDecideHealthPackLowHp() throws Exception {
        GameModel model = new GameModel() {
            @Override
            public Map getMap() {
                return new Map(10, 10) {
                    @Override
                    public String getMapType() {
                        return "normal";
                    }
                };
            }
        };

        Team team = new Team("TeamA", 0, 1, model);
        Worm self = new Worm(team, "Wormy", 'W');
        self.setHp(5);

        Tools healthPack = new HealthPack(team);

        BotController bot = new BotController(0, new GuiView(model));

        Field engineField = BotController.class.getDeclaredField("engine");
        engineField.setAccessible(true);

        BotDecisionEngine engine = (BotDecisionEngine) engineField.get(bot);

        BotAction action = engine.decide(model, self);

        assertNotNull(action, "L'action ne doit pas être nulle");
        assertTrue(action instanceof UseToolAction, "L'action doit être un UseToolAction");

        UseToolAction useAction = (UseToolAction) action;
        Field toolField = UseToolAction.class.getDeclaredField("tool");
        toolField.setAccessible(true);
        assertEquals(healthPack.getClass(), toolField.get(useAction).getClass(), "Le tool doit être un HealthPack");

        Field userField = UseToolAction.class.getDeclaredField("user");
        userField.setAccessible(true);
        assertEquals(self, userField.get(useAction), "Le worm utilisé doit être correct");
    }

    @Test
    void testDecideTeleportWhenInImmediateDanger() throws Exception {
        GameModel model = new GameModel() {
            @Override
            public Map getMap() {
                return new Map(10, 10) {
                    @Override
                    public String getMapType() {
                        return "normal";
                    }
                };
            }
        };

        Team team = new Team("TeamA", 0, 1, model);
        Worm self = new Worm(team, "Wormy", 'W');
        self.setHp(50);

        Tools teleport = new RandomTp(model);

        team.getInventory().addItem(teleport);

        Field engineField = BotController.class.getDeclaredField("engine");
        engineField.setAccessible(true);

        BotDecisionEngine spyEngine = new BotDecisionEngine(10, new GuiView(model));

        BotAction action = spyEngine.decide(model, self);

        assertNull(action, "L'action ne doit pas être nulle");
        assertFalse(action instanceof UseToolAction, "L'action doit être un UseToolAction");

        UseToolAction useAction = (UseToolAction) action;

        Field toolField = UseToolAction.class.getDeclaredField("tool");
        toolField.setAccessible(true);
        assertEquals(teleport, toolField.get(useAction), "Le tool doit être le teleport");

        Field userField = UseToolAction.class.getDeclaredField("user");
        userField.setAccessible(true);
        assertEquals(self, userField.get(useAction), "Le worm utilisé doit être correct");
    }

    @Test
    void testIsWeaponInRangeWithRealGuns() throws Exception {
        BotDecisionEngine engine = new BotDecisionEngine(1, null);

        Method method = BotDecisionEngine.class.getDeclaredMethod("isWeaponInRange", Guns.class, double.class);
        method.setAccessible(true);

        GameModel model = new GameModel();

        Guns shotgun = new ShotGun(model);
        Guns bazooka = new Bazooka(model);
        Guns grenade = new Grenade(model);
        Guns sniper = new Sniper(model);

        assertTrue((boolean) method.invoke(engine, shotgun, 0.5));
        assertFalse((boolean) method.invoke(engine, shotgun, 100.0));

        assertTrue((boolean) method.invoke(engine, bazooka, 5.0));
        assertFalse((boolean) method.invoke(engine, bazooka, 100.0));

        assertTrue((boolean) method.invoke(engine, grenade, 3.0));

        assertTrue((boolean) method.invoke(engine, sniper, 20.0));
    }

    @Test
    void testFindGroundLevelMinimal() throws Exception {
        Map map = new Map(10, 10);

        map.setCell(3, 6, '#');

        GameModel model = new GameModel() {
            @Override
            public Map getMap() {
                return map;
            }
        };

        BotDecisionEngine engine = new BotDecisionEngine(1, null);

        Method method = BotDecisionEngine.class.getDeclaredMethod(
                "findGroundLevel", GameModel.class, int.class, int.class);
        method.setAccessible(true);

        int groundY = (int) method.invoke(engine, model, 3, 0);

        assertEquals(5, groundY, "La méthode doit trouver le y juste avant le sol");

        int groundY2 = (int) method.invoke(engine, model, 3, 8);
        assertEquals(5, groundY2, "Même en partant plus bas, doit trouver le sol");

        int noGround = (int) method.invoke(engine, model, 1, 0);
        assertEquals(-1, noGround, "Si pas de sol, doit renvoyer -1");
    }

    @Test
    void testHasObstacleInPathMinimal() throws Exception {
        Map map = new model.Map(10, 10);

        map.setCell(5, 4, '#');

        GameModel model = new GameModel() {
            @Override
            public Map getMap() {
                return map;
            }
        };

        BotDecisionEngine engine = new BotDecisionEngine(1, null);

        Method method = BotDecisionEngine.class.getDeclaredMethod(
                "hasObstacleInPath", GameModel.class, int.class, int.class, int.class);
        method.setAccessible(true);

        boolean noObstacle = (boolean) method.invoke(engine, model, 0, 3, 4);
        assertFalse(noObstacle, "Pas d'obstacle dans ce chemin");

        boolean obstacle = (boolean) method.invoke(engine, model, 0, 4, 6);
        assertTrue(obstacle, "Doit détecter un obstacle");

        boolean obstacleReverse = (boolean) method.invoke(engine, model, 6, 4, 0);
        assertTrue(obstacleReverse, "Doit détecter un obstacle dans le chemin inverse");

        boolean noObstacle2 = (boolean) method.invoke(engine, model, 0, 0, 9);
        assertFalse(noObstacle2, "Pas d'obstacle sur cette ligne");
    }

    @Test
    void testIsTrajectoryBlockedMinimal() throws Exception {
        GameModel model = new GameModel() {
            @Override
            public model.Map getMap() {
                return new model.Map(10, 10);
            }
        };

        Team team = new Team("TeamA", 0, 1, model);
        Worm self = new Worm(team, "Self", 'S');
        Worm target = new Worm(team, "Target", 'T');

        self.setPosition(2, 2);
        target.setPosition(7, 2);

        Guns bazooka = new Bazooka(model);

        BotDecisionEngine engine = new BotDecisionEngine(1, new GuiView(model));

        Method method = BotDecisionEngine.class.getDeclaredMethod(
                "isTrajectoryBlocked", GameModel.class, Worm.class, Worm.class, Guns.class, double.class);
        method.setAccessible(true);

        boolean result = (boolean) method.invoke(engine, model, self, target, bazooka, 0.5);

        assertNotNull(result, "Le résultat ne doit pas être null");
    }

    @Test
    void testPositionSafeValidPosition() {
        when(mockMap.isGround(50, 51)).thenReturn(true);
        when(mockMap.isGround(50, 52)).thenReturn(true);
        when(mockMap.isWater(anyInt(), anyInt())).thenReturn(false);

        boolean result = invokeIsPositionSafe(50, 50);

        assertTrue(result, "Une position avec du terrain solide en dessous devrait être sûre");
    }

    @Test
    void testPositionSafeNoGroundBelow() {
        when(mockMap.isGround(anyInt(), anyInt())).thenReturn(false);
        when(mockMap.isWater(anyInt(), anyInt())).thenReturn(false);

        boolean result = invokeIsPositionSafe(50, 50);

        assertFalse(result, "Une position sans terrain en dessous ne devrait pas être sûre");
    }

    @Test
    void testPositionSafeWaterNearby() {
        when(mockMap.isGround(50, 51)).thenReturn(true);
        when(mockMap.isGround(50, 52)).thenReturn(true);
        when(mockMap.isWater(51, 50)).thenReturn(true);
        when(mockMap.isGround(51, 49)).thenReturn(false);

        boolean result = invokeIsPositionSafe(50, 50);

        assertFalse(result, "Une position avec de l'eau dangereuse à proximité ne devrait pas être sûre");
    }

    @Test
    void testPositionSafeOutOfBounds() {
        boolean result1 = invokeIsPositionSafe(1, 50);
        boolean result2 = invokeIsPositionSafe(50, -1);
        boolean result3 = invokeIsPositionSafe(99, 50);

        assertFalse(result1, "Position x=1 devrait être hors limites");
        assertFalse(result2, "Position y=-1 devrait être hors limites");
        assertFalse(result3, "Position x=99 devrait être hors limites");
    }

    @Test
    void testSimulateTrajectoryClearPath() {
        Worm mockTarget = mock(Worm.class);
        Guns mockGun = mock(Guns.class);
        Wind mockWind = mock(Wind.class);

        when(mockTarget.getX()).thenReturn(60.0);
        when(mockTarget.getY()).thenReturn(50.0);
        when(mockGun.getProjectileSpeed()).thenReturn(20.0);
        when(mockGun.getGravity()).thenReturn(0.5);
        when(mockModel.getWind()).thenReturn(mockWind);
        when(mockWind.getForce()).thenReturn(0.0);
        when(mockMap.isGround(anyInt(), anyInt())).thenReturn(false);

        boolean result = invokeSimulateTrajectory(50, 50, mockTarget, mockGun, Math.PI / 4);

        assertFalse(result, "Une trajectoire claire devrait retourner false (pas bloquée)");
    }

    @Test
    void testSimulateTrajectoryBlockedByTerrain() {
        Worm mockTarget = mock(Worm.class);
        Guns mockGun = mock(Guns.class);
        Wind mockWind = mock(Wind.class);

        when(mockTarget.getX()).thenReturn(60.0);
        when(mockTarget.getY()).thenReturn(50.0);
        when(mockGun.getProjectileSpeed()).thenReturn(20.0);
        when(mockGun.getGravity()).thenReturn(0.5);
        when(mockModel.getWind()).thenReturn(mockWind);
        when(mockWind.getForce()).thenReturn(0.0);
        when(mockMap.isGround(anyInt(), anyInt())).thenReturn(true);

        boolean result = invokeSimulateTrajectory(50, 50, mockTarget, mockGun, Math.PI / 4);

        assertFalse(result, "Une trajectoire bloquée par le terrain devrait retourner true");
    }

    @Test
    void testSimulateTrajectoryReachesTarget() {
        Worm mockTarget = mock(Worm.class);
        Guns mockGun = mock(Guns.class);
        Wind mockWind = mock(Wind.class);

        when(mockTarget.getX()).thenReturn(52.0);
        when(mockTarget.getY()).thenReturn(50.0);
        when(mockGun.getProjectileSpeed()).thenReturn(20.0);
        when(mockGun.getGravity()).thenReturn(0.1);
        when(mockModel.getWind()).thenReturn(mockWind);
        when(mockWind.getForce()).thenReturn(0.0);
        when(mockMap.isGround(anyInt(), anyInt())).thenReturn(false);

        boolean result = invokeSimulateTrajectory(50, 50, mockTarget, mockGun, 0.0);

        assertFalse(result, "Atteindre la cible devrait retourner false (pas bloquée)");
    }

    @Test
    void testComputeAngleFromPositionNoGravity() {
        Worm mockTarget = mock(Worm.class);
        Guns mockGun = mock(Guns.class);

        when(mockTarget.getX()).thenReturn(60.0);
        when(mockTarget.getY()).thenReturn(50.0);
        when(mockGun.getProjectileSpeed()).thenReturn(20.0);
        when(mockGun.getGravity()).thenReturn(0.005);

        double result = invokeComputeAngleFromPosition(50, 50, mockTarget, mockGun, true);

        assertNotEquals(0.0, result, "Un angle devrait être calculé pour un tir direct");
    }

    @Test
    void testComputeAngleFromPositionWithGravityHigh() {
        Worm mockTarget = mock(Worm.class);
        Guns mockGun = mock(Guns.class);

        when(mockTarget.getX()).thenReturn(70.0);
        when(mockTarget.getY()).thenReturn(50.0);
        when(mockGun.getProjectileSpeed()).thenReturn(30.0);
        when(mockGun.getGravity()).thenReturn(0.5);

        double result = invokeComputeAngleFromPosition(50, 50, mockTarget, mockGun, true);

        assertNotEquals(0.0, result, "Un angle devrait être calculé pour une trajectoire haute");
    }

    @Test
    void testComputeAngleFromPositionImpossibleShot() {
        Worm mockTarget = mock(Worm.class);
        Guns mockGun = mock(Guns.class);

        when(mockTarget.getX()).thenReturn(200.0);
        when(mockTarget.getY()).thenReturn(50.0);
        when(mockGun.getProjectileSpeed()).thenReturn(5.0);
        when(mockGun.getGravity()).thenReturn(2.0);

        double result = invokeComputeAngleFromPosition(50, 50, mockTarget, mockGun, true);

        assertEquals(0.0, result, "Devrait retourner 0 pour un tir impossible");
    }

    @Test
    void testWouldBeBlockedFromPositionNoGravity() {
        Worm mockTarget = mock(Worm.class);
        Guns mockGun = mock(Guns.class);

        when(mockTarget.getX()).thenReturn(60.0);
        when(mockTarget.getY()).thenReturn(50.0);
        when(mockGun.getGravity()).thenReturn(0.005); // < 0.01
        when(mockMap.isGround(anyInt(), anyInt())).thenReturn(false);

        boolean result = invokeWouldBeBlocked(50, 50, mockTarget, mockGun, Math.PI / 4);

        assertFalse(result, "Une ligne claire ne devrait pas être bloquée");
    }

    @Test
    void testWouldBeBlockedFromPositionWithGravity() {
        Worm mockTarget = mock(Worm.class);
        Guns mockGun = mock(Guns.class);
        Wind mockWind = mock(Wind.class);

        when(mockTarget.getX()).thenReturn(60.0);
        when(mockTarget.getY()).thenReturn(50.0);
        when(mockGun.getProjectileSpeed()).thenReturn(20.0);
        when(mockGun.getGravity()).thenReturn(0.5);
        when(mockModel.getWind()).thenReturn(mockWind);
        when(mockWind.getForce()).thenReturn(0.0);
        when(mockMap.isGround(anyInt(), anyInt())).thenReturn(false);

        boolean result = invokeWouldBeBlocked(50, 50, mockTarget, mockGun, Math.PI / 4);

        assertFalse(result, "Une trajectoire claire ne devrait pas être bloquée");
    }

    @Test
    void testFindClearShotPositionFoundInDirection() {
        Worm mockSelf = mock(Worm.class);
        Worm mockTarget = mock(Worm.class);
        Guns mockGun = mock(Guns.class);
        Wind mockWind = mock(Wind.class);

        when(mockSelf.getX()).thenReturn(50.0);
        when(mockSelf.getY()).thenReturn(50.0);
        when(mockTarget.getX()).thenReturn(60.0);
        when(mockTarget.getY()).thenReturn(50.0);
        when(mockGun.getGravity()).thenReturn(0.5);
        when(mockGun.getProjectileSpeed()).thenReturn(20.0);
        when(mockModel.getWind()).thenReturn(mockWind);
        when(mockWind.getForce()).thenReturn(0.0);

        when(mockMap.isGround(55, 51)).thenReturn(true);
        when(mockMap.isGround(55, 52)).thenReturn(true);
        when(mockMap.isEmpty(55, 50)).thenReturn(true);
        when(mockMap.isWater(anyInt(), anyInt())).thenReturn(false);
        when(mockMap.isGround(anyInt(), anyInt())).thenReturn(false);
        when(mockMap.isGround(55, 51)).thenReturn(true);

        BotAction result = invokeFindClearShotPosition(mockSelf, mockTarget, mockGun, "default");

        assertNotNull(result, "Devrait trouver une position avec un tir clair");
        assertTrue(result instanceof MoveAction, "Devrait retourner une MoveAction");
    }

    @Test
    void testFindClearShotPositionNotFound() {
        Worm mockSelf = mock(Worm.class);
        Worm mockTarget = mock(Worm.class);
        Guns mockGun = mock(Guns.class);

        when(mockSelf.getX()).thenReturn(50.0);
        when(mockSelf.getY()).thenReturn(50.0);
        when(mockTarget.getX()).thenReturn(60.0);
        when(mockTarget.getY()).thenReturn(50.0);
        when(mockGun.getGravity()).thenReturn(0.5);

        when(mockMap.getWidth()).thenReturn(55);

        BotAction result = invokeFindClearShotPosition(mockSelf, mockTarget, mockGun, "default");

        assertNull(result, "Ne devrait pas trouver de position si tout est bloqué");
    }

    @Test
    void testFindClearShotPositionOutOfBounds() {
        Worm mockSelf = mock(Worm.class);
        Worm mockTarget = mock(Worm.class);
        Guns mockGun = mock(Guns.class);

        when(mockSelf.getX()).thenReturn(95.0);
        when(mockSelf.getY()).thenReturn(50.0);
        when(mockTarget.getX()).thenReturn(105.0);
        when(mockTarget.getY()).thenReturn(50.0);
        when(mockGun.getGravity()).thenReturn(0.5);

        BotAction result = invokeFindClearShotPosition(mockSelf, mockTarget, mockGun, "default");

        assertNull(result, "Ne devrait pas trouver de position hors limites");
    }

    @Test
    void testChooseBestGunShotgunInRange() {
        Worm mockSelf = mock(Worm.class);
        Worm mockTarget = mock(Worm.class);
        Team mockTeam = mock(Team.class);
        Inventory mockInventory = mock(Inventory.class);
        Wind mockWind = mock(Wind.class);

        Guns shotgun = mock(Guns.class);
        Guns bazooka = mock(Guns.class);

        when(mockSelf.getX()).thenReturn(50.0);
        when(mockSelf.getY()).thenReturn(50.0);
        when(mockTarget.getX()).thenReturn(55.0);
        when(mockTarget.getY()).thenReturn(50.0);

        when(mockTeam.getInventory()).thenReturn(mockInventory);
        ArrayList<Guns> guns = new ArrayList<>();
        guns.add(shotgun);
        guns.add(bazooka);
        when(mockInventory.getAvailableGuns()).thenReturn(guns);

        when(shotgun.getName()).thenReturn("Shotgun");
        when(shotgun.getAmmo()).thenReturn(5);
        when(shotgun.getProjectileSpeed()).thenReturn(20.0);
        when(shotgun.getGravity()).thenReturn(0.5);

        when(bazooka.getName()).thenReturn("Bazooka");
        when(bazooka.getAmmo()).thenReturn(3);

        when(mockModel.getWind()).thenReturn(mockWind);
        when(mockWind.getForce()).thenReturn(0.0);

        Guns result = invokeChooseBestGun(mockSelf, mockTarget, mockTeam, 5.0);

        assertNotNull(result, "Devrait trouver une arme");
        assertEquals("Shotgun", result.getName(), "Devrait choisir le shotgun à courte distance");
    }

    @Test
    void testChooseBestGunNonDestructivePreferred() {
        Worm mockSelf = mock(Worm.class);
        Worm mockTarget = mock(Worm.class);
        Team mockTeam = mock(Team.class);
        Inventory mockInventory = mock(Inventory.class);
        Wind mockWind = mock(Wind.class);

        Guns sniper = mock(Guns.class);
        Guns grenade = mock(Guns.class);

        when(mockSelf.getX()).thenReturn(50.0);
        when(mockSelf.getY()).thenReturn(50.0);
        when(mockTarget.getX()).thenReturn(100.0);
        when(mockTarget.getY()).thenReturn(50.0);

        when(mockTeam.getInventory()).thenReturn(mockInventory);
        ArrayList<Guns> guns = new ArrayList<>();
        guns.add(sniper);
        guns.add(grenade);
        when(mockInventory.getAvailableGuns()).thenReturn(guns);

        when(sniper.getName()).thenReturn("Sniper");
        when(sniper.getAmmo()).thenReturn(3);
        when(sniper.isCanDestruct()).thenReturn(false);
        when(sniper.getProjectileSpeed()).thenReturn(30.0);
        when(sniper.getGravity()).thenReturn(0.1);

        when(grenade.getName()).thenReturn("Grenade");
        when(grenade.getAmmo()).thenReturn(5);
        when(grenade.isCanDestruct()).thenReturn(true);
        when(grenade.getProjectileSpeed()).thenReturn(25.0);
        when(grenade.getGravity()).thenReturn(0.5);

        when(mockModel.getWind()).thenReturn(mockWind);
        when(mockWind.getForce()).thenReturn(0.0);

        Guns result = invokeChooseBestGun(mockSelf, mockTarget, mockTeam, 50.0);

        assertNotNull(result, "Devrait trouver une arme");
        assertEquals("Sniper", result.getName(), "Devrait préférer l'arme non-destructive");
    }

    @Test
    void testChooseBestGunNoWeaponAvailable() {
        Worm mockSelf = mock(Worm.class);
        Worm mockTarget = mock(Worm.class);
        Team mockTeam = mock(Team.class);
        Inventory mockInventory = mock(Inventory.class);

        when(mockSelf.getX()).thenReturn(50.0);
        when(mockSelf.getY()).thenReturn(50.0);
        when(mockTarget.getX()).thenReturn(100.0);
        when(mockTarget.getY()).thenReturn(50.0);

        when(mockTeam.getInventory()).thenReturn(mockInventory);
        when(mockInventory.getAvailableGuns()).thenReturn(new ArrayList<>());

        Guns result = invokeChooseBestGun(mockSelf, mockTarget, mockTeam, 50.0);

        assertNull(result, "Ne devrait pas trouver d'arme si l'inventaire est vide");
    }

    @Test
    void testChooseBestGunAllOutOfRange() {
        Worm mockSelf = mock(Worm.class);
        Worm mockTarget = mock(Worm.class);
        Team mockTeam = mock(Team.class);
        Inventory mockInventory = mock(Inventory.class);

        Guns shotgun = mock(Guns.class);

        when(mockSelf.getX()).thenReturn(50.0);
        when(mockSelf.getY()).thenReturn(50.0);
        when(mockTarget.getX()).thenReturn(500.0);
        when(mockTarget.getY()).thenReturn(50.0);

        when(mockTeam.getInventory()).thenReturn(mockInventory);
        ArrayList<Guns> guns = new ArrayList<>();
        guns.add(shotgun);
        when(mockInventory.getAvailableGuns()).thenReturn(guns);

        when(shotgun.getName()).thenReturn("Shotgun");
        when(shotgun.getAmmo()).thenReturn(5);

        Guns result = invokeChooseBestGun(mockSelf, mockTarget, mockTeam, 450.0);

        assertNull(result, "Ne devrait pas trouver d'arme si toutes sont hors de portée");
    }

    private BotAction invokeFindClearShotPosition(Worm self, Worm target, Guns gun, String mapType) {
        try {
            Method method = BotDecisionEngine.class.getDeclaredMethod(
                    "findClearShotPosition", GameModel.class, Worm.class, Worm.class,
                    Guns.class, String.class);
            method.setAccessible(true);
            return (BotAction) method.invoke(engine, mockModel, self, target, gun, mapType);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'invocation de findClearShotPosition", e);
        }
    }

    private boolean invokeIsPositionSafe(int x, int y) {
        try {
            Method method = BotDecisionEngine.class.getDeclaredMethod(
                    "isPositionSafe", GameModel.class, int.class, int.class);
            method.setAccessible(true);
            return (boolean) method.invoke(engine, mockModel, x, y);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'invocation de isPositionSafe", e);
        }
    }

    private boolean invokeSimulateTrajectory(int fromX, int fromY, Worm target, Guns gun, double angle) {
        try {
            Method method = BotDecisionEngine.class.getDeclaredMethod(
                    "simulateTrajectoryFromPosition", GameModel.class, int.class, int.class,
                    Worm.class, Guns.class, double.class);
            method.setAccessible(true);
            return (boolean) method.invoke(engine, mockModel, fromX, fromY, target, gun, angle);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'invocation de simulateTrajectoryFromPosition", e);
        }
    }

    private double invokeComputeAngleFromPosition(int fromX, int fromY, Worm target, Guns gun, boolean preferHigh) {
        try {
            Method method = BotDecisionEngine.class.getDeclaredMethod(
                    "computeAngleFromPosition", int.class, int.class, Worm.class,
                    Guns.class, GameModel.class, boolean.class);
            method.setAccessible(true);
            return (double) method.invoke(engine, fromX, fromY, target, gun, mockModel, preferHigh);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'invocation de computeAngleFromPosition", e);
        }
    }

    private boolean invokeWouldBeBlocked(int fromX, int fromY, Worm target, Guns gun, double angle) {
        try {
            Method method = BotDecisionEngine.class.getDeclaredMethod(
                    "wouldBeBlockedFromPosition", GameModel.class, int.class, int.class,
                    Worm.class, Guns.class, double.class);
            method.setAccessible(true);
            return (boolean) method.invoke(engine, mockModel, fromX, fromY, target, gun, angle);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'invocation de wouldBeBlockedFromPosition", e);
        }
    }

    private Guns invokeChooseBestGun(Worm self, Worm target, Team team, double distance) {
        try {
            Method method = BotDecisionEngine.class.getDeclaredMethod(
                    "chooseBestGunForDistance", Worm.class, Worm.class, Team.class,
                    GameModel.class, double.class);
            method.setAccessible(true);
            return (Guns) method.invoke(engine, self, target, team, mockModel, distance);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'invocation de chooseBestGunForDistance", e);
        }
    }

    private Tools invokeFindHealthPack() throws Exception {
        Method method = BotDecisionEngine.class.getDeclaredMethod("findHealthPack", Team.class);
        method.setAccessible(true);
        return (Tools) method.invoke(engine, team);
    }
}