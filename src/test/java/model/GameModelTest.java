package model;

import model.physics.Projectile;
import model.physics.Wind;
import model.players.Team;
import model.players.Worm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class GameModelTest {

    private GameModel gameModel;
    private Map mockMap;
    private Team team1;
    private Team team2;
    private Worm worm1;
    private Worm worm2;
    private Worm worm3;
    private Worm worm4;

    @BeforeEach
    void setUp() {
        gameModel = new GameModel();
        mockMap = mock(Map.class);

        team1 = mock(Team.class);
        team2 = mock(Team.class);

        worm1 = mock(Worm.class);
        worm2 = mock(Worm.class);
        worm3 = mock(Worm.class);
        worm4 = mock(Worm.class);

        when(worm1.getName()).thenReturn("Worm1");
        when(worm2.getName()).thenReturn("Worm2");
        when(worm3.getName()).thenReturn("Worm3");
        when(worm4.getName()).thenReturn("Worm4");

        when(worm1.getHp()).thenReturn(100);
        when(worm2.getHp()).thenReturn(100);
        when(worm3.getHp()).thenReturn(100);
        when(worm4.getHp()).thenReturn(100);

        when(worm1.getX()).thenReturn(5.0);
        when(worm1.getY()).thenReturn(5.0);
        when(worm2.getX()).thenReturn(10.0);
        when(worm2.getY()).thenReturn(10.0);
        when(worm3.getX()).thenReturn(15.0);
        when(worm3.getY()).thenReturn(15.0);
        when(worm4.getX()).thenReturn(20.0);
        when(worm4.getY()).thenReturn(20.0);

        when(worm1.getTeam()).thenReturn(team1);
        when(worm2.getTeam()).thenReturn(team1);
        when(worm3.getTeam()).thenReturn(team2);
        when(worm4.getTeam()).thenReturn(team2);

        when(mockMap.getHeight()).thenReturn(100);
        when(mockMap.getWidth()).thenReturn(100);
    }

    @Test
    void testDefaultConstructor() {
        GameModel model = new GameModel();

        assertNotNull(model.getTeams());
        assertTrue(model.getTeams().isEmpty());
        assertNull(model.getMap());
        assertNull(model.getCurrentWorm());
        assertNull(model.getCurrentTeam());
        assertTrue(model.getFriendlyFire());
        assertTrue(model.isWindEnabled());
        assertNotNull(model.getProjectiles());
        assertTrue(model.getProjectiles().isEmpty());
        assertNotNull(model.getWind());
        assertNotNull(model.getCrateManager());
    }

    @Test
    void testParameterizedConstructor() {
        ArrayList<Team> teams = new ArrayList<>();
        ArrayList<Worm> worms1 = new ArrayList<>();
        worms1.add(worm1);
        when(team1.getWorms()).thenReturn(worms1);
        when(team1.containsWormAlive()).thenReturn(true);
        teams.add(team1);

        GameModel model = new GameModel(teams, mockMap, false);

        assertEquals(teams, model.getTeams());
        assertEquals(mockMap, model.getMap());
        assertFalse(model.getFriendlyFire());
        assertEquals(team1, model.getCurrentTeam());
        assertEquals(worm1, model.getCurrentWorm());
    }

    @Test
    void testParameterizedConstructorEmptyTeams() {
        ArrayList<Team> teams = new ArrayList<>();

        GameModel model = new GameModel(teams, mockMap, true);

        assertNull(model.getCurrentTeam());
        assertNull(model.getCurrentWorm());
    }

    @Test
    void testMapAccessors() {
        gameModel.setMap(mockMap);
        assertEquals(mockMap, gameModel.getMap());
    }

    @Test
    void testCurrentWormAccessors() {
        gameModel.setCurrentWorm(worm1);
        assertEquals(worm1, gameModel.getCurrentWorm());
    }

    @Test
    void testCurrentTeamAccessors() {
        gameModel.setCurrentTeam(team1);
        assertEquals(team1, gameModel.getCurrentTeam());
    }

    @Test
    void testTeamsAccessors() {
        ArrayList<Team> teams = new ArrayList<>();
        teams.add(team1);
        teams.add(team2);

        gameModel.setTeams(teams);
        assertEquals(teams, gameModel.getTeams());
        assertEquals(2, gameModel.getTeams().size());
    }

    @Test
    void testWindEnabledAccessors() {
        gameModel.setWindEnabled(false);
        assertFalse(gameModel.isWindEnabled());

        gameModel.setWindEnabled(true);
        assertTrue(gameModel.isWindEnabled());
    }

    @Test
    void testFriendlyFireGetter() {
        assertTrue(gameModel.getFriendlyFire());
    }

    @Test
    void testGetWinningTeamTeam1Wins() {
        ArrayList<Team> teams = new ArrayList<>();
        teams.add(team1);
        teams.add(team2);
        gameModel.setTeams(teams);

        when(team1.containsWormAlive()).thenReturn(true);
        when(team2.containsWormAlive()).thenReturn(false);

        assertEquals(team1, gameModel.getWinningTeam());
    }

    @Test
    void testGetWinningTeamTeam2Wins() {
        ArrayList<Team> teams = new ArrayList<>();
        teams.add(team1);
        teams.add(team2);
        gameModel.setTeams(teams);

        when(team1.containsWormAlive()).thenReturn(false);
        when(team2.containsWormAlive()).thenReturn(true);

        assertEquals(team2, gameModel.getWinningTeam());
    }

    @Test
    void testGetWinningTeamBothAlive() {
        ArrayList<Team> teams = new ArrayList<>();
        teams.add(team1);
        teams.add(team2);
        gameModel.setTeams(teams);

        when(team1.containsWormAlive()).thenReturn(true);
        when(team2.containsWormAlive()).thenReturn(true);

        assertNull(gameModel.getWinningTeam());
    }

    @Test
    void testGetWinningTeamEmptyTeams() {
        assertNull(gameModel.getWinningTeam());
    }

    @Test
    void testGetWinningTeamOnlyOneTeam() {
        ArrayList<Team> teams = new ArrayList<>();
        teams.add(team1);
        gameModel.setTeams(teams);

        assertNull(gameModel.getWinningTeam());
    }

    @Test
    void testGetWormByNameFound() {
        ArrayList<Team> teams = new ArrayList<>();
        ArrayList<Worm> worms1 = new ArrayList<>();
        worms1.add(worm1);
        worms1.add(worm2);
        when(team1.getWorms()).thenReturn(worms1);
        teams.add(team1);
        gameModel.setTeams(teams);

        assertEquals(worm1, gameModel.getWormByName("Worm1"));
        assertEquals(worm2, gameModel.getWormByName("Worm2"));
    }

    @Test
    void testGetWormByNameNotFound() {
        ArrayList<Team> teams = new ArrayList<>();
        ArrayList<Worm> worms1 = new ArrayList<>();
        worms1.add(worm1);
        when(team1.getWorms()).thenReturn(worms1);
        teams.add(team1);
        gameModel.setTeams(teams);

        assertNull(gameModel.getWormByName("NonExistent"));
    }

    @Test
    void testGetWormByNameNullNameInWorm() {
        Worm wormWithNullName = mock(Worm.class);
        when(wormWithNullName.getName()).thenReturn(null);

        ArrayList<Team> teams = new ArrayList<>();
        ArrayList<Worm> worms1 = new ArrayList<>();
        worms1.add(wormWithNullName);
        when(team1.getWorms()).thenReturn(worms1);
        teams.add(team1);
        gameModel.setTeams(teams);

        assertNull(gameModel.getWormByName("Worm1"));
    }

    @Test
    void testIsGameOverMultipleTeamsAlive() {
        ArrayList<Team> teams = new ArrayList<>();
        teams.add(team1);
        teams.add(team2);
        gameModel.setTeams(teams);

        when(team1.containsWormAlive()).thenReturn(true);
        when(team2.containsWormAlive()).thenReturn(true);

        assertFalse(gameModel.isGameOver());
    }

    @Test
    void testIsGameOverOneTeamAlive() {
        ArrayList<Team> teams = new ArrayList<>();
        teams.add(team1);
        teams.add(team2);
        gameModel.setTeams(teams);

        when(team1.containsWormAlive()).thenReturn(true);
        when(team2.containsWormAlive()).thenReturn(false);

        assertTrue(gameModel.isGameOver());
    }

    @Test
    void testIsGameOverNoTeamsAlive() {
        ArrayList<Team> teams = new ArrayList<>();
        teams.add(team1);
        teams.add(team2);
        gameModel.setTeams(teams);

        when(team1.containsWormAlive()).thenReturn(false);
        when(team2.containsWormAlive()).thenReturn(false);

        assertTrue(gameModel.isGameOver());
    }

    @Test
    void testGetProjectiles() {
        assertNotNull(gameModel.getProjectiles());
        assertTrue(gameModel.getProjectiles().isEmpty());
    }

    @Test
    void testGetWind() {
        assertNotNull(gameModel.getWind());
    }

    @Test
    void testGetCrateManager() {
        assertNotNull(gameModel.getCrateManager());
    }

    @Test
    void testInitWithValidData() {
        ArrayList<Team> teams = new ArrayList<>();
        ArrayList<Worm> worms1 = new ArrayList<>();
        worms1.add(worm1);
        when(team1.getWorms()).thenReturn(worms1);
        teams.add(team1);

        gameModel.init(teams, mockMap, false);

        assertEquals(teams, gameModel.getTeams());
        assertEquals(mockMap, gameModel.getMap());
        assertFalse(gameModel.getFriendlyFire());
        assertEquals(team1, gameModel.getCurrentTeam());
        assertEquals(worm1, gameModel.getCurrentWorm());
    }

    @Test
    void testInitWithEmptyTeams() {
        ArrayList<Team> teams = new ArrayList<>();

        gameModel.init(teams, mockMap, true);

        assertNull(gameModel.getCurrentTeam());
        assertNull(gameModel.getCurrentWorm());
    }

    @Test
    void testInitWithTeamHavingNoWorms() {
        ArrayList<Team> teams = new ArrayList<>();
        ArrayList<Worm> emptyWorms = new ArrayList<>();
        when(team1.getWorms()).thenReturn(emptyWorms);
        teams.add(team1);

        gameModel.init(teams, mockMap, true);

        assertEquals(team1, gameModel.getCurrentTeam());
        assertNull(gameModel.getCurrentWorm());
    }

    @Test
    void testUpdatereordersWormsCorrectly() {
        Worm worm1 = mock(Worm.class);
        Worm worm2 = mock(Worm.class);

        ArrayList<Worm> worms = new ArrayList<>();
        worms.add(worm1);
        worms.add(worm2);

        Team team1 = mock(Team.class);
        when(team1.getWorms()).thenReturn(worms);

        GameModel gameModel = new GameModel();
        gameModel.setCurrentTeam(team1);
        gameModel.setCurrentWorm(worm1);

        gameModel.update();

        assertEquals(worm2, team1.getWorms().get(0));
        assertEquals(worm1, team1.getWorms().get(1));

        assertEquals(2, team1.getWorms().size());
    }

    @Test
    void testNextTurnNormalFlow() {
        ArrayList<Team> teams = new ArrayList<>();
        ArrayList<Worm> worms1 = new ArrayList<>();
        ArrayList<Worm> worms2 = new ArrayList<>();
        Map map = new Map(20, 20);

        worms1.add(worm1);
        worms1.add(worm2);
        worms2.add(worm3);
        worms2.add(worm4);

        when(team1.getWorms()).thenReturn(worms1);
        when(team2.getWorms()).thenReturn(worms2);
        when(team1.containsWormAlive()).thenReturn(true);
        when(team2.containsWormAlive()).thenReturn(true);

        teams.add(team1);
        teams.add(team2);

        gameModel.setTeams(teams);
        gameModel.setCurrentTeam(team1);
        gameModel.setCurrentWorm(worm1);

        when(worm1.isDead()).thenReturn(false);
        when(worm2.isDead()).thenReturn(false);
        when(worm3.isDead()).thenReturn(false);
        when(worm4.isDead()).thenReturn(false);
        gameModel.setMap(map);

        gameModel.nextTurn();

        verify(worm1).setSelectedItem(null);
        assertEquals(team2, gameModel.getCurrentTeam());
        assertEquals(worm3, gameModel.getCurrentWorm());
    }

    @Test
    void testNextTurnGameOver() {
        ArrayList<Team> teams = new ArrayList<>();
        ArrayList<Worm> worms1 = new ArrayList<>();
        worms1.add(worm1);

        when(team1.getWorms()).thenReturn(worms1);
        when(team1.containsWormAlive()).thenReturn(true);

        teams.add(team1);

        gameModel.setTeams(teams);
        gameModel.setCurrentTeam(team1);
        gameModel.setCurrentWorm(worm1);

        gameModel.nextTurn();

        verify(worm1).setSelectedItem(null);
    }

    @Test
    void testNewTurnWindEnabled() {
        Wind mockWind = mock(Wind.class);
        mockWind.enabled = true;

        GameModel model = new GameModel();
        model.setWindEnabled(true);
        model.getWind().enabled = true;

        model.newTurnWind();

        assertNotNull(model.getWind());
    }

    @Test
    void testNewTurnWindDisabled() {
        gameModel.setWindEnabled(false);
        gameModel.getWind().enabled = false;

        gameModel.newTurnWind();

        assertNotNull(gameModel.getWind());
    }

    @Test
    void testDeleteWormExists() {
        Worm worm1 = mock(Worm.class);
        Worm worm2 = mock(Worm.class);

        ArrayList<Worm> worms1 = new ArrayList<>();
        worms1.add(worm1);
        worms1.add(worm2);

        Team team1 = mock(Team.class);
        when(team1.getWorms()).thenReturn(worms1);

        ArrayList<Team> teams = new ArrayList<>();
        teams.add(team1);

        GameModel gameModel = new GameModel();
        gameModel.setTeams(teams);

        gameModel.deleteWorm(worm1);

        assertFalse(worms1.contains(worm1));
        assertEquals(1, worms1.size());
        assertTrue(worms1.contains(worm2));
    }

    @Test
    void testDeleteWormNotExists() {
        Worm worm1 = mock(Worm.class);

        ArrayList<Worm> worms1 = new ArrayList<>();

        Team team1 = mock(Team.class);
        when(team1.getWorms()).thenReturn(worms1);

        ArrayList<Team> teams = new ArrayList<>();
        teams.add(team1);

        GameModel gameModel = new GameModel();
        gameModel.setTeams(teams);

        gameModel.deleteWorm(worm1);

        assertTrue(worms1.isEmpty());
    }

    @Test
    void testUpdateProjectilesInactiveProjectile() {
        Projectile mockProjectile = mock(Projectile.class);
        when(mockProjectile.isActive()).thenReturn(false);

        gameModel.getProjectiles().add(mockProjectile);
        gameModel.setMap(mockMap);

        gameModel.updateProjectiles(0.1);

        assertTrue(gameModel.getProjectiles().isEmpty());
    }

    @Test
    void testUpdateProjectilesBecomesInactive() {
        Projectile mockProjectile = mock(Projectile.class);
        Worm shooter = mock(Worm.class);

        when(mockProjectile.isActive()).thenReturn(true, false);
        when(mockProjectile.getShooter()).thenReturn(shooter);
        when(shooter.getX()).thenReturn(5.0);
        when(shooter.getY()).thenReturn(5.0);

        gameModel.getProjectiles().add(mockProjectile);
        gameModel.setMap(mockMap);

        gameModel.updateProjectiles(0.1);

        verify(mockProjectile).update(0.1, 5, 5);
        assertTrue(gameModel.getProjectiles().isEmpty());
    }

    @Test
    void testUpdateProjectilesWormCollisionFriendlyFire() {
        ArrayList<Team> teams = new ArrayList<>();
        ArrayList<Worm> worms1 = new ArrayList<>();
        worms1.add(worm1);
        worms1.add(worm2);

        when(team1.getWorms()).thenReturn(worms1);
        teams.add(team1);
        gameModel.setTeams(teams);
        gameModel.setMap(mockMap);

        Projectile mockProjectile = mock(Projectile.class);
        when(mockProjectile.isActive()).thenReturn(true);
        when(mockProjectile.getShooter()).thenReturn(worm1);
        when(mockProjectile.getMapX()).thenReturn((double) 10);
        when(mockProjectile.getMapY()).thenReturn((double) 10);
        when(mockProjectile.getDamage()).thenReturn(50);
        when(mockProjectile.getExplosionRadius()).thenReturn(2);
        when(mockProjectile.hasLeftShooterTile()).thenReturn(true);

        when(worm1.getX()).thenReturn(5.0);
        when(worm1.getY()).thenReturn(5.0);
        when(worm2.getX()).thenReturn(10.0);
        when(worm2.getY()).thenReturn(10.0);
        when(worm2.getHp()).thenReturn(100);

        when(worm1.getTeam()).thenReturn(team1);
        when(worm2.getTeam()).thenReturn(team1);

        gameModel.getProjectiles().add(mockProjectile);

        gameModel.updateProjectiles(0.1);

        verify(mockProjectile).update(anyDouble(), anyDouble(), anyDouble());
        verify(mockMap).createExplosion(10, 10, 2);
        assertTrue(gameModel.getProjectiles().isEmpty());
    }

    @Test
    void testUpdateProjectilesOnShooterTile() {
        Team team = new Team("Team1", 0, 0, gameModel);
        Worm shooter = new Worm(team, "shooter", 'a');
        team.getWorms().add(shooter);

        Projectile projectile = new Projectile(0, 0, 0, 0, 0, 0, 0, shooter) {
            @Override
            public boolean hasLeftShooterTile() {
                return false;
            }

            @Override
            public boolean shouldBeDestroyed(Map map) {
                return false;
            }
        };

        gameModel.getProjectiles().add(projectile);
        gameModel.setTeams(new ArrayList<>(List.of(team)));
        gameModel.setMap(mockMap);

        double hpBefore = shooter.getHp();
        gameModel.updateProjectiles(0.1);
        double hpAfter = shooter.getHp();

        assertEquals(hpBefore, hpAfter);

    }

    @Test
    void testUpdateProjectilesFriendlyFireDisabled() {
        ArrayList<Team> teams = new ArrayList<>();
        ArrayList<Worm> worms1 = new ArrayList<>();
        worms1.add(worm1);
        worms1.add(worm2);

        when(team1.getWorms()).thenReturn(worms1);
        teams.add(team1);

        gameModel.init(teams, mockMap, false);

        Projectile mockProjectile = mock(Projectile.class);
        when(mockProjectile.isActive()).thenReturn(true);
        when(mockProjectile.getShooter()).thenReturn(worm1);
        when(mockProjectile.getMapX()).thenReturn((double) 10);
        when(mockProjectile.getMapY()).thenReturn((double) 10);
        when(mockProjectile.hasLeftShooterTile()).thenReturn(true);
        when(mockProjectile.shouldBeDestroyed(mockMap)).thenReturn(false);

        when(worm1.getX()).thenReturn(5.0);
        when(worm1.getY()).thenReturn(5.0);
        when(worm2.getX()).thenReturn(10.0);
        when(worm2.getY()).thenReturn(10.0);

        when(worm1.getTeam()).thenReturn(team1);
        when(worm2.getTeam()).thenReturn(team1);

        gameModel.getProjectiles().add(mockProjectile);

        gameModel.updateProjectiles(0.1);

        verify(worm2, never()).setHp(anyInt());
    }

    @Test
    void testUpdateProjectilesTerrainCollisionGround() {
        gameModel.setMap(mockMap);

        Projectile mockProjectile = mock(Projectile.class);
        Worm shooter = mock(Worm.class);

        when(mockProjectile.isActive()).thenReturn(true);
        when(mockProjectile.getShooter()).thenReturn(shooter);
        when(mockProjectile.getMapX()).thenReturn((double) 15);
        when(mockProjectile.getMapY()).thenReturn((double) 15);
        when(mockProjectile.shouldBeDestroyed(mockMap)).thenReturn(true);
        when(mockProjectile.getExplosionRadius()).thenReturn(3);

        when(shooter.getX()).thenReturn(5.0);
        when(shooter.getY()).thenReturn(5.0);

        when(mockMap.isGround(15, 15)).thenReturn(true);

        gameModel.getProjectiles().add(mockProjectile);

        gameModel.updateProjectiles(0.1);

        verify(mockMap).createExplosion(15, 15, 3);
        assertTrue(gameModel.getProjectiles().isEmpty());
    }

    @Test
    void testUpdateProjectilesTerrainCollisionNotGround() {
        gameModel.setMap(mockMap);

        Projectile mockProjectile = mock(Projectile.class);
        Worm shooter = mock(Worm.class);

        when(mockProjectile.isActive()).thenReturn(true);
        when(mockProjectile.getShooter()).thenReturn(shooter);
        when(mockProjectile.getMapX()).thenReturn((double) 15);
        when(mockProjectile.getMapY()).thenReturn((double) 15);
        when(mockProjectile.shouldBeDestroyed(mockMap)).thenReturn(true);

        when(shooter.getX()).thenReturn(5.0);
        when(shooter.getY()).thenReturn(5.0);

        when(mockMap.isGround(15, 15)).thenReturn(false);

        gameModel.getProjectiles().add(mockProjectile);

        gameModel.updateProjectiles(0.1);

        verify(mockMap, never()).createExplosion(anyInt(), anyInt(), anyInt());
        assertTrue(gameModel.getProjectiles().isEmpty());
    }

    @Test
    void testUpdateTerrainAfterDestructionWormFalls() {
        ArrayList<Team> teams = new ArrayList<>();
        ArrayList<Worm> worms1 = new ArrayList<>();
        worms1.add(worm1);

        when(team1.getWorms()).thenReturn(worms1);
        teams.add(team1);
        gameModel.setTeams(teams);
        gameModel.setMap(mockMap);

        when(worm1.getX()).thenReturn(5.0);
        when(worm1.getY()).thenReturn(5.0);
        when(worm1.getHp()).thenReturn(100);

        when(mockMap.getHeight()).thenReturn(100);
        when(mockMap.isGround(5, 6)).thenReturn(true);
        when(mockMap.isWater(5, 6)).thenReturn(false);

        assertTrue(gameModel.updateTerrainAfterDestruction());
    }

    @Test
    void testUpdateTerrainAfterDestructionWormFallsInWater() {
        ArrayList<Team> teams = new ArrayList<>();
        ArrayList<Worm> worms1 = new ArrayList<>();
        worms1.add(worm1);

        when(team1.getWorms()).thenReturn(worms1);
        teams.add(team1);
        gameModel.setTeams(teams);
        gameModel.setMap(mockMap);

        when(worm1.getX()).thenReturn(5.0);
        when(worm1.getY()).thenReturn(5.0);

        when(mockMap.getHeight()).thenReturn(100);
        when(mockMap.isGround(5, 6)).thenReturn(false);
        when(mockMap.isWater(5, 6)).thenReturn(true);

        assertTrue(gameModel.updateTerrainAfterDestruction());
    }

    @Test
    void testUpdateTerrainAfterDestructionWormFallsOffMap() {
        ArrayList<Team> teams = new ArrayList<>();
        ArrayList<Worm> worms1 = new ArrayList<>();
        worms1.add(worm1);

        when(team1.getWorms()).thenReturn(worms1);
        teams.add(team1);
        gameModel.setTeams(teams);
        gameModel.setMap(mockMap);

        when(worm1.getX()).thenReturn(5.0);
        when(worm1.getY()).thenReturn(99.0);

        when(mockMap.getHeight()).thenReturn(100);

        assertTrue(gameModel.updateTerrainAfterDestruction());
    }

}