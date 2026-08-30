package model.persistence;

import model.GameModel;
import model.Map;
import model.items.Item;
import model.items.guns.Guns;
import model.items.tools.Tools;
import model.players.Team;
import model.players.Worm;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SaveManagerTest {

    @TempDir
    Path tempDir;

    private GameModel mockModel;
    private Team team1;
    private Team team2;
    private Map mockMap;
    private Worm worm1;
    private Worm worm2;

    @BeforeEach
    void setUp() throws Exception {
        setSaveDir(tempDir.toString() + "/");

        mockMap = mock(Map.class);
        when(mockMap.getWidth()).thenReturn(10);
        when(mockMap.getHeight()).thenReturn(5);
        when(mockMap.getMapType()).thenReturn("TEST_MAP");
        mockMap.grid = new char[5][10];
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 10; x++) {
                mockMap.grid[y][x] = (x == 0 || x == 9 || y == 0 || y == 4) ? '#' : ' ';
            }
        }

        mockModel = mock(GameModel.class);
        when(mockModel.getMap()).thenReturn(mockMap);
        when(mockModel.getFriendlyFire()).thenReturn(false);
        when(mockModel.isWindEnabled()).thenReturn(false);

        worm1 = new Worm("Worm1", 100, 'W', 0);
        worm1.setPosition(2, 2);
        worm2 = new Worm("Worm2", 80, 'V', 0);
        worm2.setPosition(5, 3);

        ArrayList<Worm> worms1 = new ArrayList<>();
        worms1.add(worm1);
        worms1.add(worm2);

        team1 = mock(Team.class);
        when(team1.getName()).thenReturn("Team Alpha");
        when(team1.getSymbol()).thenReturn('A');
        when(team1.getTeamId()).thenReturn(0);
        when(team1.isBot()).thenReturn(false);
        when(team1.getWorms()).thenReturn(worms1);

        ArrayList<Item> items = new ArrayList<>();
        when(team1.getInventory()).thenReturn(mock(model.items.Inventory.class));
        when(team1.getInventory().getAvailableItems(team1)).thenReturn(items);

        Worm worm3 = new Worm("Worm3", 90, 'X', 1);
        worm3.setPosition(7, 1);

        ArrayList<Worm> worms2 = new ArrayList<>();
        worms2.add(worm3);

        team2 = mock(Team.class);
        when(team2.getName()).thenReturn("Team Beta");
        when(team2.getSymbol()).thenReturn('B');
        when(team2.getTeamId()).thenReturn(1);
        when(team2.isBot()).thenReturn(true);
        when(team2.getWorms()).thenReturn(worms2);
        when(team2.getInventory()).thenReturn(mock(model.items.Inventory.class));
        when(team2.getInventory().getAvailableItems(team2)).thenReturn(new ArrayList<>());

        ArrayList<Team> teams = new ArrayList<>();
        teams.add(team1);
        teams.add(team2);
        when(mockModel.getTeams()).thenReturn(teams);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (tempDir != null && Files.exists(tempDir)) {
            Files.walk(tempDir)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                        }
                    });
        }
    }

    @Test
    void testConstructor() {
        assertNotNull(new LoadManager());
    }

    private void setSaveDir(String dir) throws Exception {
        java.lang.reflect.Field field = SaveManager.class.getDeclaredField("SAVE_DIR");
        field.setAccessible(true);

        field.set(null, dir);
    }

    @Test
    void testSaveCreatesFileWithCorrectContent() throws IOException {
        String filename = "test_save";
        SaveManager.save(mockModel, filename, team1);

        Path expectedPath = tempDir.resolve("test_save.txt");
        assertTrue(Files.exists(expectedPath), "Save file should exist");

        String content = Files.readString(expectedPath, StandardCharsets.UTF_8);
        assertAll(
                () -> assertTrue(content.contains("===== GAME SAVE ====="), "Should contain header"),
                () -> assertTrue(content.contains("=== TEAMS ORDER ==="), "Should contain teams section"),
                () -> assertTrue(content.contains("Team Alpha"), "Should contain team1 name"),
                () -> assertTrue(content.contains("Team Beta"), "Should contain team2 name"),
                () -> assertTrue(content.contains("Worm1"), "Should contain worm1 name"),
                () -> assertTrue(content.contains("=== MAP ==="), "Should contain map section"),
                () -> assertTrue(content.contains("===== END SAVE ====="), "Should contain footer"));
    }

    @Test
    void testSaveWithExtensionInFilename() throws IOException {
        String filename = "test_save.txt";
        SaveManager.save(mockModel, filename, team1);

        Path expectedPath = tempDir.resolve("test_save.txt");
        assertTrue(Files.exists(expectedPath), "Save file should exist");

        assertFalse(Files.exists(tempDir.resolve("test_save.txt.txt")), "Should not create double extension");
    }

    @Test
    void testSaveCreatesDirectory() throws Exception {
        Path newDir = tempDir.resolve("new_saves");
        setSaveDir(newDir.toString() + "/");

        assertFalse(Files.exists(newDir), "Directory should not exist initially");

        SaveManager.save(mockModel, "test", team1);

        assertTrue(Files.exists(newDir), "Directory should be created");
        assertTrue(Files.exists(newDir.resolve("test.txt")), "File should be created in new directory");
    }

    @Test
    void testSaveWithTeam2AsNext() throws IOException {
        SaveManager.save(mockModel, "reorder_test", team2);

        Path expectedPath = tempDir.resolve("reorder_test.txt");
        String content = Files.readString(expectedPath, StandardCharsets.UTF_8);

        int team1Index = content.indexOf("Team Alpha");
        int team2Index = content.indexOf("Team Beta");
        assertTrue(team2Index < team1Index, "Team Beta should appear before Team Alpha");
    }

    @Test
    void testSaveWithFriendlyFire() throws IOException {
        when(mockModel.getFriendlyFire()).thenReturn(true);

        SaveManager.save(mockModel, "ff_test", team1);

        Path expectedPath = tempDir.resolve("ff_test.txt");
        String content = Files.readString(expectedPath, StandardCharsets.UTF_8);
        assertTrue(content.contains("Friendly fire"), "Should contain friendly fire setting");
    }

    @Test
    void testSaveWithWindEnabled() throws IOException {
        when(mockModel.isWindEnabled()).thenReturn(true);

        SaveManager.save(mockModel, "wind_test", team1);

        Path expectedPath = tempDir.resolve("wind_test.txt");
        String content = Files.readString(expectedPath, StandardCharsets.UTF_8);
        assertTrue(content.contains("Wind Enabled"), "Should contain wind enabled setting");
    }

    @Test
    void testSaveWithBothSettings() throws IOException {
        when(mockModel.getFriendlyFire()).thenReturn(true);
        when(mockModel.isWindEnabled()).thenReturn(true);

        SaveManager.save(mockModel, "both_test", team1);

        Path expectedPath = tempDir.resolve("both_test.txt");
        String content = Files.readString(expectedPath, StandardCharsets.UTF_8);
        assertAll(
                () -> assertTrue(content.contains("Friendly fire"), "Should contain friendly fire"),
                () -> assertTrue(content.contains("Wind Enabled"), "Should contain wind enabled"));
    }

    @Test
    void testSaveWithGuns() throws IOException {
        Guns mockGun = mock(Guns.class);
        when(mockGun.getName()).thenReturn("Bazooka");
        when(mockGun.getAmmo()).thenReturn(5);

        ArrayList<Item> items = new ArrayList<>();
        items.add(mockGun);
        when(team1.getInventory().getAvailableItems(team1)).thenReturn(items);

        SaveManager.save(mockModel, "gun_test", team1);

        Path expectedPath = tempDir.resolve("gun_test.txt");
        String content = Files.readString(expectedPath, StandardCharsets.UTF_8);
        assertAll(
                () -> assertTrue(content.contains("- Guns:"), "Should contain guns section"),
                () -> assertTrue(content.contains("Bazooka"), "Should contain gun name"),
                () -> assertTrue(content.contains("Ammo = 5"), "Should contain ammo count"));
    }

    @Test
    void testSaveWithTools() throws IOException {
        Tools mockTool = mock(Tools.class);
        when(mockTool.getName()).thenReturn("Jetpack");
        when(mockTool.getAmmo()).thenReturn(3);

        ArrayList<Item> items = new ArrayList<>();
        items.add(mockTool);
        when(team1.getInventory().getAvailableItems(team1)).thenReturn(items);

        SaveManager.save(mockModel, "tool_test", team1);

        Path expectedPath = tempDir.resolve("tool_test.txt");
        String content = Files.readString(expectedPath, StandardCharsets.UTF_8);
        assertAll(
                () -> assertTrue(content.contains("- Tools:"), "Should contain tools section"),
                () -> assertTrue(content.contains("Jetpack"), "Should contain tool name"),
                () -> assertTrue(content.contains("Uses left = 3"), "Should contain uses count"));
    }

    @Test
    void testSaveWithGunsAndTools() throws IOException {
        Guns mockGun = mock(Guns.class);
        when(mockGun.getName()).thenReturn("Shotgun");
        when(mockGun.getAmmo()).thenReturn(10);

        Tools mockTool = mock(Tools.class);
        when(mockTool.getName()).thenReturn("Parachute");
        when(mockTool.getAmmo()).thenReturn(2);

        ArrayList<Item> items = new ArrayList<>();
        items.add(mockGun);
        items.add(mockTool);
        when(team1.getInventory().getAvailableItems(team1)).thenReturn(items);

        SaveManager.save(mockModel, "mixed_test", team1);

        Path expectedPath = tempDir.resolve("mixed_test.txt");
        String content = Files.readString(expectedPath, StandardCharsets.UTF_8);
        assertAll(
                () -> assertTrue(content.contains("Shotgun"), "Should contain gun"),
                () -> assertTrue(content.contains("Parachute"), "Should contain tool"),
                () -> assertTrue(content.contains("Ammo = 10"), "Should contain gun ammo"),
                () -> assertTrue(content.contains("Uses left = 2"), "Should contain tool uses"));
    }

    @Test
    void testSaveWithEmptyMapCells() throws IOException {
        mockMap.grid[2][5] = ' ';
        mockMap.grid[3][3] = 0;

        SaveManager.save(mockModel, "empty_test", team1);

        Path expectedPath = tempDir.resolve("empty_test.txt");
        String content = Files.readString(expectedPath, StandardCharsets.UTF_8);

        String[] lines = content.split("\n");
        boolean foundDots = false;
        for (String line : lines) {
            if (line.contains(".")) {
                foundDots = true;
                break;
            }
        }
        assertTrue(foundDots, "Empty cells should be represented as dots");
    }

    @Test
    void testSaveWithMultipleWorms() throws IOException {
        SaveManager.save(mockModel, "multi_worm_test", team1);

        Path expectedPath = tempDir.resolve("multi_worm_test.txt");
        String content = Files.readString(expectedPath, StandardCharsets.UTF_8);
        assertAll(
                () -> assertTrue(content.contains("Worm1"), "Should contain first worm"),
                () -> assertTrue(content.contains("Worm2"), "Should contain second worm"),
                () -> assertTrue(content.contains("HP = 100"), "Should contain worm1 HP"),
                () -> assertTrue(content.contains("HP = 80"), "Should contain worm2 HP"),
                () -> assertFalse(content.contains("Position = (2, 2)"), "Should not contain worm1 position"),
                () -> assertFalse(content.contains("Position = (5, 3)"), "Should not contain worm2 position"));
    }

    @Test
    void testSaveIOException() throws Exception {
        Path readOnlyDir = tempDir.resolve("readonly");
        Files.createDirectories(readOnlyDir);
        setSaveDir(readOnlyDir.toString() + "/test/deep/path/");

        File dir = readOnlyDir.toFile();
        dir.setReadOnly();

        assertDoesNotThrow(() -> SaveManager.save(mockModel, "fail_test", team1));

        dir.setWritable(true);
    }

    @Test
    void testSaveWithPromptSuccess() throws Exception {
        String input = "prompt_test\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        SaveManager.saveWithPrompt(mockModel, team1);

        assertTrue(Files.exists(tempDir.resolve("prompt_test.txt")), "File should be created");
        assertTrue(out.toString().contains("Sauvegarde effectuée"), "Should show success message");

        System.setIn(System.in);
        System.setOut(System.out);
    }

    @Test
    void testSaveWithPromptOverwriteYes() throws Exception {
        Files.writeString(tempDir.resolve("existing.txt"), "old content");

        String input = "existing\noui\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        SaveManager.saveWithPrompt(mockModel, team1);

        String newContent = Files.readString(tempDir.resolve("existing.txt"));
        assertTrue(newContent.contains("===== GAME SAVE ====="), "File should be overwritten");
        assertTrue(out.toString().contains("existe déjà"), "Should ask for confirmation");

        System.setIn(System.in);
        System.setOut(System.out);
    }

    @Test
    void testSaveWithPromptOverwriteNo() throws Exception {
        Files.writeString(tempDir.resolve("existing.txt"), "old content");

        String input = "existing\nnon\nnew_file\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        SaveManager.saveWithPrompt(mockModel, team1);

        assertTrue(Files.exists(tempDir.resolve("new_file.txt")), "New file should be created");
        String oldContent = Files.readString(tempDir.resolve("existing.txt"));
        assertEquals("old content", oldContent, "Old file should not be modified");

        System.setIn(System.in);
        System.setOut(System.out);
    }

    @Test
    void testSaveWithPromptOverwriteShortYes() throws Exception {
        Files.writeString(tempDir.resolve("existing.txt"), "old content");

        String input = "existing\no\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        SaveManager.saveWithPrompt(mockModel, team1);

        String newContent = Files.readString(tempDir.resolve("existing.txt"));
        assertTrue(newContent.contains("===== GAME SAVE ====="), "File should be overwritten with 'o'");

        System.setIn(System.in);
    }

    @Test
    void testSaveWithSingleTeam() throws IOException {
        ArrayList<Team> singleTeam = new ArrayList<>();
        singleTeam.add(team1);
        when(mockModel.getTeams()).thenReturn(singleTeam);

        SaveManager.save(mockModel, "single_team", team1);

        Path expectedPath = tempDir.resolve("single_team.txt");
        assertTrue(Files.exists(expectedPath));
        String content = Files.readString(expectedPath);
        assertTrue(content.contains("Team Alpha"));
    }

    @Test
    void testSaveWithEmptyInventory() throws IOException {
        when(team1.getInventory().getAvailableItems(team1)).thenReturn(new ArrayList<>());

        SaveManager.save(mockModel, "empty_inv", team1);

        Path expectedPath = tempDir.resolve("empty_inv.txt");
        String content = Files.readString(expectedPath);
        assertTrue(content.contains("- Guns:"));
        assertTrue(content.contains("- Tools:"));
    }

    @Test
    void testSavePreservesWormSymbols() throws IOException {
        SaveManager.save(mockModel, "symbols_test", team1);

        Path expectedPath = tempDir.resolve("symbols_test.txt");
        String content = Files.readString(expectedPath);
        assertAll(
                () -> assertTrue(content.contains("Symbol = W"), "Should preserve worm1 symbol"),
                () -> assertTrue(content.contains("Symbol = V"), "Should preserve worm2 symbol"));
    }

    @Test
    void testSavePreservesTeamIds() throws IOException {
        SaveManager.save(mockModel, "teamid_test", team1);

        Path expectedPath = tempDir.resolve("teamid_test.txt");
        String content = Files.readString(expectedPath);
        assertAll(
                () -> assertTrue(content.contains("TeamId = 0"), "Should preserve team1 ID"),
                () -> assertTrue(content.contains("TeamId = 1"), "Should preserve team2 ID"));
    }

    @Test
    void testSavePreservesBotStatus() throws IOException {
        SaveManager.save(mockModel, "bot_test", team1);

        Path expectedPath = tempDir.resolve("bot_test.txt");
        String content = Files.readString(expectedPath);
        assertAll(
                () -> assertTrue(content.contains("isBot = false"), "Should show team1 not bot"),
                () -> assertTrue(content.contains("isBot = true"), "Should show team2 is bot"));
    }
}