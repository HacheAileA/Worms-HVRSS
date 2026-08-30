package view.gui;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import javax.swing.JMenuItem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.GameModel;
import model.players.Team;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

class GuiTestEndGamePanelTest {

    private EndGamePanel panel;
    private GuiView view;
    private GameModel model;
    private GameMenu menuBar;
    private HomePanel homePanel;
    private SoundPlayer soundPlayer;

    @BeforeEach
    void setup() {
        view = mock(GuiView.class);
        model = mock(GameModel.class);
        menuBar = mock(GameMenu.class);
        homePanel = mock(HomePanel.class);
        soundPlayer = mock(SoundPlayer.class);
        menuBar.newGame = mock(JMenuItem.class);
        menuBar.loadGame = mock(JMenuItem.class);
        menuBar.saveGame = mock(JMenuItem.class);
        menuBar.paramGame = mock(JMenuItem.class);
        Team winningTeam = mock(Team.class);
        when(winningTeam.getName()).thenReturn("TeamTest");
        when(model.getWinningTeam()).thenReturn(winningTeam);
        view.model = model;
        view.gameMenuBar = menuBar;
        view.homePanel = homePanel;
        view.soundPlayer = soundPlayer;
        panel = new EndGamePanel(view);
    }

    @Test
    void testPanelIsCreated() {
        assert panel != null;
    }

    @Test
    void testSetupMenu() {
        panel.setupMenu();

        verify(menuBar).enableMenu(menuBar.newGame);
        verify(menuBar).disableMenu(menuBar.loadGame);
        verify(menuBar).disableMenu(menuBar.saveGame);
        verify(menuBar).disableMenu(menuBar.paramGame);
    }

    @Test
    void testPaintComponentStopsMusic() {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image.getGraphics();

        panel.paintComponent(graphics);

        verify(soundPlayer).stopMusic();
    }

    @Test
    void testWinningTeamIsRequestedFromModel() {
        verify(model).getWinningTeam();
    }

    @Test
    void testImageLoadingFailureDoesNotCrash() {
        GuiView view = mock(GuiView.class);
        GameModel model = mock(GameModel.class);
        Team team = mock(Team.class);

        when(team.getName()).thenReturn("Test");
        when(model.getWinningTeam()).thenReturn(team);

        view.model = model;

        EndGamePanel panel = new EndGamePanel(view);

        assertNotNull(panel);
    }

    @Test
    void testPaintComponentWithNullImage() throws Exception {
        Field imageField = EndGamePanel.class.getDeclaredField("image");
        imageField.setAccessible(true);
        imageField.set(panel, null);

        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();

        panel.paintComponent(g);

        verify(soundPlayer).stopMusic();
    }

    @Test
    void testBackHomeButtonEvent() throws Exception {
        Method eventBackHomeButtonMethod = EndGamePanel.class.getDeclaredMethod("eventBackHomeButton");
        eventBackHomeButtonMethod.setAccessible(true); 

        EndGamePanel spy = spy(panel); 
        doNothing().when(view).setContentPane(any());
        doNothing().when(view).refresh();
        doNothing().when(view).dispose();

        eventBackHomeButtonMethod.invoke(spy);

        verify(view).setContentPane(view.homePanel);
        verify(homePanel).setupMenu();
        verify(view).refresh();
        verify(view).dispose();
    }
}
