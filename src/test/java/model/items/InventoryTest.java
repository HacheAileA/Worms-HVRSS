package model.items;

import model.items.guns.Bazooka;
import model.items.guns.Guns;
import model.items.guns.ShotGun;
import model.items.tools.HealthPack;
import model.items.tools.Tools;
import model.GameModel;
import model.players.Team;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    private GameModel model;
    private Inventory inventory;
    private Team teamA;
    private Team teamB;

    @BeforeEach
    void setUp() {
        model = new GameModel();
        Team team = new Team("team1", 0, 1, model);
        model.setCurrentTeam(team);
        inventory = new Inventory(model);
        inventory.getAvailableGuns().clear();
        inventory.getAvailableTools().clear();

        teamA = new Team("TeamA", 1, 2, model);
        teamB = new Team("TeamB", 2, 3, model);
    }

    @Test
    void testInventoryInitialization() {
        assertNotNull(inventory.getGuns(), "L'arsenal d'armes ne doit pas être null");
        assertNotNull(inventory.getTools(), "L'arsenal d'outils ne doit pas être null");
        assertTrue(inventory.getAvailableGuns().isEmpty(), "Liste des armes doit être vide");
        assertTrue(inventory.getAvailableTools().isEmpty(), "Liste des outils doit être vide");
    }

    @Test
    void testAddItemGun() {
        Guns gun = new Bazooka(model);
        inventory.addItem(gun);
        assertEquals(1, inventory.getAvailableGuns().size());
        assertSame(gun, inventory.getAvailableGuns().get(0));
        assertSame(model.getCurrentTeam(), gun.getTeam(), "L'équipe du gun doit être l'équipe courante");
    }

    @Test
    void testAddItemTool() {
        Tools tool = new HealthPack(50, model);
        inventory.addItem(tool);
        assertEquals(1, inventory.getAvailableTools().size());
        assertEquals(tool, inventory.getAvailableTools().get(0));
        assertSame(model.getCurrentTeam(), tool.getTeam(), "L'équipe du tool doit être l'équipe courante");
    }

    @Test
    void testAddItemPolymorphic() {
        Guns gun = new Bazooka(model);
        Tools tool = new HealthPack(50, model);

        inventory.addItem(gun);
        inventory.addItem(tool);

        assertEquals(1, inventory.getAvailableGuns().size());
        assertEquals(1, inventory.getAvailableTools().size());
    }

    @Test
    void testRemoveGunNoAmmo() {
        Guns gun = new Bazooka(model);
        gun.setAmmo(0);
        inventory.addItem(gun);
        inventory.removeGunNoAmmo();
        assertTrue(inventory.getAvailableGuns().isEmpty());
    }

    @Test
    void testRemoveToolNoAmmo() {
        Tools tool = new HealthPack(50, model);
        tool.setAmmo(0);
        inventory.addItem(tool);
        inventory.removeToolNoAmmo();
        assertTrue(inventory.getAvailableTools().isEmpty());
    }

    @Test
    void testReloadAllItems() {
        Guns gun = new Bazooka(model);
        Tools tool = new HealthPack(50, model);
        gun.setAmmo(0);
        tool.setAmmo(0);

        inventory.addItem(gun);
        inventory.addItem(tool);

        inventory.reloadAllItems();

        assertEquals(gun.getMaxAmmo(), gun.getAmmo());
        assertEquals(tool.getMaxAmmo(), tool.getAmmo());
    }

    @Test
    void testGetGunByClass() {
        Guns gun = new Bazooka(model);
        inventory.addItem(gun);

        Guns found = inventory.getGun(Bazooka.class);
        assertNotNull(found);
        assertSame(gun, found);

        assertNull(inventory.getGun(ShotGun.class), "Doit renvoyer null si l'arme n'existe pas");
    }

    @Test
    void testGetToolByClass() {
        Tools tool = new HealthPack(50, model);
        inventory.addItem(tool);

        Tools found = inventory.getTool(HealthPack.class);
        assertNotNull(found);
        assertSame(tool, found);

        assertNotNull(inventory.getTool(HealthPack.class), "Doit renvoyer null si le type exact n'existe pas (donc ne doit pas renvoyer null ici)");
    }

    @Test
    void testGetAvailableGunsFilteredByTeam() {
        Guns gunA = new Bazooka(model);
        inventory.addItem(gunA);

        Guns gunB = new Bazooka(model);
        gunB.setTeam(teamB);
        inventory.addItem(gunB);

        ArrayList<Guns> gunsA = inventory.getAvailableGuns(teamA);

        assertEquals(0, gunsA.size());
    }

    @Test
    void testGetAvailableItems() {
        Guns gun = new Bazooka(model);
        Tools tool = new HealthPack(50, model);

        inventory.addItem(gun);
        inventory.addItem(tool);

        ArrayList<Item> items = inventory.getAvailableItems(teamA);
        assertEquals(1, items.size());
        assertFalse(items.contains(gun));
        assertTrue(items.contains(tool));
    }

    @Test
    void testLoadItemsFromSave() {
        ArrayList<Item> savedItems = new ArrayList<>();

        Guns savedGun = new Bazooka(model);
        Tools savedTool = new HealthPack(50, model);

        savedItems.add(savedGun);
        savedItems.add(savedTool);

        inventory.loadItemsFromSave(savedItems, teamB);

        ArrayList<Guns> guns = inventory.getAvailableGuns();
        ArrayList<Tools> tools = inventory.getAvailableTools();

        assertEquals(1, guns.size());
        assertNotSame(savedGun, guns.get(0), "Les guns doivent être des copies");
        assertSame(teamB, guns.get(0).getTeam());

        assertEquals(1, tools.size());
        assertSame(teamB, tools.get(0).getTeam());
    }
}
