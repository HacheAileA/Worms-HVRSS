package view.console;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConsoleHelperTest {

    @BeforeEach
    void setUp() {
        ConsoleHelper consoleHelper = new ConsoleHelper();
        assertNotNull(consoleHelper);
    }

    @Test
    void testInit() {
        ConsoleHelper consoleHelper = new ConsoleHelper();
        assertNotNull(consoleHelper);
    }

    @Test
    void testClearConsole() {
        ConsoleHelper.clearConsole();
    }

    @Test
    void testSleepTimeWithDevModeOn() throws InterruptedException {
        ConsoleHelper.sleepTime(10);
    }

    @Test
    void testSleepTimeWithoutDevMode() throws InterruptedException {
        ConsoleHelper.setDevMode(true);
        assertDoesNotThrow(() -> ConsoleHelper.sleepTime(0));
    }

    @Test
    void testSleepTimeWithDevMode() throws InterruptedException {
        ConsoleHelper.setDevMode(false);
        assertDoesNotThrow(() -> ConsoleHelper.sleepTime(0));
    }
}
