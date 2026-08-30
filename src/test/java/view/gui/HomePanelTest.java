package view.gui;

import static org.mockito.Mockito.*;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;

import javax.swing.JButton;
import javax.swing.JMenuItem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HomePanelTest {

    GuiView mockView;
    GameMenu mockMenu;
    HomePanel homePanel;

    @BeforeEach
    void setUp() {
        mockView = mock(GuiView.class);
        mockView.gameSettingsPanel = mock(SettingsPanel.class);
        mockView.savePanel = mock(SavePanel.class);
        mockView.gameMenuBar = mock(GameMenu.class);

        mockView.gameMenuBar = mock(GameMenu.class);
        mockView.gameMenuBar.newGame = new JMenuItem("New Game");
        mockView.gameMenuBar.loadGame = new JMenuItem("Load Game");
        mockView.gameMenuBar.saveGame = new JMenuItem("Save Game");
        mockView.gameMenuBar.paramGame = new JMenuItem("Settings");

        homePanel = new HomePanel(mockView);
    }

    @Test
    void testLoadImagessuccess() throws Exception {
        Method loadImages = HomePanel.class.getDeclaredMethod("loadImages");
        loadImages.setAccessible(true);

        loadImages.invoke(homePanel);
        assertNotNull(homePanel);
    }

    @Test
    void testLoadImagesfailure() throws Exception {
        Method loadImages = HomePanel.class.getDeclaredMethod("loadImages");
        loadImages.setAccessible(true);
        loadImages.invoke(homePanel);
    }

    @Test
    void testInitializeComponentscreatesButtons() throws Exception {
        Method initComponents = HomePanel.class.getDeclaredMethod("initializeComponents");
        initComponents.setAccessible(true);
        initComponents.invoke(homePanel);

        assertNotNull(getField(homePanel, "newGameButton"));
        assertNotNull(getField(homePanel, "settingsButton"));
        assertNotNull(getField(homePanel, "loadButton"));
        assertNotNull(getField(homePanel, "exitButton"));
    }

    @Test
    void testSetupLayoutaddsButtons() throws Exception {
        Method setupLayout = HomePanel.class.getDeclaredMethod("setupLayout");
        setupLayout.setAccessible(true);
        setupLayout.invoke(homePanel);

        assertEquals(4, homePanel.getComponentCount());
    }

    @Test
    void testEventNewGameButtoncallsStartGame() throws Exception {
        Method eventNewGameButton = HomePanel.class.getDeclaredMethod("eventNewGameButton");
        eventNewGameButton.setAccessible(true);

        eventNewGameButton.invoke(homePanel);
        verify(mockView.gameSettingsPanel, times(1)).eventStartGameButton();
    }

    @Test
    void testEventSettingsButtonswitchesPanel() throws Exception {
        Method eventSettingsButton = HomePanel.class.getDeclaredMethod("eventSettingsButton");
        eventSettingsButton.setAccessible(true);

        eventSettingsButton.invoke(homePanel);
        verify(mockView).setContentPane(mockView.gameSettingsPanel);
        verify(mockView.gameSettingsPanel).setupMenu();
        verify(mockView).refresh();
    }

    @Test
    void testEventLoadButtonswitchesPanel() throws Exception {
        Method eventLoadButton = HomePanel.class.getDeclaredMethod("eventLoadButton");
        eventLoadButton.setAccessible(true);

        eventLoadButton.invoke(homePanel);
        verify(mockView).setContentPane(mockView.savePanel);
        verify(mockView.savePanel).setupMenu();
        verify(mockView).refresh();
    }

    @Test
    void testSetupMenucallsEnableDisable() {
        verify(mockView.gameMenuBar).enableMenu(mockView.gameMenuBar.newGame);
        verify(mockView.gameMenuBar).enableMenu(mockView.gameMenuBar.loadGame);
        verify(mockView.gameMenuBar).disableMenu(mockView.gameMenuBar.saveGame);
        verify(mockView.gameMenuBar).enableMenu(mockView.gameMenuBar.paramGame);
    }

    @Test
    void testPaintComponentwithBackground() {
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        setField(homePanel, "backgroundImage", img);

        BufferedImage canvas = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
        Graphics g = canvas.getGraphics();

        homePanel.paintComponent(g);

        g.dispose();
    }

    @Test
    void testPaintComponentwithoutBackground() {
        setField(homePanel, "backgroundImage", null);

        BufferedImage canvas = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
        Graphics g = canvas.getGraphics();

        homePanel.paintComponent(g);

        g.dispose();
    }

    private JButton getField(HomePanel panel, String name) throws Exception {
        var field = HomePanel.class.getDeclaredField(name);
        field.setAccessible(true);
        return (JButton) field.get(panel);
    }

    private void setField(HomePanel panel, String name, Object value) {
        try {
            var field = HomePanel.class.getDeclaredField(name);
            field.setAccessible(true);
            field.set(panel, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
