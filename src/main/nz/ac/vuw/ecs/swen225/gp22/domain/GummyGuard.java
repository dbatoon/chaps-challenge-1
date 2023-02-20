package nz.ac.vuw.ecs.swen225.gp22.domain;

/**
 * A basic enemy that moves back and forth.
 * Is not used by the game, as we moved on to
 * using jar files for enemies.
 *
 * @author Abdul
 * @version 1.7
 */
@Deprecated
public class GummyGuard extends EnemyEntity<GummyGuard> {
    /**
     * Used for if the player walks into the enemy.
     */
    private final Observer<Player> playerObserver;

    /**
     * Default constructor, sets the position, direction, and speed of the enemy.
     *
     * @param entityPos Point to set the position field to.
     * @param facingDir Direction to set the direction field to.
     */
    public GummyGuard(Maze.Point entityPos, Direction facingDir) {
        super(entityPos, facingDir, 400);

        playerObserver = player -> {
            if (player.getPos().equals(getPos())) Maze.loseGame();
        };
        Maze.player.addObserver(playerObserver);
    }

    @Override
    public void ping() {
        Direction oldDir = getDir();
        Maze.Point oldPos = getPos();
        moveAndTurn(getDir());
        if (Maze.getTile(getPos().add(getDir())).isObstructive()) setDir(getDir().opposite());
        if (Maze.player.getPos().equals(getPos())) Maze.loseGame();
        action = new Action(id(), getPos().subtract(oldPos), oldDir, getDir(), new Action.Interaction(Action.Interaction.ActionType.Pinged, ColorableTile.Color.None));
    }

    @Override
    public void unping() {
        if (Maze.getTile(getPos().subtract(getDir())).isObstructive()) setDir(getDir().opposite());
        move(getDir().opposite());
        ;
        if (Maze.player.getPos().equals(getPos())) Maze.loseGame();
    }

    @Override
    public void deleteEntity() {
        Maze.player.removeObserver(playerObserver);
    }
}