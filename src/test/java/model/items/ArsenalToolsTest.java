package model.items;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.GameModel;
import model.items.tools.Tools;
import model.players.Team;
import model.players.Worm;

import static org.junit.jupiter.api.Assertions.*;

class ArsenalToolsTest {

    private GameModel model;
    private ArsenalTools arsenalTools;
    private Team team2;

    @BeforeEach
    void setUp() {
        model = new GameModel();
        arsenalTools = new ArsenalTools(model);
        team2 = new Team("b", 0, 1, model);
    }

    @Test
    void testConstructor() {
        assertFalse(arsenalTools.getAvailableTools().isEmpty(),
                "La liste des outils ne doit pas être vide après la création");
        for (Tools tool : arsenalTools.getAvailableTools()) {
            assertTrue(tool instanceof Tools, "Chaque élément doit être une instance de Tools");
        }
    }

    @Test
    void testGetAvailableTools() {
        assertNotNull(arsenalTools.getAvailableTools(), "La méthode getAvailableTools ne doit pas retourner null");
        assertEquals(arsenalTools.getAvailableTools(), arsenalTools.getAvailableTools(),
                "La méthode getAvailableTools doit retourner la même liste à chaque appel");
    }

    @Test
    void testRemoveToolNoAmmo() {
        String nameBefore = arsenalTools.getAvailableTools().get(0).getName();
        int sizeBefore = arsenalTools.getAvailableTools().size();
        arsenalTools.getAvailableTools().get(0).setAmmo(0);
        arsenalTools.removeToolNoAmmo();

        assertEquals(sizeBefore - 1, arsenalTools.getAvailableTools().size(),
                "La liste des outils doit être vide après suppression de l'outil sans munitions");
        for (Tools tool : arsenalTools.getAvailableTools()) {
            assertNotEquals(nameBefore, tool.getName(), "L'outil sans munitions doit être supprimé de la liste");
            assertTrue(tool.hasAmmo(), "Tous les outils restants doivent avoir des munitions");
        }
    }

    @Test
    void testReloadTool() {
        Tools tool = arsenalTools.getAvailableTools().get(0);
        tool.setAmmo(0);
        arsenalTools.reloadTool(tool);
        assertEquals(tool.getMaxAmmo(), tool.getAmmo(), "L'outil doit être rechargé à son maximum de munitions");
    }

    @Test
    void testReloadToolNull() {
        arsenalTools.reloadTool(null); 
    }

    @Test
    void testReloadAllTools() {
        for (Tools tool : arsenalTools.getAvailableTools()) {
            tool.setAmmo(0);
        }
        arsenalTools.reloadAllTools();
        for (Tools tool : arsenalTools.getAvailableTools()) {
            assertEquals(tool.getMaxAmmo(), tool.getAmmo(),
                    "Tous les outils doivent être rechargés à leur maximum de munitions");
        }
    }

    @Test
    void testClear() {
        arsenalTools.clear();
        assertTrue(arsenalTools.getAvailableTools().isEmpty(), "Après clear, la liste des outils doit être vide");
    }

    @Test
    void testAddToolGeneralCaseFalseBranch() {
        ArsenalTools toolsArsenal = new ArsenalTools(model);
        toolsArsenal.clear();

        Tools tool1 = new Tools("A", 1, model.getCurrentTeam()) {
            @Override
            public void useTool(Worm worm) {
            }
        };
        toolsArsenal.addTool(tool1);
        assertEquals(1, toolsArsenal.getAvailableTools().size());

        Tools tool2 = new Tools("B", 1, team2) {
            @Override
            public void useTool(Worm worm) {
            }
        };
        toolsArsenal.addTool(tool2);

        assertEquals(2, toolsArsenal.getAvailableTools().size(),
                "La liste doit contenir deux outils car les noms ne correspondent pas");
    }
}
