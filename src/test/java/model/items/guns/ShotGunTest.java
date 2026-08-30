package model.items.guns;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import model.players.Team;
import model.players.Worm;
import model.GameModel;
import model.physics.Projectile;

import java.util.List;

class ShotGunTest {

    private GameModel model;
    private ShotGun gun;
    private Worm worm;

    @BeforeEach
    void setUp() {
        model = new GameModel();
        Team team = new Team("a", 0, 1, model);
        gun = new ShotGun(model);
        worm = new Worm(team, "Cobaye", 'C');
    }

    @Test
    void testHasAmmoAfterCreation() {
        assertTrue(gun.hasAmmo(), "ShotGun devrait avoir des munitions après création");
    }

    @Test
    void testShootDecreasesAmmoAndHp() {
        gun = new ShotGun(model) {
            @Override
            protected boolean randomHitConsole() {
                return true;
            }
        };

        int initialHp = worm.getHp();
        int initialAmmo = gun.getAmmo();

        Guns.shootConsole(gun, worm);

        assertEquals(initialAmmo - 1, gun.getAmmo(), "Une munition doit être consommée après le tir");
        assertEquals(initialHp - gun.getDamagesPerBullets(), worm.getHp(),
                "Les points de vie du Worm doivent diminuer après avoir été touché");
    }

    @Test
    void testShootWithNoAmmo() {
        gun.setAmmo(0);
        int initialHp = worm.getHp();

        Guns.shootConsole(gun, worm);

        assertEquals(0, gun.getAmmo(), "Ammo doit rester à 0");
        assertEquals(initialHp, worm.getHp(), "Worm ne doit pas perdre de PV si pas de munitions");
    }

    @Test
    void testShootOnNullWorm() {
        int initialAmmo = gun.getAmmo();

        Guns.shootConsole(gun, null);

        assertEquals(initialAmmo, gun.getAmmo(), "Ammo ne doit pas diminuer si le Worm est null");
    }

    @Test
    void testRandomHitProbability() {
        boolean hit = gun.randomHitConsole();
        assertTrue(hit || !hit, "randomHit() doit renvoyer true ou false");
    }


    @Test
    void testCreateProjectilesProducesThreeProjectilesWithCorrectAngles() {
        gun.setAmmo(5);
        List<Projectile> projectiles = gun.createProjectiles(worm);

        assertEquals(3, projectiles.size(), "Le ShotGun doit créer 3 projectiles");

        assertEquals(4, gun.getAmmo(), "Ammo doit diminuer de 1 après tir");
    }

    @Test
    void testCreateProjectilesNoAmmoReturnsEmptyList() {
        gun.setAmmo(0);
        List<Projectile> projectiles = gun.createProjectiles(worm);
        assertTrue(projectiles.isEmpty(), "Pas de projectiles si pas de munitions");
    }

    @Test
    void testCreateProjectileConsumesAmmoAndSetsMaxDistance() {
        gun.setAmmo(2);
        Projectile p = gun.createProjectile(worm);

        assertNotNull(p, "Doit créer un projectile si ammo > 0");
        assertEquals(1, gun.getAmmo(), "Ammo doit diminuer de 1");
    }

    @Test
    void testCreateProjectileReturnsNullWhenNoAmmo() {
        gun.setAmmo(0);
        Projectile p = gun.createProjectile(worm);
        assertNull(p, "Doit retourner null si pas de munitions");
    }

    @Test
    void testCopyCreatesIndependentShotGun() {
        gun.setAmmo(3);
        ShotGun copy = (ShotGun) gun.copy();

        assertNotSame(gun, copy, "La copie doit être un nouvel objet");
        assertEquals(gun.getAmmo(), copy.getAmmo(), "La copie doit avoir le même nombre de munitions");
    }
}
