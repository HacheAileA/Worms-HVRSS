package controller.bot;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import model.GameModel;
import model.Map;
import model.items.guns.Guns;
import model.physics.Projectile;
import model.players.Worm;
import view.gui.GuiView;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShootActionTest {

    private GameModel mockModel;
    private Worm mockWorm;
    private GuiView mockView;
    private Guns mockGun;
    private Map mockMap;

    @BeforeEach
    void setUp() {
        mockModel = mock(GameModel.class);
        mockWorm = mock(Worm.class);
        mockView = mock(GuiView.class);
        mockGun = mock(Guns.class);
        mockMap = mock(Map.class);

        when(mockModel.getCurrentWorm()).thenReturn(mockWorm);
        when(mockModel.getMap()).thenReturn(mockMap);
        when(mockModel.getProjectiles()).thenReturn(new ArrayList<>());

        when(mockWorm.getAimAngle()).thenReturn(30.0);
    }

    @Test
    void testExecuteSetsGunAndRotatesAim() {
        double targetAngle = 45.0;
        ShootAction action = new ShootAction(mockGun, targetAngle, mockView);

        action.execute(mockModel);

        verify(mockWorm).setSelectedItem(mockGun);

        verify(mockWorm).rotateAim(targetAngle - 30.0);
    }

    @Test
    void testExecuteStartsProjectileTimer() {
        ShootAction action = new ShootAction(mockGun, 0, mockView);

        action.execute(mockModel);

        verify(mockView).startProjectileTimer();
    }

    @Test
    void testExecuteAddsProjectileToModelIfNotNull() {
        ShootAction action = new ShootAction(mockGun, 0, mockView);
        Projectile mockProjectile = mock(Projectile.class);

        when(mockWorm.shoot(mockMap, 0)).thenReturn(mockProjectile);

        action.execute(mockModel);

        verify(mockWorm).shoot(mockMap, 0);

        assertTrue(mockModel.getProjectiles().contains(mockProjectile));
    }

    @Test
    void testExecuteDoesNotAddNullProjectile() {
        ShootAction action = new ShootAction(mockGun, 0, mockView);

        when(mockWorm.shoot(mockMap, 0)).thenReturn(null);

        action.execute(mockModel);

        verify(mockWorm).shoot(mockMap, 0);

        assertTrue(mockModel.getProjectiles().isEmpty());
    }
}
