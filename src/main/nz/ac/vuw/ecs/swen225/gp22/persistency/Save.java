package nz.ac.vuw.ecs.swen225.gp22.persistency;

import nz.ac.vuw.ecs.swen225.gp22.app.Base;
import nz.ac.vuw.ecs.swen225.gp22.domain.*;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Used to save the current game.
 * Using xml files.
 *
 * @author Gideon
 * @version 1.4
 */
public class Save {
    /**
     * Save current game as xml.
     * Store information about the map, dimensions, number of treasures, nextLevel.
     * Store time and keysCollected.
     * Store player position, direction and inventory if present.
     * Store tilemap.
     **/
    public static void saveGame() {
        Document doc = DocumentHelper.createDocument();
        Element root = doc.addElement("maze");
        root.addAttribute("level", String.valueOf(Base.getLevel()));
        Element mapInfo = root.addElement("mapInfo");
        Maze.Point dimensions = Maze.getDimensions();
        mapInfo.addElement("width").addText(String.valueOf(dimensions.x()));
        mapInfo.addElement("height").addText(String.valueOf(dimensions.y()));
        mapInfo.addElement("treasures").addText(String.valueOf(Maze.getTreasuresLeft()));
        mapInfo.addElement("nextLevel").addText(String.valueOf(Maze.getNextLevel()));

        int keyCount = Maze.player.keyCount();
        Element saveInfo = root.addElement("saveInfo");
        saveInfo.addElement("time").addText(String.valueOf(Base.getTime()));
        saveInfo.addElement("keysCollected").addText(String.valueOf(keyCount));
        Element player = root.addElement("player");
        addPoint(player, Maze.player.getPos());
        player.addAttribute("direction", Maze.player.getDir().name());
        if (keyCount > 0) {
            saveInventory(player);
        }
        if (Maze.entities.size() > 0) {
            Element entities = root.addElement("entities");
            Maze.entities.forEach(e -> saveEntity(entities, e));
        }
        Element tiles = root.addElement("tiles");
        for (int x = 0; x < dimensions.x(); x++) {
            for (int y = 0; y < dimensions.y(); y++) {
                Maze.Point p = new Maze.Point(x, y);
                Tile tile = Maze.getTile(p);
                String tileID = TileDatabase.getID(tile);
                if (tileID.equals("ground")) continue;
                Element tileElement = tiles.addElement("tile");

                tileElement.addAttribute("ID", tileID);
                addPoint(tileElement, p);
                switch (tileID) {
                    case "info" -> tileElement.addElement("text").addText(((InfoField) tile).getText());
                    case "door", "key" ->
                            tileElement.addElement("color").addText(((ColorableTile) tile).getColor().name());
                    case "bounce-pad" ->
                            tileElement.addElement("direction").addText(((BouncyPad) tile).getDir().name());
                }
            }
        }

        String filename = System.getProperty("user.dir") + "/resources/saves/"
                + LocalDateTime.now().toString().replace(":", "-") + ".chaps.xml";
        try {
            FileWriter out = new FileWriter(filename, StandardCharsets.UTF_8);
            FileWriter previousOut = new FileWriter(System.getProperty("user.dir") + "/resources/saves/previousGame.xml", StandardCharsets.UTF_8);
            (new XMLWriter(out, OutputFormat.createPrettyPrint())).write(doc);
            (new XMLWriter(previousOut, OutputFormat.createPrettyPrint())).write(doc);
            out.close();
            previousOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save entities to xml element.
     *
     * @param entities Element to add the inventory to.
     * @param e        Entity to save
     */
    private static void saveEntity(Element entities, Entity e) {
        Element entity = entities.addElement("entity");
        entity.addAttribute("ID", e.getClass().getSimpleName());
        addPoint(entity, e.getPos());
        entity.addAttribute("direction", e.getDir().name());
    }

    /**
     * Save inventory to xml element.
     *
     * @param player Element to add the inventory to.
     */
    public static void saveInventory(Element player) {
        Element inventory = player.addElement("inventory");
        Map<String, Long> keyMap = Maze.player.getAllKeys().stream()
                .collect(Collectors.groupingBy(ColorableTile.Color::name, Collectors.counting()));
        keyMap.forEach(
                (k, v) -> inventory.addElement("key")
                        .addAttribute("count", String.valueOf(v))
                        .addAttribute("color", k));
    }

    /**
     * Add an x and y as attributes to an xml element.
     *
     * @param element Element to add the attributes to.
     * @param p       Point to get the x and y from.
     */
    public static void addPoint(Element element, Maze.Point p) {
        element.addAttribute("x", String.valueOf(p.x())).addAttribute("y", String.valueOf(p.y()));
    }

}
