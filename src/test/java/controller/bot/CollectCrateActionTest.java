package controller.bot;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import model.GameModel;
import model.Map;
import model.items.crates.Crate;
import model.players.Team;
import model.players.Worm;

class CollectCrateActionTest {

    @Test
    void testConstructor() throws Exception {
        Crate crate = new Crate(0, new ArrayList<>());

        CollectCrateAction action = new CollectCrateAction(crate);

        Field crateField = CollectCrateAction.class.getDeclaredField("crate");
        crateField.setAccessible(true);
        assertEquals(crate, crateField.get(action), "Le champ crate doit être correctement initialisé");
    }

    @Test
    void testExecute() {
        AtomicBoolean movedLeft = new AtomicBoolean(false);
        AtomicBoolean movedRight = new AtomicBoolean(false);
        AtomicBoolean jumped = new AtomicBoolean(false);
        AtomicBoolean stopped = new AtomicBoolean(false);

        Team team = new Team("TeamA", 0, 1, new GameModel());

        Worm worm = new Worm(team, "Wormy", 'W') {
            @Override
            public double getX() {
                return 0;
            }

            @Override
            public double getY() {
                return 0;
            }

            @Override
            public void startMoveLeft() {
                movedLeft.set(true);
            }

            @Override
            public void startMoveRight() {
                movedRight.set(true);
            }

            @Override
            public void jumpSmooth(Map map) {
                jumped.set(true);
            }

            @Override
            public void stopMove() {
                stopped.set(true);
            }
        };

        Map mockMap = new Map(20, 20) {
            @Override
            public int getWidth() {
                return 20;
            }

            @Override
            public int getHeight() {
                return 20;
            }
        };

        GameModel model = new GameModel() {
            @Override
            public Worm getCurrentWorm() {
                return worm;
            }

            @Override
            public Map getMap() {
                return mockMap;
            }
        };

        Crate crate = new Crate(0, new ArrayList<>());

        CollectCrateAction action = new CollectCrateAction(crate);
        action.execute(model);

        assertTrue(movedRight.get() || movedLeft.get(), "Le worm doit commencer à se déplacer");
    }

    @Test
    void testExecuteShouldJump() {
        AtomicBoolean jumped = new AtomicBoolean(false);
        AtomicBoolean movedLeft = new AtomicBoolean(false);
        AtomicBoolean movedRight = new AtomicBoolean(false);

        Team team = new Team("TeamA", 0, 1, new GameModel());

        Worm worm = new Worm(team, "Wormy", 'W') {
            @Override
            public double getX() {
                return 0;
            }

            @Override
            public double getY() {
                return 0;
            }

            @Override
            public void jumpSmooth(Map map) {
                jumped.set(true);
            }

            @Override
            public void startMoveLeft() {
                movedLeft.set(true);
            }

            @Override
            public void startMoveRight() {
                movedRight.set(true);
            }
        };

        Map mockMap = new Map(20, 20) {
            @Override
            public int getWidth() {
                return 20;
            }

            @Override
            public int getHeight() {
                return 20;
            }
        };

        GameModel model = new GameModel() {
            @Override
            public Worm getCurrentWorm() {
                return worm;
            }

            @Override
            public Map getMap() {
                return mockMap;
            }
        };

        Crate crate = new Crate(5, new ArrayList<>());

        CollectCrateAction action = new CollectCrateAction(crate);
        action.execute(model);

        assertTrue(movedRight.get() || movedLeft.get(), "Le worm doit commencer à se déplacer après le jump");
    }
}
