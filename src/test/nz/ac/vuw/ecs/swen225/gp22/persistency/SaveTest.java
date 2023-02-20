package nz.ac.vuw.ecs.swen225.gp22.persistency;

import nz.ac.vuw.ecs.swen225.gp22.app.Base;
import nz.ac.vuw.ecs.swen225.gp22.domain.ColorableTile;
import nz.ac.vuw.ecs.swen225.gp22.domain.Maze;
import org.dom4j.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Tests for Save class.
 *
 * @author Gideon
 * @version 1.3
 */
public class SaveTest {
    /**
     * Test to see if the save method can save a level.
     */
    @Test
    public void saveGame() {
        Load.loadLevel(1);
        Save.saveGame();
        assert Load.previousGamePresent();
    }

    /**
     * Test to see if the save method can save level 2 correctly.
     */
    @Test
    public void saveLevel2() {
        Load.loadLevel(2);
        Save.saveGame();
        assert Load.previousGamePresent();
        Load.previousGame();
        assert Base.getLevel() == 2;
    }

    /**
     * Test to see if the save method can save the inventory of the player.
     */
    @Test
    public void saveInventory() {
        Maze.player.addKey(ColorableTile.Color.Red);
        Maze.player.addKey(ColorableTile.Color.Blue);
        Maze.player.addKey(ColorableTile.Color.Yellow);
        Document doc = DocumentHelper.createDocument();
        Element test = doc.addElement("test");
        Save.saveInventory(test);
        assert test.element("inventory") != null;
        assert test.element("inventory").elements("key").size() == 3;
        assert test.element("inventory").element("key").attributeValue("color").equals("Red");
    }

    /**
     * Test to see if the addPoint method adds the correct attributes to the element.
     */
    @Test
    public void addPoint() {
        Document doc = DocumentHelper.createDocument();
        Element test = doc.addElement("test");
        Save.addPoint(test, new Maze.Point(1, 2));
        assert test.attribute("x") != null;
        assert test.attribute("y") != null;
        assert test.attributeValue("x").equals("1");
        assert test.attributeValue("y").equals("2");
    }
}
