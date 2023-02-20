package nz.ac.vuw.ecs.swen225.gp22.domain;

/**
 * Template for tiles that has fields and methods that all tiles will need.
 *
 * @author Abdul
 * @version 1.4
 */
public abstract class Tile {
    /**
     * The position of the tile on the tilemap.
     */
    private Maze.Point tilePos;

    /**
     * This represents whether or not an Entity can walk onto the tile.
     */
    private boolean obstructive;

    /**
     * Default constructor, sets the position and obstructiveness of the tile.
     *
     * @param tilePos     Point to set the position field to.
     * @param obstructive Boolean to set the obstructive field to.
     */
    public Tile(Maze.Point tilePos, boolean obstructive) {
        setPos(tilePos);
        this.obstructive = obstructive;
    }

    /**
     * @return The position of the tile.
     */
    public Maze.Point getPos() {
        return tilePos;
    }

    /**
     * @return The obstructiveness of the tile.
     */
    public boolean isObstructive() {
        return obstructive;
    }

    /**
     * Sets the position of the tile.
     *
     * @param pos Point that represents the tile's new position.
     */
    public void setPos(Maze.Point pos) {
        if (pos == null || !pos.isValid()) throw new IllegalArgumentException("Invalid point given.");
        tilePos = pos;
    }

    /**
     * Updates the obstructiveness of this tile.
     *
     * @param obstructive The new value of the obstructive field.
     */
    public void setObstructive(boolean obstructive) {
        this.obstructive = obstructive;
    }

    /**
     * Called when a tile is removed or replaced.
     * This method is not abstract because not all tiles
     * will need special code to run on deletion.
     */
    public void deleteTile() {
    }
}
