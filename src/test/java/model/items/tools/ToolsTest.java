package model.items.tools;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.GameModel;

class ToolsTest {
    
    private GameModel model;
    private Tools tool;

    @BeforeEach
    void setUp(){
        model = new GameModel();
        tool = new HealthPack(10, model);
    }

    @Test
    void asAmmoTest(){
        assertTrue(tool.hasAmmo());

        tool.setAmmo(0);
        assertFalse(tool.hasAmmo());
    }

    @Test
    void toStringTest(){
        assertInstanceOf(String.class, tool.toString());
    }
}
