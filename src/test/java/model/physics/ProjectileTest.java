package model.physics;

import model.GameModel;
import model.Map;
import model.players.Team;
import model.players.Worm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

class ProjectileTest {

    private Projectile projectile;
    private Worm shooter;

    @BeforeEach
    void setUp() {
        GameModel model = new GameModel();

        Team team = new Team("TestTeam", 0, 1, model);

        shooter = new Worm(team, "TestWorm", 'T');

        projectile = new Projectile(
                1.0, 2.0, Math.PI / 4, 10.0, 0.1,
                20, 1, shooter, 5.0);
    }

    @Test
    void testGetters() {
        assertEquals(1.0, projectile.getX());
        assertEquals(2.0, projectile.getY());
        assertEquals(20, projectile.getDamage());
        assertEquals(1.0, projectile.getExplosionRadius(), 1e-6);
        assertEquals(shooter, projectile.getShooter());
    }

    @Test
    void testSetWind() throws Exception {
        Worm shooter = new Worm("Shooter", 100, 'S', 1);

        Projectile projectile = new Projectile(
                0, 0, 0, 10, 9.8, 50, 2, shooter);

        Wind wind = new Wind();
        wind.enabled = true;

        projectile.setWind(wind);

        var windField = Projectile.class.getDeclaredField("wind");
        windField.setAccessible(true);
        Wind actualWind = (Wind) windField.get(projectile);

        assertSame(wind, actualWind, "Projectile should store the same Wind object that was set");
    }

    @Test
    void testGetDistanceTraveled() {
        Worm shooter = new Worm("Shooter", 100, 'S', 1);

        Projectile projectile = new Projectile(
                0, 0, 0, 10, 9.8, 50, 2, shooter);
        assertEquals(0, projectile.getDistanceTraveled(), 1e-6);

        try {
            var xField = Projectile.class.getDeclaredField("x");
            xField.setAccessible(true);
            xField.set(projectile, 3.0);

            var yField = Projectile.class.getDeclaredField("y");
            yField.setAccessible(true);
            yField.set(projectile, 4.0);

        } catch (Exception e) {
            fail("Reflection error: " + e.getMessage());
        }

        assertEquals(5.0, projectile.getDistanceTraveled(), 1e-6);
    }

    @Test
    void testHasLeftShooterTile() throws Exception {
        Worm shooter = new Worm("Shooter", 100, 'S', 1);

        Projectile projectile = new Projectile(
                0, 0, 0, 10, 9.8, 50, 2, shooter);

        assertFalse(projectile.hasLeftShooterTile());

        Field field = Projectile.class.getDeclaredField("hasLeftShooterTile");
        field.setAccessible(true);
        field.set(projectile, true);

        assertTrue(projectile.hasLeftShooterTile());
    }

    @Test
    void testShouldBeDestroyed() {
        Worm shooter = new Worm("Shooter", 100, 'S', 1);

        Projectile projectile = new Projectile(5, 5, 0, 0, 0, 50, 1, shooter);

        Map map = Mockito.mock(Map.class);

        Mockito.when(map.getWidth()).thenReturn(10);
        Mockito.when(map.getHeight()).thenReturn(10);
        setProjectilePosition(projectile, 15, 5);
        assertTrue(projectile.shouldBeDestroyed(map));

        setProjectilePosition(projectile, 5, -1);
        assertTrue(projectile.shouldBeDestroyed(map));

        setProjectilePosition(projectile, 3, 3);
        Mockito.when(map.isGround(3, 3)).thenReturn(true);
        Mockito.when(map.isWater(3, 3)).thenReturn(false);
        assertTrue(projectile.shouldBeDestroyed(map));

        setProjectilePosition(projectile, 4, 4);
        Mockito.when(map.isGround(4, 4)).thenReturn(false);
        Mockito.when(map.isWater(4, 4)).thenReturn(true);
        assertTrue(projectile.shouldBeDestroyed(map));

        setProjectilePosition(projectile, 2, 2);
        Mockito.when(map.isGround(2, 2)).thenReturn(false);
        Mockito.when(map.isWater(2, 2)).thenReturn(false);
        assertFalse(projectile.shouldBeDestroyed(map));
    }

    private void setProjectilePosition(Projectile projectile, double x, double y) {
        try {
            var fieldX = Projectile.class.getDeclaredField("x");
            fieldX.setAccessible(true);
            fieldX.set(projectile, x);

            var fieldY = Projectile.class.getDeclaredField("y");
            fieldY.setAccessible(true);
            fieldY.set(projectile, y);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testCheckDistanceLimit() throws Exception {
        Worm shooter = new Worm("Shooter", 100, 'S', 1);

        Projectile p1 = new Projectile(0, 0, 0, 10, 0, 50, 1, shooter);
        invokeCheckDistanceLimit(p1);
        assertTrue(p1.isActive(), "Projectile without distance limit should remain active");

        Projectile p2 = new Projectile(0, 0, 0, 10, 0, 50, 1, shooter, 100); // maxDistance=100
        invokeCheckDistanceLimit(p2);
        assertTrue(p2.isActive(), "Projectile should still be active if distance not reached");

        Projectile p3 = new Projectile(0, 0, 0, 10, 0, 50, 1, shooter, 5); // maxDistance=5
        setProjectilePosition(p3, 10, 0);
        invokeCheckDistanceLimit(p3);
        assertFalse(p3.isActive(), "Projectile should be inactive when distance limit exceeded");
    }

    private void invokeCheckDistanceLimit(Projectile projectile) throws Exception {
        Method method = Projectile.class.getDeclaredMethod("checkDistanceLimit");
        method.setAccessible(true);
        method.invoke(projectile);
    }

    @Test
    void testUpdateShooterTileStatus() throws Exception {
        Worm shooter = new Worm("Shooter", 100, 'S', 1);

        Projectile p1 = new Projectile(5, 5, 0, 10, 0, 50, 1, shooter);
        setHasLeftShooterTile(p1, true);

        invokeUpdateShooterTileStatus(p1, 0, 0);
        assertTrue(p1.hasLeftShooterTile(), "If already true, it should remain true");

        Projectile p2 = new Projectile(2, 2, 0, 10, 0, 50, 1, shooter);
        setHasLeftShooterTile(p2, false);

        invokeUpdateShooterTileStatus(p2, 5, 2);
        assertTrue(p2.hasLeftShooterTile(), "Should become true if currentTile+1 < shooterTileX");

        Projectile p3 = new Projectile(2, 2, 0, 10, 0, 50, 1, shooter);
        setHasLeftShooterTile(p3, false);

        invokeUpdateShooterTileStatus(p3, 2, 5);
        assertTrue(p3.hasLeftShooterTile(), "Should become true if currentTileY+1 < shooterTileY");
    }

    private void invokeUpdateShooterTileStatus(Projectile projectile, double shooterTileX, double shooterTileY)
            throws Exception {
        Method method = Projectile.class.getDeclaredMethod("updateShooterTileStatus", double.class, double.class);
        method.setAccessible(true);
        method.invoke(projectile, shooterTileX, shooterTileY);
    }

    private void setHasLeftShooterTile(Projectile projectile, boolean value) throws Exception {
        Field field = Projectile.class.getDeclaredField("hasLeftShooterTile");
        field.setAccessible(true);
        field.set(projectile, value);
    }

    @Test
    void testApplyPhysicswindEnabled() throws Exception {
        Worm shooter = new Worm("Shooter", 100, 'S', 1);

        Projectile p = new Projectile(0, 0, 0, 10, 5, 50, 1, shooter);

        Wind wind = new Wind();
        wind.enabled = true;
        p.setWind(wind);

        double initialVx = getPrivateField(p, "vx");

        Method applyPhysics = Projectile.class.getDeclaredMethod("applyPhysics", double.class);
        applyPhysics.setAccessible(true);
        applyPhysics.invoke(p, 1.0);

        double newVx = getPrivateField(p, "vx");

        assertEquals(initialVx + wind.getForce() * 1.0, newVx, 1e-6, "vx should be increased by wind force * time");
    }

    private double getPrivateField(Projectile p, String fieldName) throws Exception {
        Field field = Projectile.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.getDouble(p);
    }

    @Test
    void testUpdatewhenInactive() {
        Worm shooter = new Worm("Shooter", 100, 'S', 1);
        Projectile p = new Projectile(0, 0, 0, 10, 5, 50, 1, shooter);

        setPrivateField(p, "active", false);

        double xBefore = p.getX();
        double yBefore = p.getY();

        p.update(1.0, 0, 0);

        assertEquals(xBefore, p.getX(), 1e-6);
        assertEquals(yBefore, p.getY(), 1e-6);
    }

    @Test
    void testUpdatehasLeftShooterTilecallsCheckDistanceLimit() {
        Worm shooter = new Worm("Shooter", 100, 'S', 1);
        Projectile p = new Projectile(0, 0, 0, 10, 5, 50, 1, shooter);

        setPrivateField(p, "hasLeftShooterTile", true);

        setPrivateField(p, "maxDistance", 0.1);
        setPrivateField(p, "hasDistanceLimit", true);

        assertTrue(p.isActive(), "Projectile should start active");

        p.update(1.0, 0, 0);

        assertFalse(p.isActive(), "Projectile should be deactivated due to distance limit");
    }

    @Test
    void projectileUpdatePerformanceTest() {
        Worm shooter = mock(Worm.class);
        Projectile projectile = new Projectile(
                0, 0,
                Math.PI / 4,
                50,
                9.81,
                10,
                5,
                shooter);

        projectile.setWind(null);

        long start = System.nanoTime();

        for (int i = 0; i < 10_000; i++) {
            projectile.update(0.016, 0, 0);
        }

        long end = System.nanoTime();
        long durationMs = (end - start) / 1_000_000;

        assertTrue(durationMs < 100,
                "update() trop lent : " + durationMs + " ms");
    }

    @Test
    void distanceCalculationPerformanceTest() {
        Worm shooter = mock(Worm.class);
        Projectile projectile = new Projectile(
                0, 0,
                Math.PI / 3,
                100,
                9.81,
                20,
                10,
                shooter,
                1000);

        long start = System.nanoTime();

        for (int i = 0; i < 100_000; i++) {
            projectile.update(0.01, 0, 0);
            projectile.getDistanceTraveled();
        }

        long end = System.nanoTime();
        long durationMs = (end - start) / 1_000_000;

        assertTrue(durationMs < 250,
                "Calcul de distance trop lent : " + durationMs + " ms");
    }

    class FakeMap extends Map {
        public FakeMap() {
            super (10,50);
        }
    }

    @Test
    void destructionCheckPerformanceTest() {
        Worm shooter = mock(Worm.class);
        Map map = new FakeMap();

        Projectile projectile = new Projectile(
                10, 10,
                Math.PI / 6,
                30,
                9.81,
                10,
                5,
                shooter);

        long start = System.nanoTime();

        for (int i = 0; i < 50_000; i++) {
            projectile.update(0.016, 0, 0);
            projectile.shouldBeDestroyed(map);
        }

        long end = System.nanoTime();
        long durationMs = (end - start) / 1_000_000;

        assertTrue(durationMs < 500,
                "shouldBeDestroyed() trop lent : " + durationMs + " ms");
    }

    private void setPrivateField(Object obj, String fieldName, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
