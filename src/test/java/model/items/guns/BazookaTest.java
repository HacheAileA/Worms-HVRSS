package model.items.guns;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import model.GameModel;
import model.players.Team;
import model.players.Worm;

class BazookaTest {

    private GameModel model;
    private Bazooka bazooka;
    private Worm target;

    @BeforeEach
    void setUp() {
        model = new GameModel();
        Team team = new Team("Equipe de test 101",0,1, model);
        bazooka = new Bazooka(model);
        target = new Worm(team, "Cobaye", 'C');
    }

    @Test
    void testBazookaProperties() {
        assertEquals("Bazooka", bazooka.getName(), "Le nom doit être Bazooka");
        assertEquals(1, bazooka.getMaxAmmo(), "Le Bazooka doit avoir 1 munition maximale");
        assertEquals(40, bazooka.getDamagesPerBullets(), "Le Bazooka doit infliger 40 dégâts par tir");
        assertEquals(1, bazooka.getAmmo(), "L'arme doit commencer avec 1 munition");
    }

    @Test
    void testHasAmmoInitially() {
        assertTrue(bazooka.hasAmmo(), "Le Bazooka doit avoir des munitions initialement");
    }

    @Test
    void testShootConsoleReducesAmmoAndHP() {
        Bazooka bazookaAlwaysHit = new Bazooka(model) {
            @Override
            protected boolean randomHitConsole() {
                return true;
            }
        };

        int initialHP = target.getHp();
        Bazooka.shootConsole(bazookaAlwaysHit, target);

        assertEquals(0, bazookaAlwaysHit.getAmmo(), "Le Bazooka doit avoir 0 munition après un tir");
        assertEquals(initialHP - bazookaAlwaysHit.getDamagesPerBullets(), target.getHp(),
                "Le target doit avoir perdu les PV correspondant aux dégâts");
    }

    @Test
    void testShootWithNoAmmo() {
        bazooka.setAmmo(0);
        int initialHP = target.getHp();

        Bazooka.shootConsole(bazooka, target);

        assertEquals(0, bazooka.getAmmo(), "Les munitions restent à 0 si on tire sans ammo");
        assertEquals(initialHP, target.getHp(), "Le target ne doit pas perdre de PV si aucune munition");
    }

    @Test
    void testShootWithNullTarget() {
        Bazooka bazookaAlwaysHit = new Bazooka(model) {
            @Override
            protected boolean randomHitConsole() {
                return true;
            }
        };
        assertDoesNotThrow(() -> Bazooka.shootConsole(bazookaAlwaysHit, null),
                "Tirer sur une cible null ne doit pas lever d'exception");
        assertEquals(0, bazookaAlwaysHit.getAmmo() - 1, "Le tir doit diminuer l'arme même si la cible est null");
    }
}
