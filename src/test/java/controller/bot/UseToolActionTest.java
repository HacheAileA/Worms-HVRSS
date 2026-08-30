package controller.bot;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

import model.GameModel;
import model.items.tools.AirStrike;
import model.items.tools.HealthPack;
import model.items.tools.Tools;
import model.players.Team;
import model.players.Worm;

class UseToolActionTest {

    @Test
    void testConstructorSimple() throws Exception {
        Team team = new Team("a", 0, 1, new GameModel());
        Worm worm = new Worm(team, "a", 'a');

        Tools tool = new HealthPack(team);

        UseToolAction action = new UseToolAction(tool, worm);

        assertNotNull(action);

        Field toolField = UseToolAction.class.getDeclaredField("tool");
        toolField.setAccessible(true);
        assertEquals(tool, toolField.get(action));

        Field userField = UseToolAction.class.getDeclaredField("user");
        userField.setAccessible(true);
        assertEquals(worm, userField.get(action));

        Field targetXField = UseToolAction.class.getDeclaredField("targetX");
        targetXField.setAccessible(true);
        assertNull(targetXField.get(action));
    }

    @Test
    void testConstructorWithTarget() throws Exception {
        GameModel model = new GameModel();
        Team team = new Team("TeamA", 0, 1, model);
        Worm worm = new Worm(team, "Wormy", 'W');

        Tools tool = new AirStrike(model);

        int targetX = 42;

        UseToolAction action = new UseToolAction(tool, worm, targetX);

        Field toolField = UseToolAction.class.getDeclaredField("tool");
        toolField.setAccessible(true);
        assertEquals(tool, toolField.get(action));

        Field userField = UseToolAction.class.getDeclaredField("user");
        userField.setAccessible(true);
        assertEquals(worm, userField.get(action));

        Field targetXField = UseToolAction.class.getDeclaredField("targetX");
        targetXField.setAccessible(true);
        assertEquals(targetX, targetXField.get(action));
    }

    @Test
    @SuppressWarnings("unused")
    void testExecuteRegularTool() {
        GameModel model = new GameModel();
        Team team = new Team("TeamA", 0, 1, model);
        Worm worm = new Worm(team, "Wormy", 'W');

        Tools tool = new HealthPack(team) {
            boolean used = false;
            Worm calledUser = null;

            @Override
            public void useTool(Worm user) {
                used = true;
                calledUser = user;
            }
        };

        UseToolAction action = new UseToolAction(tool, worm);

        action.execute(model);

        try {
            Field usedField = tool.getClass().getDeclaredField("used");
            usedField.setAccessible(true);
            assertTrue((boolean) usedField.get(tool), "L'outil aurait dû être utilisé");

            Field userField = tool.getClass().getDeclaredField("calledUser");
            userField.setAccessible(true);
            assertEquals(worm, userField.get(tool), "Le worm utilisé doit être le bon");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
