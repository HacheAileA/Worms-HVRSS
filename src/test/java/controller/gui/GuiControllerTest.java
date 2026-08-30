package controller.gui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.io.File;

import javax.swing.JOptionPane;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import model.GameModel;
import model.persistence.SaveManager;
import model.players.Team;
import view.gui.GuiView;

class GuiControllerTest {

    @Test
    void testSetDevModetrue() {
        GameModel model = mock(GameModel.class);
        GuiView view = mock(GuiView.class);

        GuiController controller = new GuiController(model, view);

        controller.setDevMode(true);

        assertTrue(controller.devMode);
    }

    @Test
    void testSetDevModefalse() {
        GameModel model = mock(GameModel.class);
        GuiView view = mock(GuiView.class);

        GuiController controller = new GuiController(model, view);

        controller.setDevMode(false);

        assertFalse(controller.devMode);
    }

    @Test
    void testSaveGamemodelNullshowsErrorMessage() {
        GuiView view = mock(GuiView.class);
        GuiController controller = new GuiController(null, view);

        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            controller.saveGame();

            mockedPane.verify(() -> JOptionPane.showMessageDialog(
                    eq(view),
                    eq("Aucune partie en cours à sauvegarder."),
                    eq("Erreur"),
                    eq(JOptionPane.ERROR_MESSAGE)));
        }
    }

    @Test
    void testSaveGamecurrentTeamNullshowsErrorMessage() {
        GameModel model = mock(GameModel.class);
        GuiView view = mock(GuiView.class);

        when(model.getCurrentTeam()).thenReturn(null);

        GuiController controller = new GuiController(model, view);

        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            controller.saveGame();

            mockedPane.verify(() -> JOptionPane.showMessageDialog(
                    eq(view),
                    eq("Aucune partie en cours à sauvegarder."),
                    eq("Erreur"),
                    eq(JOptionPane.ERROR_MESSAGE)));
        }
    }

    @Test
    void testSaveGamefilenameNullreturnsEarly() {
        GameModel model = mock(GameModel.class);
        Team team = mock(Team.class);
        GuiView view = mock(GuiView.class);

        when(model.getCurrentTeam()).thenReturn(team);

        GuiController controller = new GuiController(model, view);

        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            mockedPane.when(() -> JOptionPane.showInputDialog(any(), any(), any(), anyInt())).thenReturn(null);

            controller.saveGame();

            mockedPane.verify(() -> JOptionPane.showInputDialog(any(), any(), any(), anyInt()));
        }
    }

    @Test
    void testSaveGameemptyFilenamereturnsEarly() {
        GameModel model = mock(GameModel.class);
        Team team = mock(Team.class);
        GuiView view = mock(GuiView.class);

        when(model.getCurrentTeam()).thenReturn(team);

        GuiController controller = new GuiController(model, view);

        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            mockedPane.when(() -> JOptionPane.showInputDialog(any(), any(), any(), anyInt())).thenReturn("   ");

            controller.saveGame();
        }
    }

    @Test
    void testSaveGamesuccessfulSave() {
        GameModel model = mock(GameModel.class);
        Team team = mock(Team.class);
        GuiView view = mock(GuiView.class);

        when(model.getCurrentTeam()).thenReturn(team);

        GuiController controller = new GuiController(model, view);

        try (
                MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class);
                MockedStatic<SaveManager> mockedSave = mockStatic(SaveManager.class)) {
            mockedPane.when(() -> JOptionPane.showInputDialog(any(), any(), any(), anyInt())).thenReturn("save1");

            mockedPane.when(() -> JOptionPane.showConfirmDialog(any(), any(), any(), anyInt(), anyInt()))
                    .thenReturn(JOptionPane.YES_OPTION);

            controller.saveGame();

            mockedSave.verify(() -> SaveManager.save(eq(model), eq("save1.txt"), eq(team)));

            mockedPane.verify(() -> JOptionPane.showMessageDialog(
                    eq(view),
                    eq("Savegarde effectuée avec succès."),
                    eq("Sauvegarde"),
                    eq(JOptionPane.INFORMATION_MESSAGE)));
        }
    }

    @Test
    void testSaveGamefileExistsuserRefuseOverwrite() {
        GameModel model = mock(GameModel.class);
        Team team = mock(Team.class);
        GuiView view = mock(GuiView.class);

        when(model.getCurrentTeam()).thenReturn(team);

        GuiController controller = new GuiController(model, view);

        try (
                MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class);
                MockedStatic<SaveManager> mockedSave = mockStatic(SaveManager.class)) {
            mockedPane.when(() -> JOptionPane.showInputDialog(any(), any(), any(), anyInt())).thenReturn("test");

            mockedPane.when(() -> JOptionPane.showConfirmDialog(any(), any(), any(), anyInt(), anyInt()))
                    .thenReturn(JOptionPane.NO_OPTION);

            controller.saveGame();
        }
    }

    @Test
    void testSaveGamefileExistsuserRefusesOverwrite() throws Exception {
        GameModel model = mock(GameModel.class);
        Team team = mock(Team.class);
        GuiView view = mock(GuiView.class);

        when(model.getCurrentTeam()).thenReturn(team);

        GuiController controller = new GuiController(model, view);

        File dir = new File("../saves");
        dir.mkdirs();
        File file = new File("../saves/testSave.txt");
        file.createNewFile();

        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class);
                MockedStatic<SaveManager> mockedSave = mockStatic(SaveManager.class)) {

            mockedPane.when(() -> JOptionPane.showInputDialog(any(), any(), any(), anyInt()))
                    .thenReturn("testSave");

            mockedPane.when(() -> JOptionPane.showConfirmDialog(any(), any(), any(), anyInt(), anyInt()))
                    .thenReturn(JOptionPane.NO_OPTION);

            controller.saveGame();

            mockedSave.verifyNoInteractions();

            mockedPane.verify(() -> JOptionPane.showMessageDialog(eq(view),
                    eq("Savegarde effectuée avec succès."),
                    anyString(),
                    anyInt()),
                    times(0));
        }

        file.delete();
    }

}
