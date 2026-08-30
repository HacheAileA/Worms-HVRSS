package model.items.guns;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import model.players.Team;
import model.players.Worm;
import model.GameModel;
import model.items.Inventory;
import model.physics.Projectile;

class GunsTest {
    private Guns gun;
    private Worm worm;
    private Inventory inventory;
    private GameModel model;

    @BeforeEach
    void setUp() {
        model = new GameModel();
        Team team = new Team("a", 0, 1, model);
        worm = new Worm(team, "Cobaye", 'C');
        gun = new Bazooka(model);
        inventory = new Inventory(model);

    }

    @Test
    void testHasAmmo() {
        assertTrue(gun.hasAmmo(), "L'arme devrait avoir des munitions après avoir été crée ");
        Guns.shootConsole(gun, worm);
        assertFalse(gun.hasAmmo(), "L'arme ne devrait pas avoir de munitions après avoir tiré");
    }

    @Test
    void testShootConsoleLooseAmmo() {
        for (int i = 0; i < inventory.getAvailableGuns().size(); i++) {
            gun = inventory.getAvailableGuns().get(i);
            Guns.shootConsole(gun, worm);
            assertEquals(gun.getAmmo(), inventory.getAvailableGuns().get(i).getMaxAmmo() - 1,
                    "L'arme devrait avoir une munition en moins après avoir tiré");
        }
    }

    @Test
    void testShootConsoleLooseHp() {
        gun = new Bazooka(model) {
            @Override
            protected boolean randomHitConsole() {
                return true;
            }
        };

        int initialHp = worm.getHp();
        Guns.shootConsole(gun, worm);
        assertTrue(worm.getHp() < initialHp, "Les points de vie du ver devraient diminuer après avoir été touché");
        assertEquals(initialHp - gun.getDamagesPerBullets(), worm.getHp(),
                "Les points de vie du ver devraient être: " + (initialHp - gun.getDamagesPerBullets()));
    }

    @Test
    void testSetDamagesPerBullets() {
        int newDamage = 50;
        gun.setDamagesPerBullets(newDamage);
        assertEquals(newDamage, gun.getDamagesPerBullets(),
                "Les dégâts par balle devraient être mis à jour correctement");
    }

    @Test
    void testGetProjectileSpeed() {
        assertEquals(20, gun.getProjectileSpeed(), "La vitesse du projectile doit correspondre à la valeur initiale");
    }

    @Test
    void testGetGravity() {
        assertEquals(0.0, gun.getGravity(), 0.001, "La gravité doit correspondre à la valeur initiale");
    }

    @Test
    void testIsCanDestruct() {
        assertTrue(gun.isCanDestruct(), "Le projectile doit pouvoir détruire si canDestruct est vrai");
    }

    @Test
    void testCreateProjectiles() {
        List<Projectile> projectiles = gun.createProjectiles(new Worm(new Team("a", 0, 0, model), "a", '1'));
        assertNotNull(projectiles);
        assertEquals(1, projectiles.size());
    }
}
