package model.items.guns;

import model.GameModel;
import model.players.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GrenadeTest {

    private GameModel mockModel;
    private Team mockTeam;
    private Grenade grenade;

    @BeforeEach
    void setUp() {
        mockModel = mock(GameModel.class);
        mockTeam = mock(Team.class);

        when(mockModel.getCurrentTeam()).thenReturn(mockTeam);

        grenade = new Grenade(mockModel);
    }

    @Test
    void testCopyCreatesNewInstanceWithSameAmmo() {
        grenade.setAmmo(5);
        Guns copy = grenade.copy();

        assertNotSame(grenade, copy);
        assertEquals(grenade.getName(), copy.getName());
        assertEquals(grenade.getAmmo(), copy.getAmmo());
        assertEquals(grenade.getTeam(), copy.getTeam());
    }
}
