package view.console;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AnsiColorTest {

    @BeforeEach
    void setUp() {
        AnsiColor color = new AnsiColor();
        assertTrue(color.getClass() == AnsiColor.class);
    }

    @Test
    void testColor() {
        String texte = "Hello";
        String colored = AnsiColor.color("\u001B[31m", texte);
        assertEquals("\u001B[31mHello\u001B[0m", colored);
    }

    @Test
    void testGetColoredStringRed() {
        String texte = "RedText";
        String colored = AnsiColor.getColoredString("RED", texte);
        assertEquals("\u001B[31mRedText\u001B[0m", colored);
    }

    @Test
    void testGetColoredStringBlue() {
        String texte = "BlueText";
        String colored = AnsiColor.getColoredString("BLUE", texte);
        assertEquals("\u001B[34mBlueText\u001B[0m", colored);
    }

    @Test
    void testGetColoredStringGreen() {
        String texte = "GreenText";
        String colored = AnsiColor.getColoredString("GREEN", texte);
        assertEquals("\u001B[32mGreenText\u001B[0m", colored);
    }

    @Test
    void testGetColoredStringYellow() {
        String texte = "YellowText";
        String colored = AnsiColor.getColoredString("YELLOW", texte);
        assertEquals("\u001B[33mYellowText\u001B[0m", colored);
    }

    @Test
    void testGetColoredStringCyan() {
        String texte = "CyanText";
        String colored = AnsiColor.getColoredString("CYAN", texte);
        assertEquals("\u001B[36mCyanText\u001B[0m", colored);
    }

    @Test
    void testGetColoredStringMagenta() {
        String texte = "MagentaText";
        String colored = AnsiColor.getColoredString("MAGENTA", texte);
        assertEquals("\u001B[35mMagentaText\u001B[0m", colored);
    }

    @Test
    void testGetColoredStringBrightBlue() {
        String texte = "BrightBlueText";
        String colored = AnsiColor.getColoredString("BRIGHT_BLUE", texte);
        assertEquals("\u001B[94mBrightBlueText\u001B[0m", colored);
    }

    @Test
    void testGetColoredStringBrown() {
        String texte = "BrownText";
        String colored = AnsiColor.getColoredString("BROWN", texte);
        assertEquals("\u001B[38;5;94mBrownText\u001B[0m", colored);
    }

    @Test
    void testGetColoredStringDefault() {
        String texte = "DefaultText";
        String colored = AnsiColor.getColoredString("UNKNOWN_COLOR", texte);
        assertEquals("\u001B[37mDefaultText\u001B[0m", colored);
    }
}
