package view.gui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.swing.JMenuItem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controller.gui.GuiController;

class GameMenuTest {

    GuiView view;
    GameMenu gameMenu;

    @BeforeEach
    void setUp() {
        view = mock(GuiView.class);

        view.savePanel = mock(SavePanel.class);
        view.homePanel = mock(HomePanel.class);
        view.gameSettingsPanel = mock(SettingsPanel.class);

        view.turnTimer = mock(javax.swing.Timer.class);
        view.projectileTimer = mock(javax.swing.Timer.class);
        view.soundPlayer = mock(SoundPlayer.class);

        GuiController mockController = mock(GuiController.class);
        when(view.getController()).thenReturn(mockController);

        gameMenu = new GameMenu(view);
    }

    @Test
    void testNewGameButtonCallsSettingsPanel() {
        JMenuItem newGame = gameMenu.newGame;
        assertNotNull(newGame);

        newGame.doClick();
        verify(view.gameSettingsPanel).eventStartGameButton();
    }

    @Test
    void testLoadGameButtonSwitchesToSavePanel() {
        JMenuItem loadGame = gameMenu.loadGame;
        assertNotNull(loadGame);

        loadGame.doClick();
        verify(view).setContentPane(view.savePanel);
        verify(view.savePanel).setupMenu();
        verify(view).refresh();
    }

    @Test
    void testSaveGameButtonCallsController() {
        JMenuItem saveGame = gameMenu.saveGame;
        assertNotNull(saveGame);

        saveGame.doClick();
        verify(view.getController()).saveGame();
    }

    @Test
    void testOpenParametersButtonSwitchesToSettingsPanel() {
        JMenuItem paramGame = gameMenu.paramGame;
        assertNotNull(paramGame);

        paramGame.doClick();
        verify(view).setContentPane(view.gameSettingsPanel);
        verify(view.gameSettingsPanel).setupMenu();
        verify(view).refresh();
    }

    @Test
    void testQuitGameButtonExits() {
        JMenuItem quitGame = gameMenu.quitGame;
        assertNotNull(quitGame);

        assertEquals("Quitter", quitGame.getText());
    }

    @Test
    void testEnableDisableMenu() {
        JMenuItem item = new JMenuItem();
        gameMenu.disableMenu(item);
        assertFalse(item.isEnabled());

        gameMenu.enableMenu(item);
        assertTrue(item.isEnabled());
    }
}
