package view.gui;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.GameModel;
import model.items.Item;
import model.items.guns.Guns;
import model.items.tools.Tools;
import model.players.Team;
import model.players.Worm;

class InventoryPanelTest {

    private GameModel model;
    private GuiView guiView;
    private InventoryPanel panel;
    private Worm worm;
    private Team team;

    @BeforeEach
    void setup() {
        model = mock(GameModel.class);
        guiView = mock(GuiView.class);
        panel = new InventoryPanel(model, guiView);

        worm = mock(Worm.class);
        team = mock(Team.class);

        when(model.getCurrentWorm()).thenReturn(worm);
        when(worm.getTeam()).thenReturn(team);

        JPanel trajectoryLayer = mock(JPanel.class);
        GamePanel gamePanel = mock(GamePanel.class);

        when(guiView.getTrajectoryLayer()).thenReturn(trajectoryLayer);
        when(guiView.getGamePanel()).thenReturn(gamePanel);
    }

    @Test
    void testRefreshAddsButtonsForItems() {
        Item gun = mock(Guns.class);
        when(gun.getName()).thenReturn("Bazooka");
        when(((Guns) gun).getAmmo()).thenReturn(5);

        Item tool = mock(Item.class);
        when(tool.getName()).thenReturn("Banana");

        when(team.getInventory()).thenReturn(mock(model.items.Inventory.class));
        when(team.getInventory().getAvailableItems(team)).thenReturn(new ArrayList<>(Arrays.asList(gun, tool)));
        panel.refresh();

        assertEquals(2, panel.getComponentCount());
        JButton firstButton = (JButton) panel.getComponent(0);
        JButton secondButton = (JButton) panel.getComponent(1);

        assertEquals("Bazooka (5)", firstButton.getText());
        assertEquals("Banana (0)", secondButton.getText());

        firstButton.doClick();

        verify(worm).setSelectedItem(gun);
    }

    @Test
    void testRefreshWithNoCurrentWorm() {
        when(model.getCurrentWorm()).thenReturn(null);
        panel.refresh();
        assertEquals(0, panel.getComponentCount());
    }

    @Test
    void testRefreshWithNoTeam() {
        when(worm.getTeam()).thenReturn(null);
        panel.refresh();
        assertEquals(0, panel.getComponentCount());
    }

    @Test
    void testRefreshHandlesImageIOException() {
        Item fakeItem = mock(Item.class);
        when(fakeItem.getName()).thenReturn("NonExistentImage");

        when(team.getInventory()).thenReturn(mock(model.items.Inventory.class));
        when(team.getInventory().getAvailableItems(team)).thenReturn(new ArrayList<>(Arrays.asList(fakeItem)));
        panel.refresh();

        assertEquals(1, panel.getComponentCount());
        JButton button = (JButton) panel.getComponent(0);
        assertEquals("NonExistentImage (0)", button.getText());
    }

    @Test
    void testRefreshWithToolsItem() {
        Tools tool = mock(Tools.class);
        when(tool.getName()).thenReturn("Jetpack");
        when(tool.getAmmo()).thenReturn(3);

        when(team.getInventory()).thenReturn(mock(model.items.Inventory.class));
        when(team.getInventory().getAvailableItems(team)).thenReturn(new ArrayList<>(Arrays.asList(tool)));

        panel.refresh();

        JButton button = (JButton) panel.getComponent(0);
        assertEquals("Jetpack (3)", button.getText());

        button.doClick();
        verify(worm).setSelectedItem(tool);
    }

}
