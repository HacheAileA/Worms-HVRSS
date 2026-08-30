package controller.gui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.GameModel;
import model.Map;
import model.items.Inventory;
import model.players.Team;
import model.players.Worm;
import view.gui.GamePanel;
import view.gui.GuiView;
import view.gui.Particle;
import view.gui.SoundPlayer;

class InputControllerTest {

        private GameModel model;
        private GamePanel panel;
        private GuiView view;
        private SoundPlayer soundPlayer;
        private InputController controller;
        private Worm worm;
        private Map map;
        private Team team;
        private Inventory inventory;
        private Component dummyComponent;

        @BeforeEach
        void setup() {
                model = mock(GameModel.class);
                panel = mock(GamePanel.class);
                view = mock(GuiView.class);
                soundPlayer = mock(SoundPlayer.class);

                view.soundPlayer = soundPlayer;

                worm = mock(Worm.class);
                map = mock(Map.class);
                team = mock(Team.class);
                inventory = mock(Inventory.class);

                when(model.getCurrentWorm()).thenReturn(worm);
                when(model.getMap()).thenReturn(map);
                when(worm.getTeam()).thenReturn(team);
                when(team.getInventory()).thenReturn(inventory);
                when(inventory.getAvailableItems(team)).thenReturn(new ArrayList<>());

                controller = new InputController(model, panel, view);
                dummyComponent = new JPanel();
        }

        @Test
        void testKeyPressedSpace() {
                testKeyPressedSpaceInAir();
                testKeyPressedSpaceOnGround();
        }

        @Test
        void testKeyPressedLeft() {
                KeyEvent e = new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0,
                                KeyEvent.VK_LEFT,
                                ' ');
                controller.keyPressed(e);

                verify(worm).startMoveLeft();
                verify(soundPlayer).playRunSound();
        }

        @Test
        void testKeyPressedRight() {
                KeyEvent e = new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0,
                                KeyEvent.VK_RIGHT, ' ');
                controller.keyPressed(e);

                verify(worm).startMoveRight();
                verify(soundPlayer).playRunSound();
        }

        @Test
        void testKeyPressedSpaceOnGround() {
                when(worm.isOnGround(map)).thenReturn(true);
                when(worm.getX()).thenReturn(10.0);
                when(worm.getY()).thenReturn(10.0);

                KeyEvent e = new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0,
                                KeyEvent.VK_SPACE, ' ');
                controller.keyPressed(e);

                verify(worm).jumpSmooth(map);
                verify(soundPlayer).playSoundEffect(contains("jump.wav"));
                verify(view, atLeastOnce()).addParticles(any(Particle.class));
                verify(view).startParticleTimer();
        }

        @Test
        void testKeyPressedSpaceInAir() {
                when(worm.isOnGround(map)).thenReturn(false);

                KeyEvent e = new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0,
                                KeyEvent.VK_SPACE, ' ');
                controller.keyPressed(e);

                verify(worm, never()).jumpSmooth(any());
                verify(soundPlayer, never()).playSoundEffect(contains("jump.wav"));
        }

        @Test
        void testKeyReleasedLeftSimple() {
                KeyEvent press = new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0,
                                KeyEvent.VK_LEFT, ' ');
                controller.keyPressed(press);

                KeyEvent release = new KeyEvent(dummyComponent, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0,
                                KeyEvent.VK_LEFT, ' ');
                controller.keyReleased(release);

                verify(worm).stopMove();
                verify(soundPlayer).stopRunSound();
        }

        @Test
        void testKeyReleasedRightSimple() {
                KeyEvent press = new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0,
                                KeyEvent.VK_RIGHT, ' ');
                controller.keyPressed(press);

                KeyEvent release = new KeyEvent(dummyComponent, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0,
                                KeyEvent.VK_RIGHT, ' ');
                controller.keyReleased(release);

                verify(worm).stopMove();
                verify(soundPlayer).stopRunSound();
        }

        @Test
        void testKeyReleasedLeftWhileRightPressed() {
                KeyEvent pressRight = new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0,
                                KeyEvent.VK_RIGHT, ' ');
                controller.keyPressed(pressRight);

                KeyEvent pressLeft = new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0,
                                KeyEvent.VK_LEFT, ' ');
                controller.keyPressed(pressLeft);

                KeyEvent releaseLeft = new KeyEvent(dummyComponent, KeyEvent.KEY_RELEASED, System.currentTimeMillis(),
                                0,
                                KeyEvent.VK_LEFT, ' ');
                controller.keyReleased(releaseLeft);

                verify(worm, times(2)).startMoveRight();
                verify(worm, never()).stopMove();
        }

        @Test
        void testKeyReleasedRightWhileLeftPressed() {
                KeyEvent pressLeft = new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0,
                                KeyEvent.VK_LEFT, ' ');
                controller.keyPressed(pressLeft);

                KeyEvent pressRight = new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0,
                                KeyEvent.VK_RIGHT, ' ');
                controller.keyPressed(pressRight);

                KeyEvent releaseRight = new KeyEvent(dummyComponent, KeyEvent.KEY_RELEASED, System.currentTimeMillis(),
                                0,
                                KeyEvent.VK_RIGHT, ' ');
                controller.keyReleased(releaseRight);

                verify(worm, times(2)).startMoveLeft();
                verify(worm, never()).stopMove();
        }

        @Test
        void testMouseMoved() {
                MouseEvent e = new MouseEvent(dummyComponent, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0,
                                100, 200,
                                0, false);
                controller.mouseMoved(e);

                assertEquals(100, controller.getMouseX());
                assertEquals(200, controller.getMouseY());
        }

        @Test
        void testMouseDragged() {
                MouseEvent e = new MouseEvent(dummyComponent, MouseEvent.MOUSE_DRAGGED, System.currentTimeMillis(), 0,
                                150, 250,
                                0, false);
                controller.mouseDragged(e);

                assertEquals(150, controller.getMouseX());
                assertEquals(250, controller.getMouseY());
        }

        @Test
        void testMouseWheelZoomIn() {
                MouseWheelEvent e = new MouseWheelEvent(dummyComponent, MouseWheelEvent.MOUSE_WHEEL,
                                System.currentTimeMillis(),
                                0, 100, 100, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, -1);
                controller.mouseWheelMoved(e);

                verify(panel).zoomIn(any(Point.class));
                verify(panel, never()).zoomOut(any(Point.class));
        }

        @Test
        void testMouseWheelZoomOut() {
                MouseWheelEvent e = new MouseWheelEvent(dummyComponent, MouseWheelEvent.MOUSE_WHEEL,
                                System.currentTimeMillis(),
                                0, 100, 100, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, 1);
                controller.mouseWheelMoved(e);

                verify(panel).zoomOut(any(Point.class));
                verify(panel, never()).zoomIn(any(Point.class));
        }

        @Test
        void testMouseExited() {
                MouseEvent e = new MouseEvent(dummyComponent, MouseEvent.MOUSE_EXITED, System.currentTimeMillis(), 0, 0,
                                0, 0, false);
                assertDoesNotThrow(() -> controller.mouseExited(e));
        }

        @Test
        void testMouseEntered() {
                MouseEvent e = new MouseEvent(dummyComponent, MouseEvent.MOUSE_ENTERED, System.currentTimeMillis(), 0,
                                0, 0, 0, false);
                assertDoesNotThrow(() -> controller.mouseEntered(e));
        }

        @Test
        void testMouseClicked() {
                MouseEvent e = new MouseEvent(dummyComponent, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0,
                                0, 0, 1, false);
                assertDoesNotThrow(() -> controller.mouseClicked(e));
        }

        @Test
        void testMouseReleased() {
                MouseEvent e = new MouseEvent(dummyComponent, MouseEvent.MOUSE_RELEASED,
                                System.currentTimeMillis(), 0, 123, 456, 1, false);

                controller.mouseReleased(e);

                assertEquals(123, controller.getMouseX());
                assertEquals(456, controller.getMouseY());
        }

        @Test
        void testMousePressed() {
                MouseEvent e = new MouseEvent(dummyComponent, MouseEvent.MOUSE_PRESSED,
                                System.currentTimeMillis(), 0, 321, 654, 1, false);

                controller.mousePressed(e);

                assertEquals(321, controller.getMouseX());
                assertEquals(654, controller.getMouseY());
        }

        @Test
        void testKeyTyped() {
                KeyEvent e = new KeyEvent(dummyComponent, KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0,
                                KeyEvent.VK_UNDEFINED, 'a');
                assertDoesNotThrow(() -> controller.keyTyped(e));
        }

        @Test
        void testKeyPressedToggleDevMode() {
                KeyEvent e = new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED,
                                System.currentTimeMillis(), 0, KeyEvent.VK_D, 'D');

                controller.keyPressed(e);

                verify(model).toggleDevMode();
        }

        @Test
        void testKeyPressedEnter() {
                KeyEvent e = new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED,
                                System.currentTimeMillis(), 0, KeyEvent.VK_ENTER, '\n');

                controller.keyPressed(e);

                verify(model).nextTurn();
        }

        @Test
        void testKeyPressed1() {
                model.items.Item firstItem = mock(model.items.Item.class);
                ArrayList<model.items.Item> items = new ArrayList<>();
                items.add(firstItem);
                when(inventory.getAvailableItems(team)).thenReturn(items);

                view.gui.InventoryPanel inventoryPanel = mock(view.gui.InventoryPanel.class);
                when(view.getInventoryPanel()).thenReturn(inventoryPanel);

                KeyEvent e = new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED,
                                System.currentTimeMillis(), 0, KeyEvent.VK_1, '1');

                controller.keyPressed(e);

                verify(worm).setSelectedItem(firstItem);

                verify(inventoryPanel).refresh();
        }

        @Test
        void testKeyPressed2() {
                model.items.Item firstItem = mock(model.items.Item.class);
                model.items.Item secondItem = mock(model.items.Item.class);
                ArrayList<model.items.Item> items = new ArrayList<>();
                items.add(firstItem);
                items.add(secondItem);

                when(inventory.getAvailableItems(team)).thenReturn(items);

                view.gui.InventoryPanel inventoryPanel = mock(view.gui.InventoryPanel.class);
                when(view.getInventoryPanel()).thenReturn(inventoryPanel);

                KeyEvent e = new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED,
                                System.currentTimeMillis(), 0, KeyEvent.VK_2, '2');

                controller.keyPressed(e);
                verify(worm).setSelectedItem(secondItem);

                verify(inventoryPanel).refresh();
        }

        @Test
        void testKeyPressed3() {
                model.items.Item item1 = mock(model.items.Item.class);
                model.items.Item item2 = mock(model.items.Item.class);
                model.items.Item item3 = mock(model.items.Item.class);
                ArrayList<model.items.Item> items = new ArrayList<>();
                items.add(item1);
                items.add(item2);
                items.add(item3);
                when(inventory.getAvailableItems(team)).thenReturn(items);

                view.gui.InventoryPanel inventoryPanel = mock(view.gui.InventoryPanel.class);
                when(view.getInventoryPanel()).thenReturn(inventoryPanel);

                KeyEvent e = new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED,
                                System.currentTimeMillis(), 0, KeyEvent.VK_3, '3');
                controller.keyPressed(e);

                verify(worm).setSelectedItem(item3);
                verify(inventoryPanel).refresh();
        }

        @Test
        void testKeyPressed4() {
                ArrayList<model.items.Item> items = new ArrayList<>();
                for (int i = 1; i <= 4; i++) {
                        items.add(mock(model.items.Item.class));
                }
                when(inventory.getAvailableItems(team)).thenReturn(items);

                view.gui.InventoryPanel inventoryPanel = mock(view.gui.InventoryPanel.class);
                when(view.getInventoryPanel()).thenReturn(inventoryPanel);

                KeyEvent e = new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED,
                                System.currentTimeMillis(), 0, KeyEvent.VK_4, '4');
                controller.keyPressed(e);

                verify(worm).setSelectedItem(items.get(3));
                verify(inventoryPanel).refresh();
        }

        @Test
        void testKeyPressed5() {
                ArrayList<model.items.Item> items = new ArrayList<>();
                for (int i = 1; i <= 5; i++) {
                        items.add(mock(model.items.Item.class));
                }
                when(inventory.getAvailableItems(team)).thenReturn(items);

                view.gui.InventoryPanel inventoryPanel = mock(view.gui.InventoryPanel.class);
                when(view.getInventoryPanel()).thenReturn(inventoryPanel);

                KeyEvent e = new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED,
                                System.currentTimeMillis(), 0, KeyEvent.VK_5, '5');
                controller.keyPressed(e);

                verify(worm).setSelectedItem(items.get(4));
                verify(inventoryPanel).refresh();
        }

        @Test
        void testKeyPressed6() {
                ArrayList<model.items.Item> items = new ArrayList<>();
                for (int i = 1; i <= 6; i++) {
                        items.add(mock(model.items.Item.class));
                }
                when(inventory.getAvailableItems(team)).thenReturn(items);

                view.gui.InventoryPanel inventoryPanel = mock(view.gui.InventoryPanel.class);
                when(view.getInventoryPanel()).thenReturn(inventoryPanel);

                KeyEvent e = new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED,
                                System.currentTimeMillis(), 0, KeyEvent.VK_6, '6');
                controller.keyPressed(e);

                verify(worm).setSelectedItem(items.get(5));
                verify(inventoryPanel).refresh();
        }

        @Test
        void testKeyPressed7() {
                ArrayList<model.items.Item> items = new ArrayList<>();
                for (int i = 1; i <= 7; i++) {
                        items.add(mock(model.items.Item.class));
                }
                when(inventory.getAvailableItems(team)).thenReturn(items);

                view.gui.InventoryPanel inventoryPanel = mock(view.gui.InventoryPanel.class);
                when(view.getInventoryPanel()).thenReturn(inventoryPanel);

                KeyEvent e = new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED,
                                System.currentTimeMillis(), 0, KeyEvent.VK_7, '7');
                controller.keyPressed(e);

                verify(worm).setSelectedItem(items.get(6));
                verify(inventoryPanel).refresh();
        }

}
