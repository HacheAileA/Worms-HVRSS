package model.items.guns;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import model.players.Team;
import model.players.Worm;
import model.GameModel;

class SniperTest {

    private GameModel model;
    private Guns gun;
    private Worm worm;

    @BeforeEach
    void setUp() {
        model = new GameModel();
        Team team = new Team("a", 0, 1, model);
        gun = new Sniper(model);
        worm = new Worm(team, "Cobaye", 'C');
    }

    @Test
    void testHasAmmoAfterCreation() {
        assertTrue(gun.hasAmmo(), "Sniper devrait avoir des munitions après création");
    }

    @Test
    void testShootDecreasesAmmoAndHp() {
        gun = new Sniper(model) {
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
    void testCopyCreatesNewInstanceWithSameAmmo() {
        gun.setAmmo(3);

        Guns copy = gun.copy();

        assertNotSame(gun, copy, "La copie doit être une nouvelle instance");

        assertEquals(gun.getName(), copy.getName(), "Le nom doit être identique");
        assertEquals(gun.getAmmo(), copy.getAmmo(), "Les munitions doivent être identiques");
        assertEquals(gun.getDamagesPerBullets(), copy.getDamagesPerBullets(),
                "Les dégâts par balle doivent être identiques");
        assertEquals(gun.getTeam(), copy.getTeam(), "La team doit être identique");
    }

}
