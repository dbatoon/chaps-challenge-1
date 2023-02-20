package nz.ac.vuw.ecs.swen225.gp22.domain;

/**
 * Represents a tile that kills the player.
 *
 * @author Abdul
 * @version 1.1
 */
public class MilkPuddle extends Tile {
    /**
     * Used for tile functionality that depends on the player.
     */
    private final Observer<Player> playerObserver;

    /**
     * Default constructor, sets the position of the tile,
     * and obstructiveness to false.
     *
     * @param tilePos Point to set the position field to.
     */
    public MilkPuddle(Maze.Point tilePos) {
        super(tilePos, false);

        playerObserver = player -> {
            if (player.getPos().equals(tilePos)) {
                Maze.loseGame();
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
        return "M";
    }
}
