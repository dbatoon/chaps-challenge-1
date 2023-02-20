package nz.ac.vuw.ecs.swen225.gp22.persistency;

import nz.ac.vuw.ecs.swen225.gp22.app.Base;
import nz.ac.vuw.ecs.swen225.gp22.domain.Maze;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Tests for Load class.
 *
 * @author Gideon
 * @version 1.4
 */
public class LoadTest {
    /**
     * Test to see if the load method can load a level correctly.
     */
    @Test
    public void loadLevel() {
        Load.loadLevel(1);
        assert Base.getTime() == 60;
        assert Base.getLevel() == 1;
        assert Maze.player.getPos().equals(new Maze.Point(8, 7)) : "Player position is not correct";
        assert Maze.getDimensions().equals(new Maze.Point(17, 16)) : "Maze dimensions are not correct";
    }

    /**
     * Test if the getFile method loads the file with the correct name.
     */
    @Test
    public void getFile() {
        File f = Load.getFile("levels/level1");
        assert f != null;
        assert f.getName().equals("level1.xml");
    }

    /**
     * Test if the previousGamePresent method returns true when a previous game is present.
     */
    @Test
    public void checkPreviousGamePresent() {
        Load.loadLevel(1);
        Save.saveGame();
        assert Load.previousGamePresent();
    }

    /**
     * Test if the previousGamePresent method returns false when a previous game is not present.
     */
    @Test
    public void checkPreviousGameNotPresent() {
        deletePreviousGame();
        assert Load.previousGamePresent() == false;
    }

    /**
     * Test if the previousGame method loads the correct level.
     */
    @Test
    public void loadPreviousGame() {
        createPreviousGame(1);
        Load.previousGame();
        assert Base.getLevel() == 1;
        assert Maze.entities.size() == 0;
    }

    /**
     * Test if the previousGame method loads level 1 when no previous game is present.
     */
    @Test
    public void loadPreviousGameNotPresent() {
        deletePreviousGame();
        Load.previousGame();
        assert Base.getTime() == 60 : "Time is not correct";
        assert Base.getLevel() == 1 : "Level is not correct";
        assert Maze.player.getPos().equals(new Maze.Point(8, 7)) : "Player position is not correct";
        assert Maze.getDimensions().equals(new Maze.Point(17, 16)) : "Maze dimensions are not correct";
    }

    /**
     * Is previousGameInfo correct.
     */
    @Test
    public void getPreviousGameInfo() {
        createPreviousGame(1);
        String info = Load.previousGameInfo();
        assert info.equals("Time: 60, Keys: 0") : "Previous game info is not correct";
        assert Base.getLevel() == 1 : "Level is not correct";
    }

    /**
     * previousGameInfo is default when no previous game is present.
     */
    @Test
    public void getPreviousGameInfoNotPresent() {
        deletePreviousGame();
        String info = Load.previousGameInfo();
        assert info.equals("Time: 0, Keys: 0") : "Previous game info is not correct";
    }

    /**
     * Test if the loadJar method creates a classloader.
     */
    @Test
    public void loadJar() {
        Load.loadLevel(2);
        assert Load.getClassLoader() != null;
    }

    /**
     * Create a previous game file for tests.
     *
     * @param level level to create
     */
    private void createPreviousGame(int level) {
        if (!Load.previousGamePresent()) {
            Load.loadLevel(level);
            Save.saveGame();
        }
    }

    /**
     * Delete the previousGame file used for tests.
     */
    private void deletePreviousGame() {
        try {
            Files.deleteIfExists(Load.getFile("saves/previousGame").toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
