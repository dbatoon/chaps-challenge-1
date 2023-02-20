package nz.ac.vuw.ecs.swen225.gp22.persistency;

import nz.ac.vuw.ecs.swen225.gp22.domain.ColorableTile;
import nz.ac.vuw.ecs.swen225.gp22.domain.Maze;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * Tests for parser class.
 *
 * @author Gideon
 * @version 1.3
 */
public class ParserTest {
    /**
     * Test to see if the parser can parse the players inventory correctly.
     */
    @Test
    public void parseInventory() {
        Load.loadLevel(1);
        Maze.player.addKey(ColorableTile.Color.Red);
        Maze.player.addKey(ColorableTile.Color.Yellow);
        Save.saveGame();
        Maze.player.consumeKey(ColorableTile.Color.Red);
        Maze.player.consumeKey(ColorableTile.Color.Yellow);
        Parser parser = new Parser(Load.getFile("saves/previousGame"));
        parser.parsePlayer(Maze.player);
        assert Maze.player.keyCount() == 2 : "Player has incorrect number of keys is:" + Maze.player.keyCount() + "should be 2";
        assert Maze.player.hasKey(ColorableTile.Color.Red) : "Player has no red key";
        assert Maze.player.hasKey(ColorableTile.Color.Yellow) : "Player has no yellow key";
    }

    /**
     * Test to see if the parser throws error when the file is invalid.
     */
    @Test
    public void parseInvalidFile() {
        try {
            new Parser(new File("src/test/nz/ac/vuw/ecs/swen225/gp22/persistency/invalidFile"));
            assert false : "Parser should throw an exception when given an invalid file";
        } catch (IllegalArgumentException e) {
            assert true;
        }
    }

}
