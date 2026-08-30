package model.items.tools;

import model.players.Team;
import model.players.Worm;
import model.GameModel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HealthPackTest {
    
    private GameModel model;
    private Worm cobaye;
    private HealthPack healthPack;

    @BeforeEach
    void setUp() {
        model = new GameModel();
        healthPack = new HealthPack(20, model);
        Team team = new Team("a", 0, 1, model);
        cobaye = new Worm(team, "bot",'A');
    }

    @Test
    void testConstructor() {
        assertNotNull(healthPack, "Le HealthPack ne devrait pas être null après la création");
    }

    @Test
    void testGetHealAmount() {
        healthPack = new HealthPack(-9, model);
        assertTrue(healthPack.getHealAmount() > 0, "Le montant du heal devrait être supérieur à 0");
    }

    @Test
    void testUseTool() {

        healthPack.useTool(cobaye);
        assertTrue(cobaye.getHp() == 100, "Le soin prodigué ne devrait pas permettre de dépasser les hp max du worm");
        
        healthPack = new HealthPack(20, model);
        cobaye.setHp(50);
        int hpBefore = cobaye.getHp();
        int useLeft = healthPack.getAmmo();
        healthPack.useTool(cobaye);
        assertEquals(healthPack.getHealAmount() + hpBefore , cobaye.getHp(), "Le worm devrait avoir été heal correctement");
        
        assertEquals(useLeft - 1, healthPack.getAmmo(), "Les utilisations restantes devraient avoir diminués de 1");

        assertDoesNotThrow(() -> healthPack.useTool(null));
        healthPack.setAmmo(1);
        useLeft = 1;
        assertEquals(useLeft, healthPack.getAmmo());

        healthPack = new HealthPack(20, model);
        Team team = new Team("a", 0, 1, model);
        cobaye = new Worm(team, "bot", 'a');
        cobaye.setHp(50);
        hpBefore = cobaye.getHp();
        healthPack.setAmmo(0);
        healthPack.useTool(cobaye);
        assertEquals(hpBefore, cobaye.getHp());
        
    }
}
