package model.items;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import model.GameModel;
import model.items.guns.*;

import java.util.List;

class ArsenalTest {
    private GameModel model;
    private Arsenal arsenal;


    @BeforeEach
    void setUp() {
        model = new GameModel();
        arsenal = new Arsenal(model);
    }

    @Test
    void testConstructorAddsGuns() {
        List<Guns> guns = arsenal.getAvailableGuns();
        assertNotNull(guns, "La liste ne doit pas être nulle");
        assertEquals(3, guns.size(), "La liste doit contenir exactement 3 armes");

        assertTrue(guns.get(0) instanceof Bazooka, "Le premier doit être un Bazooka");
        assertTrue(guns.get(1) instanceof ShotGun, "Le deuxième doit être un ShotGun");
        assertTrue(guns.get(2) instanceof Sniper, "Le troisième doit être un Sniper");
    }

    @Test
    void testGetAvailableGunsReturnsSameList() {
        List<Guns> guns1 = arsenal.getAvailableGuns();
        List<Guns> guns2 = arsenal.getAvailableGuns();
        assertSame(guns1, guns2, "getAvailableGuns doit retourner la même instance de liste");
    }

    @Test
    void testAddingGunToListPersists() {
        List<Guns> guns = arsenal.getAvailableGuns();
        Bazooka newBazooka = new Bazooka(model);
        guns.add(newBazooka);
        assertEquals(4, guns.size(), "Après ajout, la liste doit contenir 4 armes");
        assertSame(newBazooka, guns.get(3), "Le dernier élément doit être l'objet ajouté");
    }

    @Test
    void testAvailableGunsListIsMutable() {
        List<Guns> guns = arsenal.getAvailableGuns();
        int initialSize = guns.size();
        guns.add(new ShotGun(model));
        assertEquals(initialSize + 1, guns.size(), "Après ajout, la liste doit contenir une arme supplémentaire");
    }

    @Test
    void testGetAvailableGunsNeverReturnsNull() {
        List<Guns> guns = arsenal.getAvailableGuns();
        assertNotNull(guns, "La méthode getAvailableGuns ne doit jamais renvoyer null");
    }

    @Test
    void testGetAvailableGunsContainsOnlyGuns() {
        List<Guns> guns = arsenal.getAvailableGuns();
        for (Guns gun : guns) {
            assertTrue(gun instanceof Guns, "Tous les éléments de la liste doivent être des instances de Guns");
        }
    }

    @Test
    void testRemoveGunNoAmmo() {
        List<Guns> guns = arsenal.getAvailableGuns();
        guns.get(0).setAmmo(0);
        arsenal.removeGunNoAmmo();
        assertEquals(2, guns.size(), "Après suppression, la liste doit contenir 2 armes");
        for (Guns gun : guns) {
            assertTrue(gun.getAmmo() > 0, "Toutes les armes restantes doivent avoir des munitions");
        }
    }

    @Test
    void testAddGun() {
        Arsenal testArsenal = new Arsenal(model) {
            {
                getAvailableGuns().clear();
            }
        };
        Bazooka bazooka1 = new Bazooka(model);
        Bazooka bazooka2 = new Bazooka(model);

        testArsenal.addGun(bazooka1);
        assertEquals(1, testArsenal.getAvailableGuns().size(), "Après ajout, la liste doit contenir 1 arme");
        assertSame(bazooka1, testArsenal.getAvailableGuns().get(0), "L'arme ajoutée doit être présente dans la liste");

        bazooka1.setAmmo(0);
        testArsenal.addGun(bazooka1);
        assertEquals(1, testArsenal.getAvailableGuns().size(),
                "Après rechargement, la liste doit toujours contenir 1 arme");
        assertEquals(1, testArsenal.getAvailableGuns().get(0).getAmmo(), "L'arme doit être rechargée");

        bazooka2.setAmmo(0);
        testArsenal.addGun(bazooka2);
        Guns storedGun = testArsenal.getAvailableGuns().get(0);
        assertEquals(storedGun.getMaxAmmo(), storedGun.getAmmo(), "L'arme doit être rechargée");
    }

    @Test
    void testAddGunFalseBranchEvaluated() {
        Arsenal testArsenal = new Arsenal(model);
        int initialSize = testArsenal.getAvailableGuns().size();

        Guns notAvailableGun = new Guns("A", 5, 5, model.getCurrentTeam()) {

            @Override
            public Guns copy() {
                return null;
            }
        };

        testArsenal.addGun(notAvailableGun);

        assertEquals(initialSize + 1, testArsenal.getAvailableGuns().size());
    }

    @Test
    void testAddGunWithNull() {
        int initialSize = arsenal.getAvailableGuns().size();
        arsenal.addGun(null);
        assertEquals(initialSize, arsenal.getAvailableGuns().size(),
                "Ajouter une arme null ne doit pas modifier la liste");
    }

    @Test
    void testClear() {
        arsenal.clear();
        assertTrue(arsenal.getAvailableGuns().isEmpty(), "Après clear, la liste des armes doit être vide");
    }

    @Test
    void reloadAllGuns() {

        for (Guns gun : arsenal.getAvailableGuns()) {
            gun.setAmmo(0);
            assertEquals(0, gun.getAmmo(), "L'arme doit avoir 0 munitions avant rechargement");
        }

        arsenal.reloadAllGuns();

        for (Guns gun : arsenal.getAvailableGuns()) {
            assertEquals(gun.getMaxAmmo(), gun.getAmmo(), "L'arme doit être rechargée à son maximum de munitions");
        }
    }
}
