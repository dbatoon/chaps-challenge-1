package nz.ac.vuw.ecs.swen225.gp22.domain;

/**
 * Represents a tile that will transition to the next level
 * when the player walks onto the tile.
 *
 * @author Abdul
 * @version 1.4
 */
public class Exit extends Tile {
    /**
     * Default constructor, sets the position the tile, and
     * obstructiveness to false.
     *
     * @param tilePos Point to set the position field to.
     */
    public Exit(Maze.Point tilePos) {
        super(tilePos, false);
    }

    @Override
    public String toString() {
        return "E";
    }
}
