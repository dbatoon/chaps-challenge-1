package nz.ac.vuw.ecs.swen225.gp22.domain;

/**
 * Represents a tile that makes the player jump by 2 tiles
 * in a direction.
 *
 * @author Abdul
 * @version 1.2
 */
public class BouncyPad extends Tile {
    /**
     * Used for tile functionality that depends on the player.
     */
    private final Observer<Player> playerObserver;

    /**
     * The direction the player will get bounced in.
     */
    private final Entity.Direction dir;

    /**
     * Default constructor, sets the position of the tile,
     * obstructiveness to false, and the direction of the bounce pad.
     *
     * @param tilePos Point to set the position field to.
     * @param dir     The Direction the player will get bounced in.
     */
    public BouncyPad(Maze.Point tilePos, Entity.Direction dir) {
        super(tilePos, false);
        this.dir = dir;

        playerObserver = player -> {
            if (player.getPos().equals(tilePos)) {
                player.setPos(tilePos.add(dir).add(dir));
                player.setDir(dir);
            }
        };
        Maze.player.addObserver(playerObserver);
    }

    /**
     * @return The Direction the player will get bounced in.
     */
    public Entity.Direction getDir() {
        return dir;
    }

    @Override
    public void deleteTile() {
        Maze.player.removeObserver(playerObserver);
    }

    @Override
    public String toString() {
        return "B";
    }
}
