package nz.ac.vuw.ecs.swen225.gp22.domain;

import nz.ac.vuw.ecs.swen225.gp22.domain.Entity.Action.Interaction;

/**
 * Represents a tile with a treasure on it, which the player
 * can pick up by walking onto the tile.
 *
 * @author Abdul
 * @version 1.4
 */
public class Treasure extends Tile {
    /**
     * Used for tile functionality that depends on the player.
     */
    private final Observer<Player> playerObserver;

    /**
     * Default constructor, sets the position the tile, and
     * obstructiveness to false. An observer is also added to the player so
     * that when they are on this tile, it is reset and the treasure counter
     * is updated.
     *
     * @param tilePos Point to set the position field to.
     */
    public Treasure(Maze.Point tilePos) {
        super(tilePos, false);

        playerObserver = player -> {
            if (Maze.getTile(tilePos) == this && player.getPos().equals(tilePos)) {
                Maze.collectTreasure();
                Maze.resetTile(tilePos);
                Maze.unclaimedInteractions.offer(new Interaction(Interaction.ActionType.PickupTreasure, ColorableTile.Color.None));
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
        return "T";
    }
}
