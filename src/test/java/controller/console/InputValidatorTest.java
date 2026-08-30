package controller.console;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.jupiter.api.Test;


public class InputValidatorTest {
    

    private void provideInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
    }

    @Test
    void testCheckIsIntIsValid() {
        provideInput("10\n");
        Scanner scanner = new Scanner(System.in);
        int res = InputValidator.checkIsInt(scanner, "Message pour le test de checkIsInt");
        assertEquals(10, res);
        scanner.close();
    }

    @Test
    void testCheckIsIntInvalidIsValid() {
        provideInput("retour\n");
        Scanner scanner = new Scanner(System.in);
        int res = InputValidator.checkIsInt(scanner, "Message pour le test de checkIsIntInvalid");
        assertEquals(-1, res);
        scanner.close();
    }

    @Test
    void testCheckIsIntNotValid() {
        provideInput("NotIntInput\n42\n");
        Scanner sc = new Scanner(System.in);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        int res = InputValidator.checkIsInt(sc, "Message pour le test de checkIsIntInvalid");

        System.setOut(originalOut);

        assertEquals(42, res);

        String consoleOutput = outContent.toString();

        assertTrue(consoleOutput.contains("Ceci n'est pas un nombre"), "Le message d'erreur doit être affiché sur la console");

        sc.close();
    }

    @Test
    void testCheckIsDoubleIsValid() {
        provideInput("10.0\n");
        Scanner scanner = new Scanner(System.in);
        double res = InputValidator.checkIsDouble(scanner, "Message pour le test de checkIsDouble");
        assertEquals(10.0, res);
        scanner.close();
    }

    @Test
    void testCheckIsDoubleInvalidIsValid() {
        provideInput("retour\n");
        Scanner scanner = new Scanner(System.in);
        double res = InputValidator.checkIsDouble(scanner, "Message pour le test de checkIsDoubleInvalid");
        assertEquals(Double.NaN, res);
        scanner.close();
    }

    @Test
    void testGetStringOrCancelIsValid() {
        provideInput("Java\n");
        Scanner scanner = new Scanner(System.in);
        String res = InputValidator.getStringOrCancel(scanner, "Message pour le test de getStringOrCancelIsValid");
        assertEquals("Java", res);

        provideInput("retour\n");
        scanner = new Scanner(System.in);
        res = InputValidator.getStringOrCancel(scanner, "Message pour le test de getStringOrCancelIsValid");
        assertEquals(null, res);

        scanner.close();
    }

    @Test
    void testInitInputValidator() {
        InputValidator inputValidator = new InputValidator();
        assertNotNull(inputValidator);
    }
}
