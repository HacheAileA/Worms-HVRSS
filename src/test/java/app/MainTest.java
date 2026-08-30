package app;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

class MainTest {

    @Test
    void mainShouldCallLauncherWithoutException() {
        try (MockedStatic<Launcher> launcherMock = mockStatic(Launcher.class)) {
            launcherMock.when(() -> Launcher.launch(new String[]{})).thenCallRealMethod();
            
            launcherMock.when(() -> Launcher.launch(any())).thenAnswer(invocation -> null);

            assertDoesNotThrow(() -> Main.main(new String[]{}));

            launcherMock.verify(() -> Launcher.launch(new String[]{}));
        }
    }

    @Test
    void mainShouldHandleException() {
        try (MockedStatic<Launcher> launcherMock = mockStatic(Launcher.class)) {
            launcherMock.when(() -> Launcher.launch(new String[]{}))
                        .thenThrow(new RuntimeException());

            assertDoesNotThrow(() -> Main.main(new String[]{}));

            launcherMock.verify(() -> Launcher.launch(new String[]{}));
        }
    }

    @Test
    void testConstructor() {
        assertDoesNotThrow(() -> new Main());
    }
}
