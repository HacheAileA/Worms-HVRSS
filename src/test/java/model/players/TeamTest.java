package model.players;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.GameModel;
import model.Map;
import model.items.guns.Guns;

import java.util.ArrayList;

public class TeamTest {
    private GameModel model = mock(GameModel.class);
    private Team team;

    @BeforeEach
    void setUp() {
        team = new Team("Winners", 1, 3, model);
        for (Guns g : team.getInventory().getAvailableGuns()) {
            g.setTeam(team);
        }
    }

    @Test
    void testConstructor() {
        assertEquals("Winners", team.getName());
        assertNotNull(team.getWorms());
        assertEquals(3, team.getWorms().size());
        assertNotNull(team.getInventory());
    }

    @Test
    void testGetName() {
        assertEquals("Winners", team.getName());
    }

    @Test
    void testSetName() {
        team.setName("Losers");
        assertEquals("Losers", team.getName());
    }

    @Test
    void testGetWorms() {
        ArrayList<Worm> worms = team.getWorms();
        assertNotNull(worms);
        assertEquals(3, worms.size());
    }

    @Test
    void testGetArsenal() {
        assertNotNull(team.getInventory());
        assertNotNull(team.getInventory().getGuns());
    }

    @Test
    void testContainsWormAliveTrue() {
        assertTrue(team.containsWormAlive(), "Au moins un worm doit être vivant");
    }

    @Test
    void testContainsWormAliveFalse() {
        for (Worm w : team.getWorms()) {
            w.setHp(0);
        }
        assertFalse(team.containsWormAlive(), "Aucun worm n'est vivant");
    }

    @Test
    void testGetColoredName() {
        String colored = team.getColoredName();
        assertNotNull(colored);
        assertTrue(colored.contains(team.getName()));
    }

    @Test
    void testGetSymbol() {
        assertEquals('B', team.getSymbol());
    }

    @Test
    void testGetColor() {
        assertEquals("BLUE", team.getColor(), "Le premier worms doit bleu");
        Team t2 = new Team("BlueTeam", 2, 1, model);
        assertEquals("GREEN", t2.getColor(), "Le second worms doit être vert");
        Team t3 = new Team("GreenTeam", 3, 1, model);
        assertEquals("YELLOW", t3.getColor(), "Le troisième worm doit être jaune"); 
        Team t4 = new Team("YellowTeam", 4, 1, model);
        assertEquals("WHITE", t4.getColor(), "Le quatrième worm doit être blanc");
        Team t5 = new Team("DefaultTeam", 99, 1, model);
        assertEquals("WHITE", t5.getColor(), "les autres doivent être blanc");
    }

    @Test
    void testGetTeamId() {
        assertEquals(1, team.getTeamId());
    }

    @Test
    void testPlaceWormsOnMap() {
        Map map = mock(Map.class);
        for (Worm w : team.getWorms()) {
            Worm spyWorm = spy(w);
            team.getWorms().set(team.getWorms().indexOf(w), spyWorm);
        }

        team.placeWormsOnMap(map);
    }
}
