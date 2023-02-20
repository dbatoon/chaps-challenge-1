package nz.ac.vuw.ecs.swen225.gp22.domain;

import java.util.ArrayList;
import java.util.List;

import nz.ac.vuw.ecs.swen225.gp22.domain.ColorableTile.Color;
import nz.ac.vuw.ecs.swen225.gp22.domain.Entity.Action.Interaction;
import nz.ac.vuw.ecs.swen225.gp22.domain.Entity.Action.Interaction.ActionType;

/**
 * The entity that will be controller by user input, this functions just like
 * a base {@link Entity} but with an inventory to hold keys.
 *
 * @author Abdul
 * @version 1.8
 */
public class Player extends Entity<Player> {
    /**
     * Stores all the keys that the player has.
     */
    private List<ColorableTile.Color> collectedKeys = new ArrayList<>();

    /**
     * Default constructor, sets the position and direction of the player.
     *
     * @param entityPos Point to set the position field to.
     * @param facingDir Direction to set the direction field to.
     */
    public Player(Maze.Point entityPos, Direction facingDir) {
        super(entityPos, facingDir);
    }

    // Does not do anything because the player should not be pinged.
    @Override
    public void ping() {
    }

    @Override
    public void unping() {
    }

    @Override
    public void moveAndTurn(Direction dir) {
        Direction oldDir = getDir();
        Maze.Point oldPos = getPos();
        super.moveAndTurn(dir);

        Action.Interaction interaction = new Interaction(ActionType.None, Color.None);
        if (!Maze.unclaimedInteractions.isEmpty()) interaction = Maze.unclaimedInteractions.poll();
        action = new Action(id(), getPos().subtract(oldPos), oldDir, getDir(), interaction);
    }

    /**
     * Clears all the keys that the player has.
     */
    public void resetItems() {
        collectedKeys.clear();
    }

    /**
     * Adds a key to the player's inventory.
     *
     * @param color Color of the key.
     */
    public void addKey(ColorableTile.Color color) {
        if (color == null || color == ColorableTile.Color.None)
            throw new IllegalArgumentException("Given color is null.");
        int oldKeyCount = Maze.player.keyCount();
        collectedKeys.add(color);
        assert oldKeyCount + 1 == Maze.player.keyCount() && Maze.player.hasKey(color) : "Key was not added to inventory.";
        updateObservers();
    }

    /**
     * Consumes a key from the player's inventory.
     *
     * @param color Color of the key.
     */
    public void consumeKey(ColorableTile.Color color) {
        if (color == null || color == ColorableTile.Color.None)
            throw new IllegalArgumentException("Given color is null.");
        if (!collectedKeys.contains(color)) throw new IllegalArgumentException("Player does not have this key.");
        int oldKeyCount = Maze.player.keyCount();
        collectedKeys.remove(color);
        assert oldKeyCount - 1 == Maze.player.keyCount() : "Key was not consumed.";
        updateObservers();
    }

    /**
     * Checks if the player has a key of a certain color.
     *
     * @param color Color of the key.
     * @return Whether or not the key is in the player's inventory.
     */
    public boolean hasKey(ColorableTile.Color color) {
        return collectedKeys.contains(color);
    }

    /**
     * @return The number of keys the player has.
     */
    public int keyCount() {
        return collectedKeys.size();
    }

    /**
     * @return An immutable list of the keys collected.
     */
    public List<ColorableTile.Color> getAllKeys() {
        return collectedKeys.stream().toList();
    }
}
