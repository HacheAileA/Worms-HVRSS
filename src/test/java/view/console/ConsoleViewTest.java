package view.console;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.GameModel;
import model.players.Team;
import model.players.Worm;
import model.Map;
import model.items.Inventory;

public class ConsoleViewTest {

    private GameModel model;
    private ConsoleView view;

    @BeforeEach
    void setUp() {
        model = mock(GameModel.class);
        view = new ConsoleView(model);

        Map map = new Map(3, 2);
        map.grid = new char[][] {
                { '~', '#', '.' },
                { '.', '.', '.' }
        };

        when(model.getMap()).thenReturn(map);

        view.setModel(model);
        view.setDevMode(true);
    }

    @Test
    void testShowInventory() {
        GameModel model = mock(GameModel.class);
        Team team = mock(Team.class);
        Inventory inventory = mock(Inventory.class);

        when(model.getCurrentTeam()).thenReturn(team);
        when(team.getInventory()).thenReturn(inventory);
        when(inventory.getAvailableItems(team)).thenReturn(new ArrayList<>());

        view.setModel(model);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        view.showInventory();

        String output = out.toString();

        assertTrue(output.contains("Armes"));
    }

    @Test
    void testShowNewTurnInfo() {
        view = spy(new ConsoleView(model));
        view.setModel(model);

        Team team = mock(Team.class);
        Worm worm = mock(Worm.class);

        when(model.getCurrentTeam()).thenReturn(team);
        when(model.getCurrentWorm()).thenReturn(worm);

        when(team.getColoredName()).thenReturn("Team 1");
        when(worm.getSymbol()).thenReturn('W');

        doNothing().when(view).showWormInfo(any());

        view.showNewTurnInfo();

        verify(view).showWormInfo(worm);
    }

    @Test
    void testShowEndGameMessageWithParameter() {
        GameModel model = mock(GameModel.class);
        Team team = mock(Team.class);

        when(model.getWinningTeam()).thenReturn(team);
        when(team.getName()).thenReturn("Team 1");

        view.setModel(model);

        view.showEndGameMessage();

        verify(team).getName();
    }

    @Test
    void testShowEndGameMessageWithoutParameter() {
        assertDoesNotThrow(() -> view.showEndGameMessage());
    }

    @Test
    void testShowTeamInfo() {

        view = spy(new ConsoleView(model));
        view.setModel(model);

        Team team = mock(Team.class);
        Worm w1 = mock(Worm.class);
        Worm w2 = mock(Worm.class);

        ArrayList<Worm> worms = new ArrayList<>();
        worms.add(w1);
        worms.add(w2);

        when(model.getCurrentTeam()).thenReturn(team);
        when(team.getWorms()).thenReturn(worms);
        when(team.getColoredName()).thenReturn("Team 1");

        doNothing().when(view).showWormInfo(any());

        view.showTeamInfo();

        verify(view).showWormInfo(w1);
        verify(view).showWormInfo(w2);
    }

    @Test
    void testShowWormInfo() {
        Worm worm = mock(Worm.class);

        when(worm.getName()).thenReturn("wormTest");
        when(worm.getHp()).thenReturn(100);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        view.showWormInfo(worm);

        String output = out.toString();
        assertTrue(output.contains("wormTest"));
        assertTrue(output.contains("100"));
    }

    @Test
    void testShowStartGameMessage() {
        assertDoesNotThrow(() -> view.showStartGameMessage());
    }

    @Test
    void testShowMainMenu() {
        assertDoesNotThrow(() -> view.showMainMenu());
    }

    @Test
    void testShowActionsMenu() {
        assertDoesNotThrow(() -> view.showActionsMenu());
    }

    @Test
    void testShowItemChoiceMenu() {
        assertDoesNotThrow(() -> view.showItemChoiceMenu());
    }

    @Test
    void testShowShootMessage() {
        assertDoesNotThrow(() -> view.showShootMessage());
    }

    @Test
    void testShowIncorrectChoiceMessage() {
        assertDoesNotThrow(() -> view.showIncorrectChoiceMessage());
    }

    @Test
    void testShowNextFeature() {
        assertDoesNotThrow(() -> view.showNextFeature());
    }

    @Test
    void testShowQuitMessageprintsMessage() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        view.showQuitMessage();

        String output = out.toString().trim();
        assertEquals("Fermeture du jeu en cours...", output);
    }

    @Test
    void testShowFailLoadprintsMessage() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        view.showFailLoad();

        String output = out.toString().trim();
        assertEquals("Impossible de charger la sauvegarde !", output);
    }

    @Test
    void testShowGameModelInfoprintsTeamAndWormsInfo() {
        GameModel model = mock(GameModel.class);
        Team team = mock(Team.class);
        Worm worm1 = mock(Worm.class);
        Worm worm2 = mock(Worm.class);

        ArrayList<Worm> worms = new ArrayList<>();
        worms.add(worm1);
        worms.add(worm2);

        when(model.getTeams()).thenReturn(new ArrayList<>() {
            {
                add(team);
            }
        });
        when(team.getColoredName()).thenReturn("TeamRed");
        when(team.getWorms()).thenReturn(worms);

        ConsoleView view = spy(new ConsoleView(model));
        view.setModel(model);

        doAnswer(invocation -> {
            Worm w = invocation.getArgument(0);
            System.out.print(w.getName());
            return null;
        }).when(view).showWormInfo(any(Worm.class));

        when(worm1.getName()).thenReturn("Worm1");
        when(worm2.getName()).thenReturn("Worm2");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        view.showGameModelInfo();

        String output = out.toString();

        assertTrue(output.contains("Informations des équipes"));
        assertTrue(output.contains("TeamRed (2 worms)"));
        assertTrue(output.contains("Worm1"));
        assertTrue(output.contains("Worm2"));
    }

    @Test
    void testShowMapprintsMapWithWormsAndTerrainfixed() {
        GameModel model = mock(GameModel.class);
        Map map = mock(Map.class);

        char[][] grid = {
                { '~', '#', '.' },
                { '.', '.', '.' }
        };
        when(map.getHeight()).thenReturn(grid.length);
        when(map.getWidth()).thenReturn(grid[0].length);

        when(map.isWorm(anyInt(), anyInt())).thenReturn(false);

        map.grid = grid;

        when(model.getMap()).thenReturn(map);
        when(model.getTeams()).thenReturn(new ArrayList<>());

        ConsoleView view = new ConsoleView(model);
        view.setModel(model);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        view.showMap();

        String output = out.toString();

        assertTrue(output.contains(AnsiColor.getColoredString("BLUE", "~")));
        assertTrue(output.contains(AnsiColor.getColoredString("BROWN", "#")));
    }

    @Test
    void testGetTeamColorNameallCases() throws Exception {
        GameModel model = mock(GameModel.class);
        ConsoleView view = new ConsoleView(model);

        Method method = ConsoleView.class.getDeclaredMethod("getTeamColorName", Team.class);
        method.setAccessible(true);

        Team team0 = mock(Team.class);
        when(team0.getTeamId()).thenReturn(0);
        assertEquals("RED", method.invoke(view, team0));

        Team team1 = mock(Team.class);
        when(team1.getTeamId()).thenReturn(1);
        assertEquals("BLUE", method.invoke(view, team1));

        Team team2 = mock(Team.class);
        when(team2.getTeamId()).thenReturn(2);
        assertEquals("GREEN", method.invoke(view, team2));

        Team team3 = mock(Team.class);
        when(team3.getTeamId()).thenReturn(3);
        assertEquals("YELLOW", method.invoke(view, team3));

        Team teamOther = mock(Team.class);
        when(teamOther.getTeamId()).thenReturn(99);
        assertEquals("WHITE", method.invoke(view, teamOther));
    }

    @Test
    void testGetColoredTextreturnsAnsiColoredString() throws Exception {
        GameModel model = mock(GameModel.class);
        ConsoleView view = new ConsoleView(model);

        Method method = ConsoleView.class.getDeclaredMethod("getColoredText", String.class, String.class);
        method.setAccessible(true);

        String color = "RED";
        String text = "Hello";

        String result = (String) method.invoke(view, color, text);

        String expected = AnsiColor.getColoredString(color, text);

        assertEquals(expected, result);
    }

    @Test
    void testShowGameInitializationRecapprintsTeams() {
        GameModel model = mock(GameModel.class);
        ConsoleView view = new ConsoleView(model);

        Team team1 = mock(Team.class);
        Team team2 = mock(Team.class);

        when(team1.getName()).thenReturn("Alpha");
        when(team2.getName()).thenReturn("Beta");
        when(team1.getTeamId()).thenReturn(0);
        when(team2.getTeamId()).thenReturn(1);

        ArrayList<Team> teams = new ArrayList<>();
        teams.add(team1);
        teams.add(team2);

        when(model.getTeams()).thenReturn(teams);

        view.setModel(model);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        view.showGameInitializationRecap();

        String output = out.toString();

        assertTrue(output.contains("Les équipes sont prêtes"));
        assertTrue(output.contains("Alpha"));
        assertTrue(output.contains("Beta"));
        assertTrue(output.contains("VS"));
    }

}