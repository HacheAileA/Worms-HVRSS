package controller.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

import model.GameModel;
import model.Map;
import model.physics.Projectile;
import model.physics.Wind;
import model.players.Worm;
import model.items.Item;
import model.items.guns.Bazooka;
import model.items.guns.Grenade;
import model.items.guns.Guns;
import model.items.guns.ShotGun;
import model.items.guns.Sniper;
import view.gui.GamePanel;
import view.gui.GuiView;
import view.gui.InventoryPanel;
import view.gui.Particle;
import view.gui.SoundPlayer;

public class ShootControllerTest {

    @Test
    void testSetCanShootupdatesField() throws Exception {
        GameModel model = mock(GameModel.class);
        GuiView view = mock(GuiView.class);

        ShootController controller = new ShootController(model, view);

        controller.setCanShoot(false);

        Field canShootField = ShootController.class.getDeclaredField("canShoot");
        canShootField.setAccessible(true);

        boolean value = canShootField.getBoolean(controller);

        assertFalse(value);
    }

    @Test
    void testBindMouseToaddsMouseListenerAndRepaintsOnLeftClick() {
        GameModel model = mock(GameModel.class);
        GuiView view = mock(GuiView.class);

        ShootController controller = new ShootController(model, view);

        JPanel panel = spy(new JPanel());

        controller.bindMouseTo(panel);

        MouseListener[] listeners = panel.getMouseListeners();
        assertEquals(1, listeners.length);

        MouseEvent event = new MouseEvent(
                panel,
                MouseEvent.MOUSE_PRESSED,
                System.currentTimeMillis(),
                0,
                50,
                50,
                1,
                false,
                MouseEvent.BUTTON1);

        listeners[0].mousePressed(event);

        verify(panel).repaint();
    }

    @Test
    void testCreateShootParticlesadds20ParticlesAndStartsTimer() throws Exception {
        GameModel model = mock(GameModel.class);
        GuiView view = mock(GuiView.class);
        Worm worm = mock(Worm.class);

        when(model.getCurrentWorm()).thenReturn(worm);
        when(worm.getX()).thenReturn(5.0);
        when(worm.getY()).thenReturn(10.0);

        ShootController controller = new ShootController(model, view);

        Method method = ShootController.class.getDeclaredMethod("createShootParticles");
        method.setAccessible(true);

        method.invoke(controller);

        verify(view, times(20)).addParticles(any(Particle.class));
        verify(view).startParticleTimer();
    }

    @Test
    void testShootnonGunaddsProjectileAndStartsTimers() throws Exception {
        GameModel model = mock(GameModel.class);
        GuiView view = mock(GuiView.class);
        GamePanel gamePanel = mock(GamePanel.class);
        Worm worm = mock(Worm.class);
        Map map = mock(Map.class);
        Projectile projectile = mock(Projectile.class);
        Wind wind = mock(Wind.class);

        when(model.getCurrentWorm()).thenReturn(worm);
        when(model.getMap()).thenReturn(map);
        when(model.getWind()).thenReturn(wind);
        when(model.getProjectiles()).thenReturn(new ArrayList<>());
        when(view.getGamePanel()).thenReturn(gamePanel);

        Item item = mock(Item.class);
        when(worm.getSelectedItem()).thenReturn(item);

        when(worm.shoot(any(), anyDouble())).thenReturn(projectile);

        ShootController controller = new ShootController(model, view);

        Method shootMethod = ShootController.class.getDeclaredMethod("shoot", double.class);
        shootMethod.setAccessible(true);

        shootMethod.invoke(controller, 100.0);

        verify(projectile).setWind(wind);
        verify(gamePanel).stopMovementTimer();
        verify(view).startProjectileTimer();
        verify(view, atLeastOnce()).addParticles(any());
    }

    @Test
    void testShootwhenProjectilesNotEmptydoesNothing() throws Exception {
        GameModel model = mock(GameModel.class);
        GuiView view = mock(GuiView.class);
        Worm worm = mock(Worm.class);

        ArrayList<Projectile> existing = new ArrayList<>();
        existing.add(mock(Projectile.class));

        when(model.getCurrentWorm()).thenReturn(worm);
        when(model.getProjectiles()).thenReturn(existing);

        ShootController controller = new ShootController(model, view);

        Method shootMethod = ShootController.class.getDeclaredMethod("shoot", double.class);
        shootMethod.setAccessible(true);

        shootMethod.invoke(controller, 100.0);

        verifyNoInteractions(view);
        verify(worm, never()).shoot(any(), anyDouble());
    }

    @Test
    void testShootwithShotGuncreatesProjectilesAndPlaysSound() throws Exception {
        GameModel model = mock(GameModel.class);
        GuiView view = mock(GuiView.class);
        GamePanel gamePanel = mock(GamePanel.class);
        InventoryPanel inventoryPanel = mock(InventoryPanel.class);
        SoundPlayer soundPlayer = mock(SoundPlayer.class);
        Worm worm = mock(Worm.class);
        ShotGun shotGun = mock(ShotGun.class);
        Wind wind = mock(Wind.class);

        Projectile p1 = mock(Projectile.class);
        Projectile p2 = mock(Projectile.class);

        when(shotGun.hasAmmo()).thenReturn(true);
        when(shotGun.createProjectiles(worm)).thenReturn(List.of(p1, p2));

        when(model.getCurrentWorm()).thenReturn(worm);
        when(model.getProjectiles()).thenReturn(new ArrayList<>());
        when(model.getWind()).thenReturn(wind);

        when(worm.getSelectedItem()).thenReturn(shotGun);

        when(view.getGamePanel()).thenReturn(gamePanel);
        when(view.getInventoryPanel()).thenReturn(inventoryPanel);
        view.soundPlayer = soundPlayer;

        ShootController controller = new ShootController(model, view);

        Method shootMethod = ShootController.class.getDeclaredMethod("shoot", double.class);
        shootMethod.setAccessible(true);

        shootMethod.invoke(controller, 100.0);

        verify(p1).setWind(wind);
        verify(p2).setWind(wind);

        verify(soundPlayer).playSoundEffect("/sounds/sounds_effects/shotgun.wav");
        verify(gamePanel).stopMovementTimer();
        verify(view).startProjectileTimer();
        verify(inventoryPanel).refresh();
    }

    @Test
    void testShootgunWithoutAmmodoesNothing() throws Exception {
        GameModel model = mock(GameModel.class);
        GuiView view = mock(GuiView.class);
        Worm worm = mock(Worm.class);
        ShotGun gun = mock(ShotGun.class);

        when(gun.hasAmmo()).thenReturn(false);
        when(worm.getSelectedItem()).thenReturn(gun);

        when(model.getCurrentWorm()).thenReturn(worm);
        when(model.getProjectiles()).thenReturn(new ArrayList<>());

        ShootController controller = new ShootController(model, view);

        Method shootMethod = ShootController.class.getDeclaredMethod("shoot", double.class);
        shootMethod.setAccessible(true);

        shootMethod.invoke(controller, 100.0);

        verifyNoInteractions(view);
    }

    @Test
    void testShootgunCreatesNoProjectilesdoesNothing() throws Exception {
        GameModel model = mock(GameModel.class);
        GuiView view = mock(GuiView.class);
        Worm worm = mock(Worm.class);
        ShotGun gun = mock(ShotGun.class);

        when(gun.hasAmmo()).thenReturn(true);
        when(gun.createProjectiles(worm)).thenReturn(List.of());
        when(worm.getSelectedItem()).thenReturn(gun);

        when(model.getCurrentWorm()).thenReturn(worm);
        when(model.getProjectiles()).thenReturn(new ArrayList<>());

        ShootController controller = new ShootController(model, view);

        Method shootMethod = ShootController.class.getDeclaredMethod("shoot", double.class);
        shootMethod.setAccessible(true);

        shootMethod.invoke(controller, 100.0);

        verifyNoInteractions(view);
    }

    @Test
    void testShootwithShotGunplaysShotgunSound() throws Exception {
        GameModel model = mock(GameModel.class);
        GuiView view = mock(GuiView.class);
        Worm worm = mock(Worm.class);
        ShotGun gun = mock(ShotGun.class);
        SoundPlayer soundPlayer = mock(SoundPlayer.class);

        Projectile p = mock(Projectile.class);

        prepareCommonGunSetup(model, view, worm, gun, List.of(p));

        view.soundPlayer = soundPlayer;

        ShootController controller = new ShootController(model, view);

        Method shootMethod = ShootController.class.getDeclaredMethod("shoot", double.class);
        shootMethod.setAccessible(true);

        shootMethod.invoke(controller, 100.0);

        verify(soundPlayer).playSoundEffect("/sounds/sounds_effects/shotgun.wav");
    }

    @Test
    void testShootwithSniperplaysSniperSound() throws Exception {
        GameModel model = mock(GameModel.class);
        GuiView view = mock(GuiView.class);
        Worm worm = mock(Worm.class);
        Sniper gun = mock(Sniper.class);
        SoundPlayer soundPlayer = mock(SoundPlayer.class);

        Projectile p = mock(Projectile.class);

        prepareCommonGunSetup(model, view, worm, gun, List.of(p));

        view.soundPlayer = soundPlayer;

        ShootController controller = new ShootController(model, view);

        Method shootMethod = ShootController.class.getDeclaredMethod("shoot", double.class);
        shootMethod.setAccessible(true);

        shootMethod.invoke(controller, 100.0);

        verify(soundPlayer).playSoundEffect("/sounds/sounds_effects/sniper.wav");
    }

    @Test
    void testShootwithBazookaplaysBazookaSound() throws Exception {
        GameModel model = mock(GameModel.class);
        GuiView view = mock(GuiView.class);
        Worm worm = mock(Worm.class);
        Bazooka gun = mock(Bazooka.class);
        SoundPlayer soundPlayer = mock(SoundPlayer.class);

        Projectile p = mock(Projectile.class);

        prepareCommonGunSetup(model, view, worm, gun, List.of(p));

        view.soundPlayer = soundPlayer;

        ShootController controller = new ShootController(model, view);

        Method shootMethod = ShootController.class.getDeclaredMethod("shoot", double.class);
        shootMethod.setAccessible(true);

        shootMethod.invoke(controller, 100.0);

        verify(soundPlayer).playSoundEffect("/sounds/sounds_effects/bazooka.wav");
    }

    @Test
    void testShootwithGrenadeplaysGrenadeSound() throws Exception {
        GameModel model = mock(GameModel.class);
        GuiView view = mock(GuiView.class);
        Worm worm = mock(Worm.class);
        Grenade gun = mock(Grenade.class);
        SoundPlayer soundPlayer = mock(SoundPlayer.class);

        Projectile p = mock(Projectile.class);

        prepareCommonGunSetup(model, view, worm, gun, List.of(p));

        view.soundPlayer = soundPlayer;

        ShootController controller = new ShootController(model, view);

        Method shootMethod = ShootController.class.getDeclaredMethod("shoot", double.class);
        shootMethod.setAccessible(true);

        shootMethod.invoke(controller, 100.0);

        verify(soundPlayer).playSoundEffect("/sounds/sounds_effects/grenade.wav");
    }

    private void prepareCommonGunSetup(
            GameModel model,
            GuiView view,
            Worm worm,
            Guns gun,
            List<Projectile> createdProjectiles) {
        when(model.getCurrentWorm()).thenReturn(worm);
        when(model.getProjectiles()).thenReturn(new ArrayList<>());
        when(model.getWind()).thenReturn(mock(Wind.class));

        when(worm.getSelectedItem()).thenReturn(gun);

        when(gun.hasAmmo()).thenReturn(true);
        when(gun.createProjectiles(worm)).thenReturn(createdProjectiles);

        when(view.getGamePanel()).thenReturn(mock(GamePanel.class));
        when(view.getInventoryPanel()).thenReturn(null);
    }

}
