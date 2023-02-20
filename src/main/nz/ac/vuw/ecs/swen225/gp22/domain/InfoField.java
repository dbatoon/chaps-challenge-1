package nz.ac.vuw.ecs.swen225.gp22.domain;

/**
 * Represents a tile which will give the player
 * additional information if they are on it.
 *
 * @author Abdul
 * @version 1.4
 */
public class InfoField extends Tile {
    /**
     * The text that will be displayed if the player is on this tile.
     */
    private final String infoText;

    /**
     * Default constructor, sets the position of the tile,
     * and obstructiveness to false.
     *
     * @param tilePos Point to set the position field to.
     * @param infoText The text that will be displayed.
     */
    public InfoField(Maze.Point tilePos, String infoText) {
        super(tilePos, false);
        if (infoText == null) throw new IllegalArgumentException("Given text is null.");
        this.infoText = infoText;
    }

    /**
     * @return The info text of this tile.
     */
    public String getText() {
        return infoText;
    }

    @Override
    public String toString() {
        return "I";
    }
}
