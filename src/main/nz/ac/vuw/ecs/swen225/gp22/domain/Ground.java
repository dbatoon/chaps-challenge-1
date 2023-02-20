package nz.ac.vuw.ecs.swen225.gp22.domain;

/**
 * Represents an empty tile.
 *
 * @author Abdul
 * @version 1.4
 */
public class Ground extends Tile {
    /**
     * Default constructor, sets the position of the tile,
     * and obstructiveness to false.
     *
     * @param tilePos Point to set the position field to.
     */
    public Ground(Maze.Point tilePos) {
        super(tilePos, false);
    }

    @Override
    public String toString() {
        return "G";
    }
}
