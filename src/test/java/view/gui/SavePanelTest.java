package view.gui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;

import javax.swing.JButton;
import javax.swing.JPanel;

import model.GameModel;
import model.persistence.LoadManager;

class SavePanelTest {

    @TempDir
    File tempDir;

    private GuiView view;
    private GameMenu menu;
    private HomePanel homePanel;

    @BeforeAll
    static void headless() {
        System.setProperty("java.awt.headless", "true");
    }

    @BeforeEach
    void setup() throws Exception {
        view = mock(GuiView.class);
        menu = mock(GameMenu.class);
        homePanel = mock(HomePanel.class);

        menu.newGame = null;
        menu.loadGame = null;
        menu.saveGame = null;
        menu.paramGame = null;

        view.gameMenuBar = menu;
        view.homePanel = homePanel;

        File saves = new File("../saves/");
        if (saves.exists()) {
            deleteRecursively(saves);
        }
        saves.mkdirs();
    }

    @AfterEach
    void cleanup() {
        deleteRecursively(new File("../saves/"));
    }

    @Test
    void constructorcreatesPanelwhenNoSaveFilesExist() {
        SavePanel panel = new SavePanel(view);

        assertNotNull(panel);
        assertTrue(panel.getComponentCount() >= 2);
    }

    @Test
    void constructorcreatesButtonsforNonEmptySaveFiles() throws Exception {
        File savesDir = new File("../saves/");
        File validSave = new File(savesDir, "game1.sav");
        try (FileOutputStream fos = new FileOutputStream(validSave)) {
            fos.write(1);
        }

        File emptySave = new File(savesDir, "empty.sav");
        emptySave.createNewFile();

        SavePanel panel = new SavePanel(view);

        boolean foundSaveButton = false;
        for (Component c : panel.getComponents()) {
            if (c instanceof JPanel) {
                JPanel listPanel = (JPanel) c;
                for (Component child : listPanel.getComponents()) {
                    if (child instanceof JButton) {
                        foundSaveButton = true;
                    }
                }
            }
        }
        assertTrue(foundSaveButton);
    }

    @Test
    void setupMenuenablesAndDisablesCorrectMenus() {
        SavePanel panel = new SavePanel(view);

        panel.setupMenu();

        verify(menu, times(1)).enableMenu(menu.newGame);
        verify(menu, times(3)).disableMenu(null);
    }

    @Test
    void clickingSaveButtondoesNothingwhenLoadReturnsNull() throws Exception {
        File savesDir = new File("../saves/");
        File save = new File(savesDir, "null.sav");
        try (FileOutputStream fos = new FileOutputStream(save)) {
            fos.write(1);
        }

        try (MockedStatic<LoadManager> mocked = Mockito.mockStatic(LoadManager.class)) {
            mocked.when(() -> LoadManager.load(save)).thenReturn(null);

            SavePanel panel = new SavePanel(view);
            JButton saveButton = findFirstButton(panel, "null");

            assertNotNull(saveButton);
            saveButton.doClick();

            verify(view, never()).setModel(any());
            verify(view, never()).refresh();
            verify(view, never()).showMap();
        }
    }

    @Test
    void clickingSaveButtonloadsGameAndRefreshesView() throws Exception {
        File savesDir = new File("../saves/");
        File save = new File(savesDir, "valid.sav");
        try (FileOutputStream fos = new FileOutputStream(save)) {
            fos.write(1);
        }

        GameModel model = mock(GameModel.class);
        LoadManager.LoadResult result = mock(LoadManager.LoadResult.class);

        Field modelField = LoadManager.LoadResult.class.getDeclaredField("model");
        modelField.setAccessible(true);
        modelField.set(result, model);

        try (MockedStatic<LoadManager> mocked = Mockito.mockStatic(LoadManager.class)) {
            mocked.when(() -> LoadManager.load(save)).thenReturn(result);

            SavePanel panel = new SavePanel(view);
            JButton saveButton = findFirstButton(panel, "valid");

            saveButton.doClick();

            verify(view).setModel(model);
            verify(view).showMap();
            verify(view).refresh();
        }
    }

    @Test
    void clickingCancelButtonreturnsToHomePanel() {
        SavePanel panel = new SavePanel(view);

        JButton cancelButton = findFirstButton(panel, "Retour");
        assertNotNull(cancelButton);

        cancelButton.doClick();

        verify(view).setContentPane(view.homePanel);
        verify(view.homePanel).setupMenu();
        verify(view).refresh();
    }

    private JButton findFirstButton(Container root, String text) {
        for (Component c : root.getComponents()) {
            if (c instanceof JButton && ((JButton) c).getText().contains(text)) {
                return (JButton) c;
            }
            if (c instanceof Container) {
                JButton found = findFirstButton((Container) c, text);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private void deleteRecursively(File file) {
        if (file == null || !file.exists())
            return;
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                deleteRecursively(f);
            }
        }
        file.delete();
    }
}
