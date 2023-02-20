package nz.ac.vuw.ecs.swen225.gp22.recorder;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * The recorder for the game. Used to record actions.
 *
 * @author Chris
 * @version 1.3
 */
public class Recorder {
    private final int level;
    private final List<GameState> gameStates;
    private GameState prevState;

    /**
     * Create a new recorder.
     *
     * @param lvl the level to record
     * @param time the time of the game
     */
    public Recorder(int lvl, int time) {
        level = lvl;
        gameStates = new ArrayList<>();
        prevState = new GameState(0, time);
        gameStates.add(prevState);
    }

    /**
     * Add an action to the recorder.
     *
     * @param action The action to add.
     * @param time The time of the action.
     */
    public void addAction(Action action, int time) {
        if (prevState.getTime() != time) {
            prevState = new GameState(gameStates.size(), time);
            gameStates.add(prevState);
        }
        prevState.addAction(action);
    }

    /**
     * Save the recorded actions to a xml file.
     */
    public void save() {
        Document doc = DocumentHelper.createDocument();
        Element root = doc.addElement("game").addAttribute("level", String.valueOf(level));
        gameStates.forEach(state -> root.add(state.toxml()));

        String filename = System.getProperty("user.dir") + "/resources/recordings/"
            + LocalDateTime.now().toString().replace(":", "-") + ".chaps.xml";
        try (FileWriter out = new FileWriter(filename, StandardCharsets.UTF_8)) {
            (new XMLWriter(out, OutputFormat.createPrettyPrint())).write(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
