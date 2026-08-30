package controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import model.GameModel;
import model.Map;
import model.players.Team;

class GameInitializationServiceTest {

    @Test
    void testGameSettingsSimpleConstructor() {
        GameInitializationService.GameSettings settings = new GameInitializationService.GameSettings(3, 4, false);
        assertEquals(3, settings.getNbTeam());
        assertEquals(4, settings.getNbWormsPerTeam());
        assertEquals(13, settings.getMapHeight());
        assertTrue(settings.getMapWidth() >= 10);
    }

    @Test
    void testInitTeamsDevMode() {
        Scanner scanner = new Scanner("");
        GameModel model = mock(GameModel.class);
        GameInitializationService service = new GameInitializationService(scanner, model, true);
        GameInitializationService.GameSettings settings = new GameInitializationService.GameSettings(2, 2, true);
        ArrayList<Team> teams = service.initTeams(settings, scanner, true);
        assertEquals(2, teams.size());
        assertEquals("Player", teams.get(0).getName());
        assertEquals("Bot", teams.get(1).getName());
    }

    @Test
    void testInitTeamsNormalModeWithEmptyNames() {
        String input = "\n\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        GameModel model = mock(GameModel.class);
        GameInitializationService service = new GameInitializationService(scanner, model, false);
        GameInitializationService.GameSettings settings = new GameInitializationService.GameSettings(2, 2, false);
        ArrayList<Team> teams = service.initTeams(settings, scanner, false);

        assertEquals(2, teams.size());
        assertEquals("Team1", teams.get(0).getName());
        assertEquals("Team2", teams.get(1).getName());
    }

    @Test
    void testInitTeamsNormalModeWithProvidedNames() {
        String input = "Alpha\nBeta\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        GameModel model = mock(GameModel.class);
        GameInitializationService service = new GameInitializationService(scanner, model, false);
        GameInitializationService.GameSettings settings = new GameInitializationService.GameSettings(2, 3, false);
        ArrayList<Team> teams = service.initTeams(settings, scanner, false);

        assertEquals(2, teams.size());
        assertEquals("Alpha", teams.get(0).getName());
        assertEquals("Beta", teams.get(1).getName());
        assertEquals(3, teams.get(0).getWorms().size());
    }

    @Test
    void testInitMap() {
        GameInitializationService.GameSettings settings = new GameInitializationService.GameSettings(2, 2, false);
        GameInitializationService service = new GameInitializationService(new Scanner(""), mock(GameModel.class), false);
        Map map = service.initMap(settings);
        assertEquals(settings.getMapHeight(), map.getHeight());
        assertEquals(settings.getMapWidth(), map.getWidth());
    }

    @Test
    void testInitializeNewGameConsoleDevModeCreatesModelAndTeams() {
        Scanner scanner = new Scanner("");
        GameInitializationService service = new GameInitializationService(scanner, mock(GameModel.class), true);
        GameModel gameModel = service.initializeNewGameConsole();

        assertNotNull(gameModel);
        assertNotNull(gameModel.getTeams());
        assertEquals(2, gameModel.getTeams().size());
        assertEquals("Player", gameModel.getTeams().get(0).getName());
        assertEquals("Bot", gameModel.getTeams().get(1).getName());
        assertEquals(2, gameModel.getTeams().get(0).getWorms().size());
        assertNotNull(gameModel.getCurrentTeam());
    }
}
