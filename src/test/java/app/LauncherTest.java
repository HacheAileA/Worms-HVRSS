package app;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import controller.console.ConsoleController;
import controller.gui.GuiController;
import model.GameModel;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import view.console.ConsoleView;
import view.gui.GuiView;

public class LauncherTest {

    GameModel modelMock;
    ConsoleView consoleViewMock;
    GuiView guiViewMock;
    ConsoleController consoleControllerMock;
    GuiController guiControllerMock;

    @BeforeEach
    void setup() throws Exception {

        modelMock = mock(GameModel.class);
        consoleViewMock = mock(ConsoleView.class);
        guiViewMock = mock(GuiView.class);
        consoleControllerMock = mock(ConsoleController.class);
        guiControllerMock = mock(GuiController.class);

        Launcher.modelFactory = () -> modelMock;
        Launcher.consoleViewFactory = (modelFactory) -> consoleViewMock;
        Launcher.guiViewFactory = (modelFactory) -> guiViewMock;

        Launcher.consoleCtrlFactory = (m, v) -> consoleControllerMock;
        Launcher.guiCtrlFactory = (m, v) -> guiControllerMock;
    }

    @Test
    void testInstanciation() {
        Launcher launcher = new Launcher();
        assertNotNull(launcher.getClass());
    }

    @Test
    void launchShouldUseGuiModeByDefault() throws Exception {

        Launcher.launch(new String[] { "gui" });

        verify(guiViewMock).setDevMode(false);
    }

    @Test
    void launchShouldUseConsoleModeWhenConsoleArgProvided() throws Exception {
        Launcher.launch(new String[] { "console" });

        verify(consoleViewMock).setDevMode(false);
        verify(consoleControllerMock).startApplication();
    }

    @Test
    void launchWithException() throws Exception {

        try (MockedStatic<ConsoleController> mock = mockStatic(ConsoleController.class)) {

            mock.when(ConsoleController::closeScanner)
                    .thenThrow(new RuntimeException());

            Launcher.launch(new String[] { "console" });
        }
    }

}
