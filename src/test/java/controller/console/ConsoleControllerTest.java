package controller.console;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import org.mockito.MockedStatic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.GameModel;
import model.items.Inventory;
import model.persistence.LoadManager;
import model.persistence.SaveManager;
import model.players.Team;
import model.players.Worm;
import controller.GameInitializationService;
import view.console.ConsoleView;

class ConsoleControllerTest {

    private GameModel model;
    private ConsoleView view;
    private ConsoleController controller;
    private Team team;
    private Worm worm;

    private void provideInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
    }

    @BeforeEach
    void setup() {
        model = mock(GameModel.class);
        view = mock(ConsoleView.class);
        controller = new ConsoleController(model, view);

        worm = mock(Worm.class);
        team = mock(Team.class);

        ArrayList<Worm> worms = new ArrayList<>();
        worms.add(worm);
        when(team.getWorms()).thenReturn(worms);

        ArrayList<Team> teams = new ArrayList<>();
        teams.add(team);
        when(model.getTeams()).thenReturn(teams);

        when(model.getCurrentWorm()).thenReturn(worm);
        when(model.getCurrentTeam()).thenReturn(team);
        when(model.isGameOver()).thenReturn(true);
    }

    @Test
    void testConstructorIsValid() {
        assertEquals(model, controller.model);
        assertEquals(view, controller.view);
    }

    @Test
    void testSetDevMode() {
        controller.setDevMode(true);
        assertEquals(controller.devMode, true);

        controller.setDevMode(false);
        assertEquals(controller.devMode, false);
    }

    @Test
    void testStartApplicationLoadGame() throws InterruptedException {
        testStartApplicationLoadGameTrue();
    }

    @Test
    void testStartApplicationLoadGameTrue() throws InterruptedException {
        provideInput("1\n");

        GameModel loadedModel = mock(GameModel.class);
        LoadManager.LoadResult mockResult = new LoadManager.LoadResult(loadedModel, null);

        try (MockedStatic<LoadManager> mockedLoadManager = mockStatic(LoadManager.class)) {
            mockedLoadManager.when(LoadManager::load).thenReturn(mockResult);

            controller = new ConsoleController(model, view) {
                @Override
                public void run() {
                }
            };

            controller.startApplication();

            assertEquals(loadedModel, controller.model);
            verify(view).setModel(loadedModel);
        }
    }

    @Test
    void testStartApplicationNewGame() throws InterruptedException {
        provideInput("2\n");

        GameInitializationService mockService = mock(GameInitializationService.class);
        GameModel newModel = mock(GameModel.class);
        when(mockService.initializeNewGameConsole()).thenReturn(newModel);

        final boolean run[] = { false };

        controller = new ConsoleController(model, view) {
            @Override
            protected GameInitializationService createGameInitializationService() {
                return mockService;
            }

            @Override
            public void run() {
                run[0] = true;
            }
        };

        controller.startApplication();

        verify(view, atLeastOnce()).showMainMenu();
        verify(view).setModel(newModel);
        verify(view).showGameInitializationRecap();
        assertEquals(true, run[0], "La méthode run() doit être appelée après l'initialisation.");
    }

    @Test
    void testStartNewGame() throws InterruptedException {
        GameInitializationService mockService = mock(GameInitializationService.class);
        GameModel newModel = mock(GameModel.class);
        when(mockService.initializeNewGameConsole()).thenReturn(newModel);

        controller = new ConsoleController(model, view) {
            @Override
            protected GameInitializationService createGameInitializationService() {
                return mockService;
            }
        };

        controller.startNewGame();

        assertEquals(newModel, controller.model);
        verify(view).setModel(newModel);
        verify(view).showGameInitializationRecap();
    }

    @Test
    void testCreateGameInitializationService() {
        GameInitializationService service = controller.createGameInitializationService();
        assertNotNull(service, "La méthode doit retourner une instance non nulle");
        assertTrue(service instanceof GameInitializationService, "Doit retourner un GameInitializationService");
    }

    @Test
    void testRun() {
    }

    @Test
    void testActionChoices() {
        provideInput("1\n");
        setup();

        int res1 = controller.actionChoices();
        assertEquals(1, res1);
        verify(view).showActionsMenu();

        clearInvocations(view);

        provideInput("retour\n");
        setup();

        int res2 = controller.actionChoices();
        assertEquals(-1, res2);
        verify(view).showActionsMenu();
    }

    @Test
    void testGunsChoices() {
        provideInput("1\n");
        setup();

        int res1 = controller.gunsChoices();
        assertEquals(1, res1);
        verify(view).showInventory();

        clearInvocations(view);

        provideInput("retour\n");
        setup();

        int res2 = controller.gunsChoices();
        assertEquals(-1, res2);
        verify(view).showInventory();
    }

    @Test
    void testRunturnEndsImmediately() throws InterruptedException {
        Worm worm = mock(Worm.class);
        Team team = mock(Team.class);
        Inventory inventory = mock(Inventory.class);

        when(team.getWorms()).thenReturn(new ArrayList<>() {
            {
                add(worm);
            }
        });
        when(team.getInventory()).thenReturn(inventory);

        when(model.getTeams()).thenReturn(new ArrayList<>() {
            {
                add(team);
            }
        });
        when(model.isGameOver()).thenReturn(false, true);
        when(model.getCurrentWorm()).thenReturn(worm);
        when(model.getCurrentTeam()).thenReturn(team);

        ConsoleController spyController = spy(new ConsoleController(model, view));
        doReturn(4).when(spyController).actionChoices();

        spyController.run();

        verify(view).showGameModelInfo();
        verify(view).showMap();
        verify(view).showTeamInfo();
        verify(model, atLeastOnce()).isGameOver();
    }

    @Test
    void testRuncase5saveGame() throws InterruptedException {
        try (MockedStatic<InputValidator> inputMock = mockStatic(InputValidator.class);
                MockedStatic<SaveManager> saveMock = mockStatic(SaveManager.class)) {

            inputMock.when(() -> InputValidator.checkIsInt(any(), anyString())).thenReturn(5);

            Worm mockWorm = mock(Worm.class);
            Team mockTeam = mock(Team.class);
            when(mockTeam.getWorms()).thenReturn(new ArrayList<>() {
                {
                    add(mockWorm);
                }
            });
            when(mockTeam.getInventory()).thenReturn(mock(Inventory.class));
            when(model.getTeams()).thenReturn(new ArrayList<>() {
                {
                    add(mockTeam);
                }
            });
            when(model.getCurrentWorm()).thenReturn(mockWorm);
            when(model.getCurrentTeam()).thenReturn(mockTeam);

            when(model.isGameOver()).thenReturn(false);

            controller.run();

            saveMock.verify(() -> SaveManager.saveWithPrompt(any(), any()), times(1));
        }
    }

    @Test
    void testRundefaultCase() throws InterruptedException {
        try (MockedStatic<InputValidator> inputMock = mockStatic(InputValidator.class)) {
            controller.setDevMode(true);

            inputMock.when(() -> InputValidator.checkIsInt(any(), anyString()))
                    .thenReturn(99, 4);

            when(model.isGameOver()).thenReturn(false, true);

            controller.run();

            verify(view).showIncorrectChoiceMessage();
        }
    }

}
