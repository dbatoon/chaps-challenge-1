package nz.ac.vuw.ecs.swen225.gp22.domain;

import nz.ac.vuw.ecs.swen225.gp22.domain.Entity.Action.Interaction;

/**
 * Represents a tile which the player can only walk
 * on if they have collected all the treasures.
 *
 * @author Abdul
 * @version 1.5
 */
public class LockedExit extends Tile {
    /**
     * Used for tile functionality that depends on the player.
     */
    private final Observer<Player> playerObserver;

    /**
     * Default constructor, sets the position and color of the tile, and
     * obstructiveness to true. An observer is also added to the player so
     * that the obstructiveness is updated based on the treasures collected. The same
     * observer makes sure that when they are on this tile, it is reset.
     *
     * @param tilePos Point to set the position field to.
     */
    public LockedExit(Maze.Point tilePos) {
        super(tilePos, true);

        playerObserver = player -> {
            setObstructive(!Maze.collectedAllTreasures());
            if (Maze.getTile(tilePos) == this && player.getPos().equals(tilePos)) {
                Maze.resetTile(tilePos);
                Maze.unclaimedInteractions.offer(new Interaction(Interaction.ActionType.UnlockExit, ColorableTile.Color.None));
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
        return "L";
    }
}
