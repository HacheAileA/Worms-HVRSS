package model.items.tools;

import model.GameModel;
import model.Map;
import model.players.Worm;
import model.players.Team;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RandomTpTest {

    @Mock
    private GameModel mockModel;
    
    @Mock
    private Team mockTeam;
    
    @Mock
    private Worm mockWorm;
    
    @Mock
    private Map mockMap;
    
    private RandomTp randomTp;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockModel.getCurrentTeam()).thenReturn(mockTeam);
        when(mockModel.getMap()).thenReturn(mockMap);
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testConstructor() {
        randomTp = new RandomTp(mockModel);
        
        assertNotNull(randomTp);
        assertEquals("RandomTP", randomTp.getName());
        assertEquals(1, randomTp.getAmmo());
        verify(mockModel, times(1)).getCurrentTeam();
    }

    @Test
    void testConstructorStoresModel() {
        randomTp = new RandomTp(mockModel);
        
        assertNotNull(randomTp);
    }

    @Test
    void testUseToolWithNullUser() {
        randomTp = new RandomTp(mockModel);
        
        randomTp.useTool(null);
        
        assertTrue(outputStream.toString().contains("model est null"));
        assertEquals(1, randomTp.getAmmo()); 
    }

    @Test
    void testUseToolWhenModelIsNull() {
        randomTp = new RandomTp(mockModel);
        
        try {
            java.lang.reflect.Field modelField = RandomTp.class.getDeclaredField("model");
            modelField.setAccessible(true);
            modelField.set(randomTp, null);
        } catch (Exception e) {
            fail("Could not set model field");
        }
        
        randomTp.useTool(mockWorm);
        
        assertTrue(outputStream.toString().contains("model est null"));
        assertEquals(1, randomTp.getAmmo());
    }

    @Test
    void testUseToolWhenMapIsNull() {
        when(mockModel.getMap()).thenReturn(null);
        randomTp = new RandomTp(mockModel);
        
        randomTp.useTool(mockWorm);
        
        assertTrue(outputStream.toString().contains("map nulle"));
        assertEquals(1, randomTp.getAmmo());
    }

    @Test
    void testUseToolWhenAmmoIsNegative() {
        randomTp = new RandomTp(mockModel);
        
        try {
            java.lang.reflect.Field ammoField = Tools.class.getDeclaredField("ammo");
            ammoField.setAccessible(true);
            ammoField.setInt(randomTp, -1);
        } catch (Exception e) {
            fail("Could not set ammo field");
        }
        
        randomTp.useTool(mockWorm);
        
        assertTrue(outputStream.toString().contains("plus de munitions"));
    }

    @Test
    void testUseToolWithNoValidPositions() {
        randomTp = new RandomTp(mockModel);
        when(mockMap.getWidth()).thenReturn(5);
        when(mockMap.getHeight()).thenReturn(5);
        when(mockMap.isEmpty(anyInt(), anyInt())).thenReturn(false);
        
        randomTp.useTool(mockWorm);
        
        assertTrue(outputStream.toString().contains("RandomTp: aucune position valide trouvée"));
        assertEquals(1, randomTp.getAmmo());
    }

    @Test
    void testUseToolSuccessfulTeleport() {
        randomTp = new RandomTp(mockModel);
        when(mockMap.getWidth()).thenReturn(10);
        when(mockMap.getHeight()).thenReturn(10);
        
        when(mockMap.isEmpty(5, 5)).thenReturn(true);
        when(mockMap.isGround(5, 6)).thenReturn(true);
        when(mockMap.isEmpty(anyInt(), anyInt())).thenAnswer(invocation -> {
            int x = invocation.getArgument(0);
            int y = invocation.getArgument(1);
            return x == 5 && y == 5;
        });
        when(mockMap.isGround(anyInt(), anyInt())).thenAnswer(invocation -> {
            int x = invocation.getArgument(0);
            int y = invocation.getArgument(1);
            return x == 5 && y == 6;
        });
        
        when(mockWorm.getX()).thenReturn(3.0);
        when(mockWorm.getY()).thenReturn(3.0);
        when(mockWorm.getSymbol()).thenReturn('W');
        
        randomTp.useTool(mockWorm);
        
        verify(mockWorm, times(1)).setPosition(anyInt(), anyInt());
        verify(mockMap, times(1)).setCell(3, 3, ' ');
        verify(mockMap, times(1)).setCell(anyInt(), anyInt(), eq('W'));
        assertEquals(0, randomTp.getAmmo());
        assertTrue(outputStream.toString().contains("RandomTp pos="));
    }

    @Test
    void testUseToolWithWormAtBoundary() {
        randomTp = new RandomTp(mockModel);
        when(mockMap.getWidth()).thenReturn(10);
        when(mockMap.getHeight()).thenReturn(10);
        
        when(mockMap.isEmpty(5, 5)).thenReturn(true);
        when(mockMap.isGround(5, 6)).thenReturn(true);
        when(mockMap.isEmpty(anyInt(), anyInt())).thenAnswer(invocation -> {
            int x = invocation.getArgument(0);
            int y = invocation.getArgument(1);
            return x == 5 && y == 5;
        });
        when(mockMap.isGround(anyInt(), anyInt())).thenAnswer(invocation -> {
            int x = invocation.getArgument(0);
            int y = invocation.getArgument(1);
            return x == 5 && y == 6;
        });
        
        when(mockWorm.getX()).thenReturn(9.0);
        when(mockWorm.getY()).thenReturn(9.0);
        when(mockWorm.getSymbol()).thenReturn('W');
        
        randomTp.useTool(mockWorm);
        
        verify(mockMap, times(1)).setCell(9, 9, ' ');
        assertEquals(0, randomTp.getAmmo());
    }

    @Test
    void testUseToolWithWormOutsideBounds() {
        randomTp = new RandomTp(mockModel);
        when(mockMap.getWidth()).thenReturn(10);
        when(mockMap.getHeight()).thenReturn(10);
        
        when(mockMap.isEmpty(5, 5)).thenReturn(true);
        when(mockMap.isGround(5, 6)).thenReturn(true);
        when(mockMap.isEmpty(anyInt(), anyInt())).thenAnswer(invocation -> {
            int x = invocation.getArgument(0);
            int y = invocation.getArgument(1);
            return x == 5 && y == 5;
        });
        when(mockMap.isGround(anyInt(), anyInt())).thenAnswer(invocation -> {
            int x = invocation.getArgument(0);
            int y = invocation.getArgument(1);
            return x == 5 && y == 6;
        });
        
        when(mockWorm.getX()).thenReturn(-1.0);
        when(mockWorm.getY()).thenReturn(-1.0);
        when(mockWorm.getSymbol()).thenReturn('W');
        
        randomTp.useTool(mockWorm);
        
        verify(mockMap, never()).setCell(-1, -1, ' ');
        assertEquals(0, randomTp.getAmmo());
    }

    @Test
    void testUseToolOnlyConsidersEmptyPositions() {
        randomTp = new RandomTp(mockModel);
        when(mockMap.getWidth()).thenReturn(5);
        when(mockMap.getHeight()).thenReturn(5);
        
        when(mockMap.isEmpty(2, 2)).thenReturn(true);
        when(mockMap.isGround(2, 3)).thenReturn(true);
        when(mockMap.isEmpty(anyInt(), anyInt())).thenReturn(false);
        when(mockMap.isEmpty(2, 2)).thenReturn(true);
        when(mockMap.isGround(anyInt(), anyInt())).thenReturn(false);
        when(mockMap.isGround(2, 3)).thenReturn(true);
        
        when(mockWorm.getX()).thenReturn(0.0);
        when(mockWorm.getY()).thenReturn(0.0);
        when(mockWorm.getSymbol()).thenReturn('W');
        
        randomTp.useTool(mockWorm);
        
        verify(mockWorm, times(1)).setPosition(2, 2);
        assertEquals(0, randomTp.getAmmo());
    }

    @Test
    void testUseToolWithSmallMap() {
        randomTp = new RandomTp(mockModel);
        when(mockMap.getWidth()).thenReturn(1);
        when(mockMap.getHeight()).thenReturn(2);
        
        when(mockMap.isEmpty(0, 0)).thenReturn(true);
        when(mockMap.isGround(0, 1)).thenReturn(true);
        
        when(mockWorm.getX()).thenReturn(0.0);
        when(mockWorm.getY()).thenReturn(1.0);
        when(mockWorm.getSymbol()).thenReturn('W');
        
        randomTp.useTool(mockWorm);
        
        verify(mockWorm, times(1)).setPosition(0, 0);
        assertEquals(0, randomTp.getAmmo());
    }

    @Test
    void testConstructorWithNullModel() {
        assertThrows(NullPointerException.class, () -> {
            new RandomTp(null);
        });
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}