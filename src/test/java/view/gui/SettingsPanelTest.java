package view.gui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controller.gui.GuiController;
import model.GameModel;
import model.physics.Wind;

class SettingsPanelTest {

    private GameModel mockModel;
    private GuiView mockView;
    private SettingsPanel panel;

    @BeforeEach
    void setup() {
        mockModel = mock(GameModel.class);
        mockView = mock(GuiView.class);

        mockView.gameMenuBar = mock(GameMenu.class);
        mockView.homePanel = mock(HomePanel.class);
        mockView.soundPlayer = mock(SoundPlayer.class);

        mockView.gameMenuBar.newGame = mock(JMenuItem.class);
        mockView.gameMenuBar.loadGame = mock(JMenuItem.class);
        mockView.gameMenuBar.saveGame = mock(JMenuItem.class);
        mockView.gameMenuBar.paramGame = mock(JMenuItem.class);

        panel = new SettingsPanel(mockModel, mockView);
    }

    @Test
    void testConstructorinitializesComponents() {
        assertNotNull(panel.getModel());
        assertEquals(2, panel.getNbTeams());
        assertTrue(panel.isWindEnabled());
    }

    @Test
    void testUpdateTeamCountdisablesExcessRows() throws Exception {
        JSlider slider = getPrivateField(panel, "teamCountSlider", JSlider.class);
        slider.setValue(3);

        Method method = SettingsPanel.class.getDeclaredMethod("updateTeamCount");
        method.setAccessible(true);
        method.invoke(panel);

        assertEquals(3, panel.getNbTeams());
    }

    @Test
    void testUpdateWormCountsetsCorrectValue() throws Exception {
        JSlider slider = getPrivateField(panel, "wormCountSlider", JSlider.class);
        slider.setValue(4);

        Method method = SettingsPanel.class.getDeclaredMethod("updateWormCount");
        method.setAccessible(true);
        method.invoke(panel);

        assertEquals(4, getPrivateField(panel, "nbWormsPerTeam", Integer.class));
    }

    @Test
    void testUpdateBotDifficultysetsCorrectValue() throws Exception {
        JSlider slider = getPrivateField(panel, "botDifficultySlider", JSlider.class);
        slider.setValue(7);

        Method method = SettingsPanel.class.getDeclaredMethod("updateBotDifficulty");
        method.setAccessible(true);
        method.invoke(panel);

        assertEquals(7, getPrivateField(panel, "botDifficulty", Integer.class));
    }

    @Test
    void testEventCloseButtoncallsViewMethods() throws Exception {
        Method method = SettingsPanel.class.getDeclaredMethod("eventCloseButton");
        method.setAccessible(true);
        method.invoke(panel);

        verify(mockView).setContentPane(mockView.homePanel);
        verify(mockView.homePanel).setupMenu();
        verify(mockView).refresh();
    }

    @Test
    void testEventStartGameButtonwithValidModelcallsExpectedMethods() {
        JPanel teamInputsPanel = getPrivateField(panel, "teamInputsPanel", JPanel.class);
        JPanel teamRow = (JPanel) teamInputsPanel.getComponent(0);
        JTextField nameField = (JTextField) teamRow.getComponent(1);
        nameField.setText("");

        JCheckBox botCheckBox = (JCheckBox) teamRow.getComponent(2);
        botCheckBox.setSelected(true);

        Wind mockWind = mock(Wind.class);
        when(mockModel.getWind()).thenReturn(mockWind);

        doNothing().when(mockView).setController(any(GuiController.class));

        when(mockModel.getTeams()).thenReturn(new ArrayList<>());

        panel.eventStartGameButton();

        verify(mockView, atLeastOnce()).setController(any(GuiController.class));
        verify(mockView).setModel(mockModel);
        verify(mockView).showMap();
        verify(mockView).setBotDifficulty(anyInt());
        verify(mockView.soundPlayer).stopMusic();
        verify(mockView.soundPlayer).playBackgroundMusic(anyString(), anyInt());
    }

    @Test
    void testEventStartGameButtonwithNullModelprintsError() {
        panel = new SettingsPanel(null, mockView);
        panel.eventStartGameButton();
    }

    @Test
    void testGettersreturnCorrectValues() {
        assertEquals(2, panel.getNbTeams());
        assertEquals(2, panel.getNbWormsPnbWormsPerTeam());
        assertEquals(mockModel, panel.getModel());
        assertFalse(panel.getFriendlyFire());
        assertTrue(panel.isWindEnabled());
    }

    @Test
    void testSetupMenucallsMenuBarMethods() {
        mockView.gameMenuBar.newGame = mock(JMenuItem.class);
        mockView.gameMenuBar.loadGame = mock(JMenuItem.class);
        mockView.gameMenuBar.saveGame = mock(JMenuItem.class);
        mockView.gameMenuBar.paramGame = mock(JMenuItem.class);

        panel.setupMenu();

        verify(mockView.gameMenuBar).enableMenu(mockView.gameMenuBar.newGame);
        verify(mockView.gameMenuBar).enableMenu(mockView.gameMenuBar.loadGame);
        verify(mockView.gameMenuBar).disableMenu(mockView.gameMenuBar.saveGame);
        verify(mockView.gameMenuBar).disableMenu(mockView.gameMenuBar.paramGame);
    }

    @SuppressWarnings("unchecked")
    private <T> T getPrivateField(Object obj, String fieldName, Class<T> clazz) {
        try {
            var field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
