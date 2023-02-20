package nz.ac.vuw.ecs.swen225.gp22.domain;

import nz.ac.vuw.ecs.swen225.gp22.domain.Entity.Action.Interaction;

/**
 * Represents a tile which the player can only walk
 * on if they have a key of the correct color.
 *
 * @author Abdul
 * @version 1.5
 */
public class LockedDoor extends ColorableTile {
    /**
     * Used for tile functionality that depends on the player.
     */
    private final Observer<Player> playerObserver;

    /**
     * Default constructor, sets the position and color of the tile, and
     * obstructiveness to true. An observer is also added to the player so
     * that the obstructiveness is updated based on the player's keys. The same
     * observer makes sure that when they are on this tile, it is reset and the
     * key is consumed from the player's inventory.
     *
     * @param tilePos Point to set the position field to.
     * @param color   Color to set the color field to.
     */
    public LockedDoor(Maze.Point tilePos, Color color) {
        super(tilePos, true, color);

        playerObserver = player -> {
            setObstructive(!player.hasKey(color));
            if (Maze.getTile(tilePos) == this && player.getPos().equals(tilePos)) {
                Maze.resetTile(tilePos);
                Maze.player.consumeKey(color);
                Maze.unclaimedInteractions.offer(new Interaction(Interaction.ActionType.UnlockDoor, color));
            }
        };
        Maze.player.addObserver(playerObserver);
    }

    @Override
    public void deleteTile() {
        Maze.player.removeObserver(playerObserver);
    }

    @Override
    public String toString() {
        return "D";
    }
}
