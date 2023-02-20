package nz.ac.vuw.ecs.swen225.gp22.recorder;

import nz.ac.vuw.ecs.swen225.gp22.app.Base;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * The game state class to store the game state.
 *
 * @author Chris
 * @version 1.5
 */
public class GameState {
    private final int id;
    private final int time;
    private final List<Action> actions;

    /**
     * Create a new game state.
     *
     * @param id   the id of the game state
     * @param time the time of the game state
     */
    protected GameState(int id, int time) {
        this.id = id;
        this.time = time;
        this.actions = new ArrayList<>();
    }

    /**
     * Add an action to the game state.
     *
     * @param id      the id of the action
     * @param time    the time of the action
     * @param actions the actions to add
     */
    protected GameState(int id, int time, List<Action> actions) {
        this.id = id;
        this.time = time;
        this.actions = actions;
    }

    /**
     * Add an action to the game state.
     *
     * @param action The action to add.
     */
    protected void addAction(Action action) {
        actions.add(action);
    }

    /**
     * Get the time of the game state.
     *
     * @return The time of the game state.
     */
    public int getTime() {
        return time;
    }

    /**
     * Apply this game state.
     *
     * @param base the base to call the apply method on
     */
    public void apply(Base base) {
        base.apply(actions);
    }

    /**
     * Undo this game state.
     *
     * @param base the base to call the undo method on
     */
    public void undo(Base base) {
        base.undo(actions);
    }

    /**
     * Convert the game state to xml.
     *
     * @return The xml element of the game state.
     */
    public Element toxml() {
        Element element = DocumentHelper.createElement("state")
            .addAttribute("id", String.valueOf(id))
            .addAttribute("time", String.valueOf(time));
        actions.forEach(action -> element.add(action.toxml()));
        return element;
    }
}
